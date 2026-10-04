# 消息幂等（clientMessageId）实施计划

## 一、当前开发阶段与目标

- **阶段**：实现阶段（方案已与用户评审通过，本文档为施工依据）。
- **目标**：前端为「发送聊天消息」新增 `clientMessageId`（`crypto.randomUUID()` 生成，全局唯一），后端接通幂等链路，保证**同一 `clientMessageId` 只落库一条、只推送一次**；并为媒体消息提供「免重复上传」的信号以节省用户流量。

## 二、改造前现状（已调研确认）

| 项 | 结论 |
| --- | --- |
| 数据库 | [V2__reliability.sql](file:///c:/Users/Administrator/easy-chat-java/src/main/resources/db/migration/V2__reliability.sql#L19-L28) **已包含** `client_message_id VARCHAR(64) NULL` 与 `UNIQUE INDEX uk_client_msg_id`，但尚未执行（Flyway 待迁移） |
| 写入口 | 全链路**唯一**写入口：`ChatController.sendMessage` → `ChatMessageServiceImpl.saveMessage`（消息先落库，再经 `MessageHandler` → Redis Pub/Sub → `ChannelContextUtils` 推送） |
| 事务现状 | `ChatMessageServiceImpl` 类上仅 `@Service("chatMessageService")`（L45），import 中**无** `org.springframework.transaction.annotation.Transactional`，全类无事务 |
| 已知缺陷 1 | `saveMessageFile` L261-263 构造了带 `status=SENDING` 条件的 `messageQuery`，但 L264 实际调用 `updateByMessageId(uploadInfo, messageId)`，**条件未被使用**（死代码）→ 重复上传会把状态重复推进并重复推送 |
| 已知缺陷 2 | `saveMessageFile` L267-272 推送的 `MessageSendDto` 只塞了 `status/fileName/fileType/contactId`，**缺 `messageId`/`sessionId`**，前端无法定位是哪条消息 |
| 媒体消息机制 | `messageType=MEDIA_CHAT` 时 `saveMessage` 落库并置 `status=SENDING(0)`（L165），随后由 `/chat/uploadFile` 上传文件并把状态置 `SENDED(1)`。文件名为 `messageId + 后缀`（L243），封面再加 `COVER_IMAGE_SUFFIX`（L252）→ 重复上传是**覆盖同一个文件**（文件不会重复，但**白耗传输流量**） |

## 三、涉及组件

| 类型 | 文件 | 改动性质 |
| --- | --- | --- |
| PO | `src/main/java/com/easychat/entity/po/ChatMessage.java` | 新增字段 |
| DTO | `src/main/java/com/easychat/entity/dto/MessageSendDto.java` | 新增字段 |
| Controller | `src/main/java/com/easychat/controller/ChatController.java` | 新增入参 |
| Mapper 接口 | `src/main/java/com/easychat/mapper/ChatMessageMapper.java` | 新增查询/条件更新方法 |
| Mapper XML | `src/main/resources/mapper/ChatMessageMapper.xml` | 列映射、insert 增列、新增语句 |
| Service 实现 | `src/main/java/com/easychat/service/impl/ChatMessageServiceImpl.java` | **核心**：幂等编排 + 上传闸门 |
| 数据库 | `V2__reliability.sql` | 无需改（列与唯一索引已就绪），需执行 |

## 四、技术选型与关键决策

### 4.1 事务边界：自注入 + `REQUIRES_NEW`（不新增独立 Bean）

用户明确要求不新增 Bean，改用自注入：

```java
@Autowired
@Lazy
private ChatMessageServiceImpl self;   // 拿到的是代理对象
```

三个前提（缺一不可）：

1. 必须 `self.xxx()`，不能用 `this.xxx()` —— `this` 是原始对象，绕不过代理，事务静默失效。
2. 事务方法必须 `public`。**放在实现类上、不写进 `ChatMessageService` 接口**，以保持接口契约干净；这依赖 Spring Boot 默认 `proxy-target-class=true`（CGLIB 子类代理），需在启动后实测确认。
3. 保留 `@Lazy`：自注入本质是自引用，`@Lazy` 注入懒代理，绕开实例化顺序与 `allow-circular-references` 默认值的影响。

**风险与兜底**：自注入把「本类存在自调用」变成隐式约定，后人若把事务方法改 private 或改用 `this`，事务会**静默失效**。因此**不能只靠代码写对**，必须用并发重复发送的集成测试证明边界生效（见第八节）。

### 4.2 幂等主策略：预检 + 唯一索引兜底 + 新事务回查

```
① clientMessageId 非空 → selectByClientMessageId
     命中 → 直接返回既有消息（含其真实 status），不落库、不推送
② 未命中 → self.saveMessageInNewTx(...)   @Transactional(REQUIRES_NEW)
     内部：insert(chatMessage) + 更新 chat_session.lastMessage
③ 若抛 DuplicateKeyException → 在新事务中按 clientMessageId 回查 → 返回既有消息（不推送）
```

**为什么回查必须在新事务/新连接中**（两个理由，第二个才是关键）：

1. 同一事务内 insert 失败后事务已被标记 rollback-only，继续查询会抛 `UnexpectedRollbackException`；
2. MySQL 默认 REPEATABLE READ，事务快照在**第一次读**时建立。若并发那条记录是在本事务预检**之后**才提交的，则**在同一事务内回查依然看不到它** —— 只有新事务才能看到已提交的行。

### 4.3 明确排除的方案：`INSERT ... ON DUPLICATE KEY UPDATE`

可用 `ON DUPLICATE KEY UPDATE message_id = LAST_INSERT_ID(message_id)` 单语句拿回 id，再靠「受影响行数」区分新建/命中。**不采用**，原因：该信号依赖驱动参数（Connector/J `useAffectedRows` 默认 `false`，会带上 `CLIENT_FOUND_ROWS`），语义不直观且必须实测才能确认，不如 4.2 语义明确的组合可靠。

### 4.4 媒体消息上传闸门：条件更新 + 影响行数

```sql
UPDATE chat_message SET status = 1, file_size = ?, file_type = ?
 WHERE message_id = ? AND status = 0
```

- 影响行数 `= 1` → 首次完成上传 → 正常推送
- 影响行数 `= 0` → 已被前一次重试置为 SENDED → **判重，跳过推送**

**为什么这里的「影响行数」可靠，而 4.3 的不可靠**：本语句 `WHERE` 带 `status=0`、`SET` 又把它改成 `1`，「匹配行」与「实际变更行」必然一致，因此即使驱动设置了 `CLIENT_FOUND_ROWS`，返回结果也不变。`INSERT ... ON DUPLICATE KEY UPDATE` 不具备这一性质。

### 4.5 省流量：入站流量只能在客户端省

HTTP multipart 请求体在进入 Controller 之前就已被 Spring 解析完成（文件默认先落盘临时文件），**服务端任何「提前 return」都省不了入站字节**。真正的省流量动作是「客户端根本不发起第二次 uploadFile」。后端只需配合一件事：

> **幂等命中时，`/chat/sendMessage` 返回库里那条既有记录的当前 `status`，而不是新造的对象。**

`MessageSendDto.status` 字段已存在且无 `@JsonIgnore`，天然会序列化返回。前端据此判断：

| 重试返回的 status | 前端动作 |
| --- | --- |
| `SENDED(1)` | 文件早已上传完成 → **跳过 `/chat/uploadFile`，整包流量省掉** |
| `SENDING(0)` | 上次上传未完成 → 继续上传 |

## 五、逐文件实施策略

### 5.1 `ChatMessage.java`
新增 `private String clientMessageId;` + getter/setter，并补进 `toString()`（现 L159-176）。沿用现有格式（制表符缩进、单行 `@Schema(description=...)`）。
> 建议加 `@JsonIgnore`？**不加**：该字段需要随推送下发（保持与 `sessionId` 等字段一致的可序列化性）。

### 5.2 `MessageSendDto.java`
新增 `private String clientMessageId;` + getter/setter（L11-61 区域）。
`MessageSendDto` 是**双职责**类：既承载入参信息，又是落库后经 `CopyTools.copy(chatMessage, MessageSendDto.class)`（L183）推送给接收方的对象。加上后发送方能拿到自己那条消息的 `clientMessageId`，用于把「本地乐观消息」与「服务端确认消息」对上。

### 5.3 `ChatController.sendMessage`
在 L53-58 参数区新增：
```java
@Parameter(description = "客户端幂等ID（UUID，用于消息去重）") @Size(max = 64) String clientMessageId
```
并 `chatMessage.setClientMessageId(clientMessageId)`。
**必须可选**（不加 `@NotEmpty`）：老客户端不传 → `null` → 完整走原逻辑，保持向后兼容。`@Size(max=64)` 对齐列宽，UUID 36 位满足。

### 5.4 `ChatMessageMapper.java`
新增两个方法：
```java
ChatMessage selectByClientMessageId(String clientMessageId);
Integer updateStatusByMessageIdAndStatus(ChatMessage bean, Long messageId, Byte fromStatus);
```

### 5.5 `ChatMessageMapper.xml`
| 位置 | 改动 |
| --- | --- |
| `base_result_map`（L5-33） | 加 `<result column="client_message_id" property="clientMessageId"/>` |
| `base_column_list`（L35-39） | 加 `client_message_id` |
| `insert`（L142-229） | 加列与值（含 `<trim>` 的 if 分支） |
| 新增 `selectByClientMessageId` | 按唯一索引查询，复用 `base_column_list` |
| 新增 `updateStatusByMessageIdAndStatus` | 条件更新，返回 `int` 影响行数 |

`insertOrUpdate`/`insertBatch`/`insertOrUpdateBatch` 等生成器产物**不在发送链路上**，本次不动（如需一致性可后续统一补列）。

### 5.6 `ChatMessageServiceImpl.java`（核心）

**自注入字段**（与现有 `@Resource` 字段区 L49-60 并列）：
```java
/** 自注入代理，用于让 saveMessageInNewTx 的事务注解生效（避免自调用绕过代理） */
@Autowired @Lazy
private ChatMessageServiceImpl self;
```

**`saveMessage` 编排改造**（现 L128-200）：
1. 保留原有联系人校验（L130-145）、sessionId 计算（L147-156）、内容清洗（L168-169）、消息类型校验（L161-164）—— 全部前置，避免非法请求借着幂等通道绕过校验。
2. **幂等预检**：`!StringTools.isEmpty(clientMessageId)` 时先 `selectByClientMessageId`，命中则 `CopyTools.copy` 返回既有消息并**直接 return**（不落库、不推送、不更新会话）。
3. 未命中 → 调 `self.saveMessageInNewTx(chatMessage, sessionId, messageContent)`（新事务内：`insert` + 更新 `chat_session.lastMessage`）。
4. 捕获 `DuplicateKeyException` → 新事务回查 `selectByClientMessageId` → 返回既有消息（不推送）；回查仍为 null 则原样抛出（避免吞异常）。
5. **推送移出事务**：`messageHandler.sendMessage(messageSendDto)` 在落库提交之后调用。
6. 机器人递归调用（L185-197）保持不变 —— 它由服务端生成、`clientMessageId` 为 `null`，会自然跳过幂等分支（MySQL 唯一索引允许多个 NULL）。

**新增事务方法**：
```java
@Transactional(propagation = Propagation.REQUIRES_NEW)
public void saveMessageInNewTx(ChatMessage chatMessage, String sessionId, String lastMessage) {
    chatMessageMapper.insert(chatMessage);
    ChatSession chatSession = new ChatSession();
    chatSession.setLastMessage(lastMessage);
    chatSession.setLastReceiveTime(chatMessage.getSendTime());
    chatSessionMapper.updateBySessionId(chatSession, sessionId);
}
```
（注意保留 L173-175 的群聊 `昵称 + ": "` 前缀逻辑。）

**`saveMessageFile` 改造**（现 L210-273）：
1. **`transferTo` 之前短路**（在 L249-251 之前）：查一次 status，若已 `SENDED` 则视为重复上传，`logger.info` 后直接 `return` → 省下磁盘写入与覆盖 IO。
2. **条件更新闸门**替换 L258-264：调用 `updateStatusByMessageIdAndStatus(uploadInfo, messageId, SENDING)`，按影响行数判定：
   - `= 1` → 首次完成 → 继续推送
   - `= 0` → 判重 → `return`，**不推送**
3. **推送补全字段**（L267-272）：补 `messageId`、`sessionId`、`clientMessageId`，让前端能"按 messageId 覆盖"而不是新增气泡。

### 5.7 前端契约（需同步给前端）
- 发送消息请求体新增 `clientMessageId`（UUID 字符串，可空）。
- 重试发送拿到响应后，若 `status == 1 (SENDED)` → **跳过 `/chat/uploadFile`**。
- 上传完成推送新增 `messageId`，前端按 `messageId` 做「覆盖/确认」而非「追加」。
- 本地乐观消息与 `clientMessageId` 建立映射，收到确认后替换为服务端 `messageId`。

## 六、状态管理与组件编排

```
ChatController.sendMessage(clientMessageId)
   └─ ChatMessageServiceImpl.saveMessage        [无事务：幂等编排层]
        ├─ 校验联系人 / 算 sessionId / 清洗内容 / 校验消息类型
        ├─ selectByClientMessageId  ──命中──▶ 返回既有消息(含真实status)  ──┐
        │                                                                │
        └─ self.saveMessageInNewTx(...)   [REQUIRES_NEW 事务]            │
              ├─ insert(chat_message)                                    │
              └─ update(chat_session.lastMessage)                        │
                    │提交成功                                            │
                    ├─▶ messageHandler.sendMessage(dto)  [事务外推送]     │
                    │                                                   │
                    └─抛 DuplicateKeyException ─▶ 新事务回查 ────────────┘
```

事务边界原则：**幂等编排在事务外，落库原子性在事务内，推送在提交之后。**

## 七、潜在困难与风险

| 风险 | 对策 |
| --- | --- |
| 自注入未拿到代理（事务静默失效） | 保留 `@Lazy`；用并发重复发送集成测试断言「只落库一条 + 无 `UnexpectedRollbackException`」；必要时打印 `AopUtils.isAopProxy(self)` 自检 |
| 事务方法若不为 `public` 或被改写为 `this` 调用 → 静默失效 | JavaDoc 显式注明「必须 `self.` 调用且保持 `public`」；纳入自查清单 |
| CGLIB 代理假设被破坏（若改为 JDK 动态代理，impl 类型自注入会启动失败） | 启动失败是**显式**的、不会静默；届时改回「自注入接口 + 方法上移接口」即可 |
| 并发下「先查后插」竞态 | 唯一索引 `uk_client_msg_id` 为权威防线，`catch DuplicateKeyException` 兜底 |
| REPEATABLE READ 快照导致同事务回查看不到并发行 | 回查放在新事务（设计已内置，见 4.2） |
| 媒体消息并发重试导致重复推送 | 条件更新 + 影响行数闸门（设计已内置，见 4.4） |
| 唯一索引为**全局**（非按发送人） | `clientMessageId` 为 UUID，全局唯一性成立；V2 脚本已定稿不再改 |
| 老客户端不传 `clientMessageId` | 字段可空，`null` 时完整跳过幂等分支，行为与改造前一致 |
| 端到端省流量仍需前端配合 | 已明确写入前端契约（5.7），后端只负责返回真实 `status` |

## 八、验证方案

1. **迁移**：对临时库执行 Flyway（V1 基线 + V2），确认 `client_message_id` 列与 `uk_client_msg_id` 存在。
2. **编译**：`mvn compile -DskipTests`。
3. **集成测试（临时库，不动正式库 `easychat`）**：
   - 顺序幂等：同一 `clientMessageId` 连续发送两次 → 仅 1 条记录，两次返回**同一 `messageId`**。
   - 并发幂等：多线程同时用同一 `clientMessageId` 发送 → 仅 1 条记录，无异常上抛到接口层。
   - 媒体重试：同一媒体消息重复调用 `uploadFile` → 状态只推进一次、接收方**只收到一次**推送。
   - 事务边界：验证 `REQUIRES_NEW` 确实生效（自注入未失效）。
   - 兼容性：不传 `clientMessageId`（模拟老客户端，含机器人回复路径）→ 行为与改造前一致。
4. **清理**：删除临时库与测试进程，校验正式库未被改动。

## 九、待办清单

- [x] 5.1 `ChatMessage.java` 加字段
- [x] 5.2 `MessageSendDto.java` 加字段
- [x] 5.3 `ChatController.sendMessage` 加入参
- [x] 5.4 `ChatMessageMapper.java` 加方法
- [x] 5.5 `ChatMessageMapper.xml` 加列映射/insert/新语句
- [x] 5.6 `ChatMessageServiceImpl` 幂等编排 + `saveMessageFile` 闸门 + 自注入
- [x] 8. 编译 + 数据库层验证（应用启动/端到端受环境限制未执行，见 10.4）
- [x] 代码自查（注释、异常处理、JavaDoc）
- [x] 补充「完成记录」与本文件验证结果

## 十、完成记录

### 10.1 改动文件（6 个）

| 文件 | 改动 |
| --- | --- |
| `ChatMessage.java` | 新增 `clientMessageId` 字段、getter/setter，并补进 `toString()` |
| `MessageSendDto.java` | 新增 `clientMessageId` 字段、getter/setter |
| `ChatController.java` | `sendMessage` 新增**可选**入参 `clientMessageId`（`@Size(max=64)`）；空串归一化为 `null` 后 set 进 `ChatMessage` |
| `ChatMessageMapper.java` | 新增 `selectByClientMessageId`、`updateStatusByMessageIdAndStatus`（沿用泛型 `T/P` 风格） |
| `ChatMessageMapper.xml` | `base_result_map` / `base_column_list` / `insert` 补 `client_message_id`；新增两条语句 |
| `ChatMessageServiceImpl.java` | 幂等编排 + 自注入 `self` + `saveMessageInNewTx(REQUIRES_NEW)` + `saveMessageFile` 短路与条件更新闸门 + 推送补全字段 |

### 10.2 实现要点

- 事务边界：按用户要求**未新增独立 Bean**，采用 `@Autowired @Lazy private ChatMessageServiceImpl self;`，落库统一走 `self.saveMessageInNewTx(...)`（`REQUIRES_NEW`）；事务方法保持 `public`，仅存在于实现类、不进接口。
- 回查位置：`DuplicateKeyException` 的回查在外层**无事务**方法中执行（Hikari 自动提交），可看到并发已提交行，规避 REPEATABLE READ 快照问题。
- 闸门语句：`updateStatusByMessageIdAndStatus` 的 `<set>` 支持 `status/fileSize/fileName/fileType`，服务层当前只设 `status`（与原行为一致），仅新增 `WHERE status = fromStatus` 条件。
- 已按 4.3 排除 `INSERT ... ON DUPLICATE KEY UPDATE`。
- **空串归一化**：判断统一用 `!StringTools.isEmpty(clientMessageId)`，并在 Controller 层把空串转成 `null`。原因：MySQL 唯一索引把 `''` 视为有效值（多条 `''` 会撞 1062），而 `NULL` 允许多条；若不归一化，前端传空串会破坏老客户端兼容。

### 10.3 已执行并通过的验证

| 项 | 方法 | 结果 |
| --- | --- | --- |
| 编译 | `mvn compile -DskipTests` | ✅ 通过 |
| 迁移 | 临时库 `easychat_it` 应用 V1+V2 | ✅ `client_message_id varchar(64) NULL` + 唯一索引 `uk_client_msg_id` 就位 |
| 唯一索引判重 | 重复插入同一 `client_message_id` | ✅ `ERROR 1062 Duplicate entry`（MyBatis 会转成 `DuplicateKeyException`） |
| 上传闸门 | `UPDATE ... WHERE message_id=? AND status=0` 连做两次 | ✅ 影响行数依次为 1、0（第二次判重跳过推送） |
| 老客户端兼容 | 连续插入两条 `client_message_id = NULL` | ✅ 均成功（唯一索引允许多个 NULL） |
| 空串 vs NULL | 同一唯一索引下插入 `('','')` 与 `(NULL,NULL)` | ✅ `''` 报 `ERROR 1062`、`NULL` 两条均成功 → 证明空串必须归一化为 `NULL` |
| Mapper XML | 良构性 + 语句 ID 检查 | ✅ 良构；两条新语句均已注册 |
| 生产库 | 仅查询、未写入 | ✅ 未改动（V2 早于本会话已应用，见 `flyway_schema_history`） |

### 10.4 未能执行的验证（环境限制）

- **应用启动 / 端到端集成测试未执行**：本机未安装 Redis（6379 未监听），应用无法启动。
- 因此以下两项**尚未在运行时验证**，需在具备 Redis 的环境补测：
  1. `self` 自注入确实拿到 CGLIB 代理、`REQUIRES_NEW` 事务生效（建议并发重复发送，断言「只落库一条 + 无 `UnexpectedRollbackException`」）。
  2. `DuplicateKeyException` 上抛到 `saveMessage` 后被捕获并回查成功（接口层不抛异常）。
- 临时库 `easychat_it` 已删除，无残留。

