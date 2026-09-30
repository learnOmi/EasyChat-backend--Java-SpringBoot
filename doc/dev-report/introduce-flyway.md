# 引入 Flyway 数据库版本管理 实施记录

## 一、当前阶段与目标

- **阶段**：工程化/数据库治理（实现阶段）。
- **目标**：引入 Flyway 接管数据库结构变更，把原先散落在 `docs/sql/` 的手工脚本纳入自动迁移；为存量库生成 V1 基线，避免重复建表。
- **现状（改造前）**：无 Flyway 依赖；`docs/sql/V2__reliability.sql` 已按 Flyway 命名但未被任何工具加载，需人工执行；`easychat` 库处于 V2 应用**之前**的状态（无 `client_message_id`、无 `chat_message_delete`、无 `flyway_schema_history`）。

## 二、涉及组件

| 类型 | 内容 |
| --- | --- |
| 依赖 | `org.flywaydb:flyway-core`、`org.flywaydb:flyway-mysql`（版本由 Spring Boot 3.2.0 BOM 管理，无需指定） |
| 配置 | `application.properties` 新增 flyway 配置段 |
| 迁移脚本 | `src/main/resources/db/migration/V1__baseline.sql`（新增，mysqldump 生成）、`V2__reliability.sql`（由 `docs/sql/` 移入） |
| 删除 | 空目录 `docs/sql/`、`docs/` |

## 三、技术选型说明

- 选用 **Flyway** 而非 Liquibase：项目已有 V2 脚本采用 Flyway 的 `V{version}__{desc}.sql` 命名约定，迁移成本最低；且 Flyway 的 SQL 原生迁移对 MySQL 8 支持成熟。
- 选用 `classpath:db/migration` 作为迁移目录：随 jar 打包，生产环境可通过同一份制品自动迁移，无需外部挂载脚本目录。
- 必须引入 `flyway-mysql`：Flyway 9.x 起将各数据库方言拆分为独立模块，仅 `flyway-core` 会报找不到数据库支持。

## 四、实施策略

### 4.1 迁移目录与命名

```
src/main/resources/db/migration/
├── V1__baseline.sql     # 基线：存量库结构快照（仅表结构）
└── V2__reliability.sql  # 消息可靠性 + IM 功能补全
```

### 4.2 存量库与新库的两种行为（核心设计）

配置 `baseline-on-migrate=true` + `baseline-version=1` 后：

| 场景 | Flyway 行为 |
| --- | --- |
| **存量库**（schema 非空，如本机 `easychat`） | 不执行 V1，而是把 V1 记为基线写入历史表，随后执行 V2 及之后的迁移 → 不会重复建表 |
| **全新空库** | 不在空库上建立基线，按顺序执行 V1 建表、V2 加列 → 一键得到完整结构 |

> 这是「既能让存量库安全纳管、又能让新库一键初始化」的标准做法。

### 4.3 V1 基线生成方式

```bash
mysqldump --host=localhost --port=3306 --user=root --password=*** \
  --no-data --routines --triggers --events --single-transaction \
  --set-gtid-purged=OFF --no-tablespaces --default-character-set=utf8mb4 \
  --ignore-table=easychat.flyway_schema_history \
  --result-file=src/main/resources/db/migration/V1__baseline.sql easychat
```

关键参数取舍：

| 参数 | 原因 |
| --- | --- |
| `--no-data` | 仅导出结构，不把本机开发数据（126 条聊天记录等）带进版本库 |
| 不加 `--databases` | 避免生成 `CREATE DATABASE` / `USE`，由连接串决定目标库 |
| `--set-gtid-purged=OFF` | 避免生成需要 SUPER 权限的 GTID 语句 |
| `--no-tablespaces` | 避免需要 PROCESS 权限 |
| `--ignore-table` | 防御性排除 Flyway 历史表 |
| `--result-file` | 直接由 mysqldump 写文件，规避 PowerShell 重定向的编码问题 |

## 五、状态管理方式

- Flyway 自身状态存于数据库表 `flyway_schema_history`（记录版本、描述、checksum、执行时间）。
- `validate-on-migrate=true`：启动时校验已执行脚本的 checksum，历史脚本被改动会直接启动失败，防止库与脚本漂移。
- `clean-disabled=true`：禁用 `flyway clean`，避免误删整库。
- 应用启动顺序：DataSource 初始化 → Flyway 执行迁移 → MyBatis/业务 Bean 初始化。

## 六、组件逻辑编排

```
应用启动
  └─ 读取 application.properties（flyway.*）
      └─ 复用 spring.datasource 的数据源（凭据来自 application-local.properties）
          ├─ 查 flyway_schema_history 是否存在
          ├─ schema 非空且无历史表 → baseline(V1) 并记入历史表
          └─ 执行版本号 > 已应用版本的迁移（V2…）
              └─ 完成后 MyBatis 等组件才初始化
```

## 七、潜在难点与风险

| 风险 | 对策 |
| --- | --- |
| 基线快照与 V2 重叠（若 dump 时 V2 已应用，V2 会因重复列失败） | 已确认 dump 时库处于 V2 之前状态；V1 不含 `client_message_id` 等列 |
| Flyway 的 MySQL 解析器比 mysql 客户端更严格 | 已实测：V1 由 Flyway 实际执行通过 |
| PowerShell `>` 重定向输出 UTF-16 导致脚本损坏 | 使用 mysqldump `--result-file` 直写 |
| 后续手工改历史脚本触发 checksum 失败 | 在 V1 头部与文档中明确「请勿手工修改」 |
| `docs/sql` 目录被移除影响其他引用 | 该目录下仅有 V2 一个文件，已确认无其他引用 |

## 八、完成记录

见文末「九、验证结果」。

## 九、验证结果

### 9.1 验证方法

为避免污染正式开发库 `easychat`，新建临时库 `easychat_fw_test` 做端到端验证：
先手工执行 V1 使其「非空」，再启动应用指向该库，观察 Flyway 是否按设计走「基线 + 执行 V2」路径。

### 9.2 验证结论

| 项 | 结果 |
| --- | --- |
| `mvn compile` | ✅ 通过（`flyway-core` / `flyway-mysql` 版本由 Spring Boot 3.2.0 BOM 解析，无需手写版本号） |
| V1 语法有效性（mysql 客户端执行） | ✅ 建出 9 张表，与源库一致 |
| Flyway 识别迁移脚本 | ✅ `Successfully validated 2 migrations` |
| 存量库基线路径 | ✅ `Creating Schema History table ... with baseline` → `Successfully baselined schema with version: 1`（V1 被跳过，未重复建表） |
| 自动执行后续迁移 | ✅ `Migrating schema to version "2 - reliability"` → `Successfully applied 1 migration, now at version v2` |
| 迁移历史表内容 | ✅ rank1 = version 1 / `<< Flyway Baseline >>` / type=BASELINE；rank2 = version 2 / reliability / type=SQL，success 均为 1 |
| V2 变更落地 | ✅ `chat_message` 新增 5 列、`chat_message_delete` 表创建成功 |
| 应用启动 | ✅ `Tomcat started on port 5099` / `Netty WebSocket 启动成功` / `Started Application in 3.072 seconds`，无 ERROR |
| Flyway 版本 | Flyway Community Edition 9.22.3，识别数据库 `MySQL 8.0` |

### 9.3 清理与影响面

- 临时库 `easychat_fw_test` 已删除；测试实例已停止（端口 5099/5098 释放）；临时日志已删除。
- **正式开发库 `easychat` 未被改动**（校验：无 `flyway_schema_history` 表、`chat_message` 无 `client_message_id` 列）。
- 因此下次以 `local` 环境启动应用时，Flyway 才会对 `easychat` 建立 V1 基线并执行 V2 —— 这是预期行为，届时 `chat_message` 将新增 5 列并创建 `chat_message_delete` 表。

