# application 配置分层重构（非敏感上移到主文件）实施记录

## 一、背景与目标

- **背景**：`.gitignore` 已将 `application-local.properties`（及 `*-local.properties`）忽略，而 `application.properties` 纳入版本库。但原 `application-local.properties` 混杂了「非敏感公共配置」与「敏感凭据」，导致大量可共享配置无法进入版本库。
- **目标**：把**非敏感**配置上移到 `application.properties`，`application-local.properties` 只保留**敏感/凭据**类配置；并明确两文件的覆盖关系。行为保持完全等价。

## 二、涉及文件

- `src/main/resources/application.properties`（纳入版本库）
- `src/main/resources/application-local.properties`（被 .gitignore 忽略）

## 三、划分依据

| 类别 | 去向 | 具体内容 |
| --- | --- | --- |
| 非敏感公共配置 | `application.properties` | 服务端口/上下文/session、multipart 大小、静态资源开关、数据源通用项（driver、Hikari 连接池）、MyBatis mapper 路径、Redis 连接与连接池、Knife4j 开关、project.folder、dev 开关、日志全部配置 |
| 敏感/凭据类 | `application-local.properties` | `spring.datasource.url`、`spring.datasource.username`、`spring.datasource.password`（凭据三元组）、`admin.emails`（个人邮箱，决定管理员身份） |
| 运行环境选择 | `application.properties` | `spring.profiles.active=local`（**不可**写在 profile-specific 文件中，否则启动报 `InvalidConfigDataPropertyException`） |

## 四、覆盖规则（Spring Boot 3）

1. **key 级合并**：两份文件按「配置键」合并，而非整文件替换。例如 `spring.datasource.hikari.*` 在主文件、`spring.datasource.password` 在 local 文件，运行时能正常合成完整数据源配置。
2. **profile-specific 永远赢**：`application-local.properties` 中的同名 key，其优先级**始终高于** `application.properties`，与文件加载顺序无关（这是设计规则）。
3. **完整优先级链（低 → 高）**：
   `application.properties` → `application-local.properties` → 操作系统环境变量 → JVM 系统属性 `-D` → 命令行参数 `--key=value`。
4. **同一文件内重复 key**：`properties` 语法下最后一个生效。
5. **占位符兜底**：`${ENV_NAME:default}` 只在同名环境变量缺失时使用默认值；设置环境变量即覆盖该默认值（如 `EASYCHAT_DB_PASSWORD`）。

## 五、验证结果

| 项 | 结论 |
| --- | --- |
| key 总数 | 47（原 46 项 + 新增 `spring.profiles.active`） |
| 跨文件重复 key | 0，两文件无同名 key，覆盖关系当前不会实际触发 |
| 敏感项位置 | 仅在 `application-local.properties`（url/username/password/admin.emails） |
| 行为等价性 | ✅ 纯「移动」，拆分前后合并结果一致 |

## 六、风险与建议

- `application-local.properties` 被 gitignore 且现仅存敏感项，新克隆仓库将缺少数据库连接配置，应用无法直接启动（与改造前一致，无回归）。
- 建议后续新增 `application-local.properties.example` 模板（该文件名不匹配 `*-local.properties` 与显式忽略规则，可入库）说明需要自行补全的敏感项。
- 若将来新增 prod 等环境，建议 Redis、`project.folder` 等环境相关项改为 `${ENV:default}` 占位形式。
