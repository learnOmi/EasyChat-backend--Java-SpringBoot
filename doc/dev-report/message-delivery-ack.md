# 单聊消息送达 ACK 实施计划

## 一、当前开发阶段与目标

- **阶段**：实现阶段（方案已与用户评审通过，本文档为施工依据）。
- **目标**：为**单聊**消息增加「送达确认」——接收方客户端收到消息后通过 WebSocket 回一条 ACK，服务端把 `chat_message.status` 推进到 `DELIVERED(2)` 并通知发送方；发送方在线时实时收到状态变更，离线时在重连的 INIT 中补齐自己发出消息的最新状态。
- **范围（已与用户确认）**：只做单聊；只做「送达」不做「已读」；状态直接存 `chat_message.status`；**不新增表、不新增迁移脚本**。

## 二、改造前现状（已调研确认）

| 项 | 结论 |
| --- | --- |
| 推送链路 | `saveMessage` → `MessageHandler.sendMessage`（Redisson topic `message.topic`）→ 监听器 → `ChannelContextUtils.sendMessage` → `send2User`/`send2Group` → `sendMsg` 写 `TextWebSocketFrame`(JSON of `MessageSendDto`) |
| 离线丢弃 | `ChannelContextUtils.sendMsg`：`USER_CONTEXT_MAP.get(reciveId)` 为 null 直接 `return`，不补偿、不留痕 |
| 上行忽略 | `HandlerWebSocket.channelRead0`：只 `saveHeartBeat(userId)`，**完全不解析上行内容** |
| 状态枚举 | `MessageStatusEnum` 仅 `SENDING(0)` / `SENDED(1)` |
| 消息类型 | `MessageTypeEnum` 最大到 13（`ADD_FRIEND_SELF`），无任何 ACK / 状态类 |
| INIT 拉取 | `ChannelContextUtils.addContext`：仅查 `contact_id ∈ {我所在的群, 我的userId}` 且 `send_time > lastOffTime`（窗口被 `MILLLSSECONDS_3DAY` 兜底为 3 天），**不含「我发出的消息」** |
| 序列化 | `ChatMessage` 仅在 `WsInitData.chatMessageList` 一处序列化给前端；`ChatMessage.status` 带 `@JsonIgnore` |
| 群聊送达 | 本次不做（单字段 `status` 无法表达「N 个接收方各自不同状态」） |

## 三、涉及组件

| 类型 | 文件 | 改动性质 |
| --- | --- | --- |
| 枚举 | `enums/MessageStatusEnum.java` | 新增 `DELIVERED(2)` |
| 枚举 | `enums/MessageTypeEnum.java` | 新增 `MESSAGE_ACK(14)`、`MESSAGE_STATUS_CHANGE(15)` |
| DTO | `entity/dto/WsUpstreamDto.java` | **新增**：上行 ACK 载体 |
| Netty | `websocket/netty/HandlerWebSocket.java` | 解析上行并路由 ACK |
| Service 接口 | `service/ChatMessageService.java` | 新增 `ackMessage` |
| Service 实现 | `service/impl/ChatMessageServiceImpl.java` | 新增 `ackMessage` 实现 |
| PO | `entity/po/ChatMessage.java` | 去掉 `status` 的 `@JsonIgnore` |
| WS | `websocket/ChannelContextUtils.java` | `addContext` 增加「我发出的消息」查询 |
| 复用 | `mapper/ChatMessageMapper.java` / `.xml` | **复用** `updateStatusByMessageIdAndStatus`，无需改动 |
| 数据库 | — | **无需迁移**（`status` 是 tinyint，仅加枚举值） |

## 四、关键决策（已确认）

1. **范围**：单聊 only；群聊显式忽略。
2. **存储**：扩展现有 `chat_message.status`，不建关联表。
3. **通道**：ACK 走已有 WebSocket 上行，不新增 HTTP 接口。
4. **不做已读**：状态只推进到 `DELIVERED`。
5. **离线补齐**：扩展 `addContext` 的 INIT 查询，而非新增同步接口（3 天窗口已能满足聊天场景）。

## 五、逐文件实施策略

### 5.1 `MessageStatusEnum`
新增 `DELIVERED(2, "已送达")`。字段注释同步补全为 `0:发送中 1:已发送 2:已送达`。

### 5.2 `MessageTypeEnum`
新增两项（沿用现有 `(type, initMessage, desc)` 格式）：
```java
MESSAGE_ACK(14, "", "消息送达确认（上行）"),
MESSAGE_STATUS_CHANGE(15, "", "消息状态变更通知（下行）");
```

### 5.3 新增 `WsUpstreamDto`
包 `com.easychat.entity.dto`，`implements Serializable`，字段：
- `Integer messageType`（上行消息类型）
- `Long messageId`（消息ID）

加 `@Schema` 字段说明、`@JsonIgnoreProperties(ignoreUnknown = true)` 容错、getter/setter。

### 5.4 `HandlerWebSocket.channelRead0`
保持 `redisComponent.saveHeartBeat(userId)`；在其后新增：
- 整个「解析 + 路由」包在 `try/catch` 中，异常只记日志，**绝不能把 WS 连接搞挂**。
- 注意 `JsonUtils.convertJson2Obj` 内部会把解析异常包装成 `BusinessException` 抛出，所以 try 必须能接住它。
- 注入 `ChatMessageService`。
- 路由：`MessageTypeEnum.MESSAGE_ACK == MessageTypeEnum.getByType(dto.getMessageType())` → `chatMessageService.ackMessage(userId, dto.getMessageId())`。
- `userId` 取自 channel attribute（现有代码已能取到）。

### 5.5 `ChatMessageService` + Impl：`ackMessage`

接口：`void ackMessage(String userId, Long messageId);`

实现（`ChatMessageServiceImpl`）：
1. `userId` 为空或 `messageId == null` → 记日志 return。
2. `ChatMessage msg = chatMessageMapper.selectByMessageId(messageId);`，`null` → 记日志 return。
3. **防伪造校验**：`msg.getContactType() != null && msg.getContactType() == UserContactTypeEnum.USER.getType().byteValue()` 且 `userId.equals(msg.getContactId())`；不满足 → 记日志 return（群聊本次直接忽略）。
4. **条件更新（幂等闸门）**：
```java
ChatMessage updateBean = new ChatMessage();
updateBean.setStatus(MessageStatusEnum.DELIVERED.getStatus().byteValue());
Integer affected = chatMessageMapper.updateStatusByMessageIdAndStatus(
        updateBean, messageId, MessageStatusEnum.SENDED.getStatus().byteValue());
if (affected == null || affected == 0) { logger.info(...); return; } // 已 ACK 过
```
5. **推状态变更给发送方**：
```java
MessageSendDto dto = new MessageSendDto();
dto.setMessageType(MessageTypeEnum.MESSAGE_STATUS_CHANGE.getType());
dto.setMessageId(messageId);
dto.setSessionId(msg.getSessionId());
dto.setStatus(MessageStatusEnum.DELIVERED.getStatus());
dto.setSendUserId(userId);              // ACK 的人（接收方）
dto.setContactId(msg.getSendUserId());  // 路由目标：发送方
messageHandler.sendMessage(dto);
```
> **易错点**：`ChannelContextUtils.sendMsg` 内部会把 `contactId` 覆盖成 `sendUserId` 再写回前端。所以路由靠 `contactId`（填发送方），展示用的「联系人」是接收方——两个字段别填反。

### 5.6 `ChatMessage`：去掉 `status` 的 `@JsonIgnore`
目的：让 INIT 的 `chatMessageList` 能带出 `status`，使发送方重连后能看到已变为 `DELIVERED` 的自己发出的消息。已确认 `ChatMessage` 仅在此一处序列化给前端，移除安全。

### 5.7 `ChannelContextUtils.addContext`：INIT 增加「我发出的消息」
在现有 `chatMessageList` 查询之后追加第二段查询并合并：
```java
ChatMessageQuery sentQuery = new ChatMessageQuery();
sentQuery.setSendUserId(userId);
sentQuery.setLastReceiveTime(lastOffTime);
List<ChatMessage> sentList = chatMessageMapper.selectList(sentQuery);
```
合并 → **按 `messageId` 去重** → **按 `sendTime` 升序排序** → `wsInitData.setChatMessageList(...)`。
- **去重原因**：我发出的群消息会同时命中两段（`contact_id = 群` 命中第一段、`send_user_id = 我` 命中第二段）。
- **排序原因**：前端按列表顺序渲染，两段查询各自有序、合并后需重排。

## 六、状态与编排

**状态机**：
```
0 SENDING ──(媒体上传完成)──▶ 1 SENDED ──(接收方 ACK)──▶ 2 DELIVERED
```

**时序（A 发送、B 接收）**：
```
A: sendMessage → 落库 status=1 → 推送(B)
├─ B 在线 : 收到 → 回 MESSAGE_ACK → ackMessage(1→2) → 推 MESSAGE_STATUS_CHANGE 给 A → A 实时更新气泡
└─ B 离线 : 消息留库 status=1 → B 上线 INIT 拉到 → 回 ACK → 1→2
                                    ├─ A 在线 : 实时收到状态变更
                                    └─ A 离线 : 推送被丢弃 → A 重连取 INIT（含「我发出的消息」）看到 status=2
```

## 七、离线场景分析（两个方向）

### 7.1 接收方 B 离线
- 消息已落库 `status=SENDED`，推送被 `sendMsg` 丢弃（正常，无需补偿——数据在库里）。
- B 上线 → `addContext` 通过 INIT 把消息拉给 B。
- **前提（写进前端契约）**：B 必须对 INIT 中「发给自己的、`status=SENDED`」的消息逐条回 ACK，否则这批离线消息永远停在 SENDED。服务端 `ackMessage` 幂等，重复 ACK 无害。

### 7.2 发送方 A 离线（本次修复的核心漏洞）
- B 的 ACK 到达 → 改 `DELIVERED` → 推状态变更给 A → `sendMsg` 发现 A 的 channel 为 null → **丢弃**。
- 原 INIT 查询只查 `contact_id ∈ {群, 我的userId}`，即**只有别人发给 A 的消息**；**A 自己发出的消息（`contact_id = B`）根本不在结果集里**。
- 因此若不修，A 离线期间被送达的消息，实时收不到、重连也查不到，**状态永远停在 SENDED**。
- **修复**：5.7 的第二段查询（`send_user_id = A`），使 A 重连能看到自己发出消息的最新 `status`。

## 八、风险与对策

| 风险 | 对策 |
| --- | --- |
| 伪造 ACK | `contactType=USER` 且 `contactId == userId` 双重校验 |
| 重复 / 延迟 ACK | 条件更新 `SENDED→DELIVERED`，影响行数 0 即跳过（幂等），同时兜住「很久之后才到的 ACK」 |
| ACK 帧丢失 | B 每次上线对 INIT 中 `status=SENDED` 的消息重发 ACK；服务端幂等 |
| Netty IO 线程执行 DB 操作 | 沿用现状（`addContext` 亦如此），本次不引入业务线程池；记为已知隐患 |
| 群聊 | 本次显式忽略，等关联表方案 |
| INIT 数据量 / 重复 | 仍受 3 天窗口约束；按 `messageId` 去重、按 `sendTime` 排序 |
| 前端状态乱序 | 排序保证 |

## 九、待办清单

- [ ] 5.1 `MessageStatusEnum` 加 `DELIVERED(2)`
- [ ] 5.2 `MessageTypeEnum` 加 `MESSAGE_ACK(14)` / `MESSAGE_STATUS_CHANGE(15)`
- [ ] 5.3 新增 `WsUpstreamDto`
- [ ] 5.4 `HandlerWebSocket.channelRead0` 上行路由
- [ ] 5.5 `ChatMessageService` / Impl 新增 `ackMessage`
- [ ] 5.6 `ChatMessage` 去掉 `status` 的 `@JsonIgnore`
- [ ] 5.7 `ChannelContextUtils.addContext` INIT 补「我发出的消息」
- [ ] `mvn compile` 编译验证
- [ ] 数据库层验证（条件更新 `SENDED→DELIVERED` 影响行数 1→0）
- [ ] 代码自查 + 回填「完成记录」与验证结果

## 十、验证方案

- **编译**：`mvn compile -DskipTests`。
- **数据库层**（临时库，**不得改动正式库 `easychat`**）：应用 V1+V2 后，造一条 `status=1` 的消息，执行
  `UPDATE chat_message SET status=2 WHERE message_id=? AND status=1` 两次，断言影响行数依次为 `1`、`0`。
- **应用启动 / 端到端**：本机未安装 Redis（6379 未监听），应用无法启动；与前次一致，记为「未执行」，需在具备 Redis 的环境补测自注入/WS 上行/离线补齐。
- **静态核对**：`HandlerWebSocket` 解析与异常兜底、`ackMessage` 的校验与幂等、INIT 合并去重排序。

## 十一、前端契约

1. **回 ACK**：收到 WS 消息（实时推送 + INIT 批量，两条路径都要），凡 `contactId == 自己` 且 `status == 1(SENDED)` 的，回 `{"messageType":14,"messageId":<消息ID>}`。
2. **处理状态变更**：收到 `messageType == 15(MESSAGE_STATUS_CHANGE)` 时，按 `messageId` 把对应气泡置为「已送达」。
3. **本地状态机**：发送中 → 已发送 → 已送达。
4. **重连**：依赖 INIT 中 `chatMessageList` 每条消息的 `status` 字段刷新「自己发出消息」的送达状态。

## 十二、完成记录（2026-10-04）

### 12.1 已实施文件

| 文件 | 改动 |
| --- | --- |
| `enums/MessageStatusEnum.java` | 新增 `DELIVERED(2, "已送达")`，补类注释 `0:发送中 1:已发送 2:已送达` |
| `enums/MessageTypeEnum.java` | 新增 `MESSAGE_ACK(14)`、`MESSAGE_STATUS_CHANGE(15)` |
| `entity/dto/WsUpstreamDto.java` | **新增**：上行载体（`messageType` + `messageId`），`@JsonIgnoreProperties(ignoreUnknown = true)` |
| `websocket/netty/HandlerWebSocket.java` | 注入 `ChatMessageService`；`channelRead0` 在 `saveHeartBeat` 后解析上行，`MESSAGE_ACK` 路由到 `ackMessage`；整段 `try/catch`，空文本短路，异常仅记日志 |
| `service/ChatMessageService.java` | 新增接口 `void ackMessage(String userId, Long messageId)` |
| `service/impl/ChatMessageServiceImpl.java` | 新增 `ackMessage`：参数校验 → 查消息 → 防伪造校验（单聊 + 接收方本人）→ 条件更新（SENDED→DELIVERED，影响行数为 0 即跳过）→ 推 `MESSAGE_STATUS_CHANGE` 给发送方 |
| `entity/po/ChatMessage.java` | 去掉 `status` 的 `@JsonIgnore`（并移除随之无用的 import），description 补 `2:已送达` |
| `websocket/ChannelContextUtils.java` | `addContext` 追加第二段「我发出的消息」查询，两段合并 → 按 `messageId` 去重 → 按 `sendTime` 升序排序 |

> 复用：`ChatMessageMapper.updateStatusByMessageIdAndStatus` 与 `ChatMessage` 的 `sendUserId`/`lastReceiveTime` 查询条件均已存在，mapper 接口与 XML **未改动**；数据库**未新增迁移**。

### 12.2 编译验证

`mvn -q compile -DskipTests` → 成功（`-q` 静默，无输出即 BUILD SUCCESS）。

### 12.3 数据库层验证（临时库，已清理）

- 环境：MySQL 8.0.43，本机 `localhost:3306`。
- 临时库 `easychat_ack_verify` 依次应用 `V1__baseline.sql`、`V2__reliability.sql`（共 10 张表，`chat_message` 含 `status` 与唯一索引 `client_message_id`）。
- **条件更新幂等闸门**：造一条 `status=1` 的消息，执行
  `UPDATE chat_message SET status=2 WHERE message_id=? AND status=1` 两次，
  `ROW_COUNT()` 结果依次为 **1、0**，终态 `status=2` —— 与 `ackMessage` 的幂等预期一致。
- **INIT 第二段查询核对**：以 A=`U_A`、B=`U_B` 造两条消息——
  - 第一段（`contact_id IN {群, U_A}`）只命中「B→A」的消息，**不含** A 发出的消息；
  - 第二段（`send_user_id = U_A`）命中「A→B」的消息，且带出最新 `status=2`。
  - 印证：若无 5.7 的第二段查询，A 重连后无法得知自己发出的消息已送达。
- **清理**：`DROP DATABASE easychat_ack_verify`，`SHOW DATABASES LIKE 'easychat%'` 仅剩 `easychat`；**全程未改动正式库 `easychat`**。

### 12.4 未执行项（环境限制）

- **应用启动 / 端到端**（`@Lazy` 自注入、WS 上行解析、离线补齐）：本机未安装 Redis（6379 未监听），应用无法启动，记为「未执行」；需在具备 Redis 的环境补测：B 在线收消息回 ACK → A 实时收到 `messageType=15`；B 离线后上线经 INIT 补拉并回 ACK → A 重连经 INIT 看到 `status=2`。

### 12.5 代码自查结论

- `HandlerWebSocket` 上行解析：空文本短路避免心跳刷错误日志；解析异常被 `try/catch` 兜住，不会因上行数据异常关闭 WS 连接；`userId` 缺失时 `ackMessage` 首道判空即返回。
- `ackMessage` 防伪造：`contactType=USER` 且 `contactId == userId` 双重校验，群聊及非接收方 ACK 一律忽略。
- 推送字段方向正确：`contactId` 填发送方（路由目标），`sendUserId` 填接收方（`sendMsg` 内部会将 `contactId` 覆盖为 `sendUserId`，前端看到的联系人即接收方）。
- INIT 合并：按 `messageId` 去重兜住「我发出的群消息同时命中两段」，`sendTime` 升序保证前端渲染顺序，`nullsLast` 避免空发送时间导致 NPE。
- 未引入 NPE：合并排序字段全程走 `Comparator.nullsLast`。

## 十三、缺陷修复记录（2026-10-04，评审后）

第十二节的实现经设计评审后发现两个缺陷，本节记录根因与修复。

### 13.1 缺陷一：第二段查询谓词错误，实际恒查空

- 第二段沿用了第一段的 `lastReceiveTime = lastOffTime`。但 `lastReceiveTime` 在 `ChatMessageMapper.xml` 中映射为 `and send_time &gt; #{query.lastReceiveTime}`，作用于 **`send_time`**。
- 而 `lastOffTime` 是 `removeContext` 写入的 **「WS 断开时刻」**。待回补的消息发送于**断开之前**，`send_time &lt; lastOffTime`，会被该条件直接过滤掉；A 断开后又不可能再发消息 → **第二段永远查不到任何数据**，5.7 等于没生效。
- **根因不是「窗口写反了」**，而是：第二段关心的是「状态**变更**」这一事件，却套用了「消息**发送**时间」的谓词，字段语义对不上。
- **修复**：第二段改用 `send_user_id = 当前用户` + `status = DELIVERED(2)` 精确筛选，时间条件退化为「最近三天」的回溯下界（`now - 3天`，仅用于兜住数据量），不再使用 `lastOffTime`。

### 13.2 缺陷二：把「我发出的消息」混入 `chatMessageList` 会让前端未读虚增

- 前端 `ChatMessageModel.saveMessageBatch` 进入后会**先按 `contactId` 累加未读数**（`updateNoReadCount`），再逐条 `INSERT OR REPLACE` 写库。
- 该路径本用于「离线期间别人发给我的**新**消息」，计未读合理；把「我自己发出的消息」一起塞进去后，这些既非新消息、也非他人所发，却各计了一次未读（群消息 `contactType=1` 时 `contactId` 是群，直接加到群未读上）。
- 又因缺陷一使第二段查空，该副作用此前被掩盖 —— 属「因为错了所以没炸」。
- 另：本地 `chat_message` 主键为 `(user_id, message_id)`、写入走 `INSERT OR REPLACE`，故**不存在重复气泡渲染问题**。

### 13.3 修复方案（方案 B：INIT 独立字段，不混入 chatMessageList）

| 文件 | 改动 |
| --- | --- |
| `entity/dto/SentMessageStatusDto.java` | **新增**：只含 `messageId` / `sessionId` / `status` 三个字段的状态快照 |
| `entity/dto/WsInitData.java` | 新增字段 `sentMessageStatusList`（含 getter/setter 与 `@Schema`） |
| `websocket/ChannelContextUtils.java` | 删除原「两段合并去重排序」逻辑；`chatMessageList` 直接取第一段结果；新增按 `sendUserId + status=DELIVERED + 近三天` 查询，映射为 `SentMessageStatusDto` 列表置入新字段；移除随之无用的 `Comparator`/`LinkedHashMap`/`Map` import |
| `easy-chat`：`src/main/wsClient.js` | `case 0` 在 `saveMessageBatch` 后遍历 `sentMessageStatusList`：逐条 `updateMessage({status},{messageId})` 写本地库，并经 `receiveMessage`（`messageType=15`）通知渲染进程就地更新内存状态 |

- 前端 `case 15` 分支（`updateMessage` 写库 + `Chat.vue` 内存补齐）原本就存在，本次**复用**该通道，未新增 WS 消息类型、未新增渲染进程分支。
- 复用既有查询能力：`ChatMessageQuery.status` 与 `ChatMessageMapper.xml` 的 `and status = #{query.status}` 均已存在，**mapper 接口与 XML 未改动**，数据库**未新增迁移**。

### 13.4 数据库层验证（临时库，已清理）

- 临时库 `easychat_ack_verify2` 应用 `V1__baseline.sql` + `V2__reliability.sql`。
- 造数据（A=`UA`、B=`UB`；`@t1` = 断开时刻 = `now - 1小时`）：`#1` A→B `status=2` `send_time=t1-60s`；`#2` A→B `status=1`；`#3` A→B `status=2` 但 `send_time=now-4天`（超回溯窗口）；`#4` B→A `status=1` `send_time=t1+10s`。
- 三组查询结果：

| 查询 | 谓词 | 命中 |
| --- | --- | --- |
| 新第二段 | `send_user_id='UA' AND status=2 AND send_time &gt; now-3天` | **1**（命中 `#1`；正确排除 `#2` 未送达、`#3` 超窗口） |
| 旧第二段 | `send_user_id='UA' AND send_time &gt; @t1` | **NULL**（印证 13.1 的「恒查空」） |
| 第一段 | `contact_id IN ('UA','UG') AND send_time &gt; @t1` | **4**（只含他人发给 A 的 `#4`，印证覆盖不到 A 自己发出的 `#1`） |

- **清理**：`DROP DATABASE easychat_ack_verify2`，`SHOW DATABASES LIKE 'easychat%'` 仅剩 `easychat`；**全程未改动正式库 `easychat`**。
- **编译**：`mvn -q compile -DskipTests` → 成功。

### 13.5 更正与遗留

- **更正 12.1 / 12.5**：`ChannelContextUtils` 中「两段合并 → 去重 → 按 `sendTime` 排序」的做法**已废弃**；`chatMessageList` 恢复为仅第一段（他人发给我的）结果，排序仍由前端 `order by message_id desc` 决定。
- **更正 十一.4**：重连回补「自己发出消息」的送达状态，不再依赖 `chatMessageList` 中每条消息的 `status`，改为消费 `WsInitData.sentMessageStatusList`。
- **已知代价**：每次重连都会把「最近三天内、状态为已送达的我发出的消息」全量回补一次（缺「客户端已确认到哪个状态」的水位线，在不新增表/迁移的前提下无法更精确）。数据量可控、操作幂等，但非增量最优，记为已知取舍。
- **遗留待办**：`ChatMessage.vue` 目前只有 `v-if="data.status == 0"` 显示骨架屏，`status=1` 与 `status=2` 渲染完全相同 —— 「已送达」在前端暂无可见效果，是否补 UI 待定。

### 13.6 附带修复：心跳帧被误当 JSON 解析

- **现象**：前端心跳发的是**纯文本** `heart beat`（`wsClient.js` 每 5 秒一次），而上行路由会把**所有**非空文本都送去 `JsonUtils.convertJson2Obj`。fastjson 解析 `heart beat` 必然失败：
  1. `JsonUtils.convertJson2Obj` 内部 `logger.error("convertJson2Obj转换异常,json:{}")` —— 第 1 条 ERROR；
  2. 异常被包成 `BusinessException` 抛出，`HandlerWebSocket` 的 `catch` 再 `logger.error(..., e)` —— 第 2 条 ERROR（带完整堆栈）。
- **影响**：一次心跳两条 ERROR，频率为 5 秒/连接；连接数一多即刷爆日志、淹没真实错误，并持续浪费异常构造与堆栈填充。`saveHeartBeat` 在 `try` 之外，故**心跳续期与连接本身不受影响**，属日志与性能噪音，非功能性故障。
- **修复（方案 A）**：在 `StringTools.isEmpty(text)` 之后增加首字符短路 `if (!text.startsWith("{")) { return; }`。心跳等非 JSON 帧直接返回，不再进入解析；`try/catch` 保留，继续接住「形似 JSON 但解析失败」的脏数据。
- **验证**：`mvn -q compile -DskipTests` → 成功。
