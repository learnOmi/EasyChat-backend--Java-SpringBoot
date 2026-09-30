# Knife4j 接口注解补全 实施记录

## 一、当前阶段与目标

- **阶段**：接口文档完善（实现阶段）。
- **目标**：为项目全部 HTTP 接口补齐 Knife4j / OpenAPI 3 注解，使 `/doc.html` 上的接口文档具备完整的**分组、摘要、参数说明、字段说明**，可直接用于前后端联调与自测。
- **现状**：`pom.xml` 已引入 `springdoc-openapi-starter-webmvc-ui 2.3.0` 与 `knife4j-openapi3-jakarta-spring-boot-starter 4.5.0`，`OpenApiConfig` 已提供全局 `token` 安全方案与静态资源映射。但注解仅覆盖 `AccountController` 的 `@Tag` 与 2 个 `@Operation`，其余 10 个 Controller 与约 30 个模型类均无注解。

## 二、涉及组件/模块清单

### 2.1 Controller 层（11 个）

| Controller | 路由前缀 | 计划 `@Tag` |
| --- | --- | --- |
| AccountController | `/account` | 账号模块（已存在，补齐方法注解） |
| UserInfoController | `/userInfo` | 用户信息模块 |
| UserContactController | `/contact` | 联系人模块 |
| GroupInfoController | `/group` | 群组模块 |
| ChatController | `/chat` | 聊天消息模块 |
| UpdateController | `/update` | APP 更新模块 |
| AdminSettingController | `/admin` | 管理端-系统设置 |
| AdminUserInfoController | `/admin` | 管理端-用户管理 |
| AdminUserInfoBeautyController | `/admin` | 管理端-靓号管理 |
| AdminGroupController | `/admin` | 管理端-群组管理 |
| AdminAppUpdateController | `/admin` | 管理端-APP更新 |

> 说明：4 个管理端 Controller 共用 `/admin` 前缀，通过不同的 `@Tag(name=...)` 在文档中区分模块分组。

### 2.2 模型层

- **PO**：`UserInfo`、`UserContact`、`UserContactApply`、`GroupInfo`、`ChatMessage`、`ChatSession`、`ChatSessionUser`、`UserInfoBeauty`、`AppUpdation`
- **VO**：`ResponseVO`、`UserInfoVO`、`GroupInfoVO`、`PaginationResultVO`、`AppUpdateVo`
- **DTO**：`TokenUserInfoDto`、`SysSettingDto`、`MessageSendDto`、`WsInitData`、`UserContactSearchResultDto`
- **Query**：`BaseQuery`、`SimplePage`、`UserInfoQuery`、`UserContactQuery`、`UserContactApplyQuery`、`GroupInfoQuery`、`ChatMessageQuery`、`ChatSessionQuery`、`ChatSessionUserQuery`、`UserInfoBeautyQuery`、`AppUpdationQuery`

## 三、技术选型说明

- 沿用项目已有的 `springdoc-openapi 2.x` + `knife4j-openapi3-jakarta`，**不引入新技术**。
- 注解全部来自 `io.swagger.v3.oas.annotations`（OpenAPI 3 / Swagger v3 注解包），与 springdoc 2.x 匹配；不使用 SpringFox（Swagger 2）的 `@Api`/`@ApiOperation`（二者不兼容 Spring Boot 3 / Jakarta）。
- 选用注解：`@Tag`（模块分组）、`@Operation`（接口摘要/描述）、`@Parameter`（参数说明）、`@Schema`（模型字段说明）。

## 四、实施策略

1. **Controller 层**：类上加 `@Tag`；每个映射方法加 `@Operation(summary, description)`；对语义不自明的参数加 `@Parameter(description)`；`HttpServletRequest` / `HttpServletResponse` 属框架参数，保持不注解（springdoc 默认忽略），避免污染文档。
2. **模型层**：类上加 `@Schema(description=...)`；字段上加 `@Schema(description=...)`，枚举型取值把可选值写进 description（如 `0:女 1:男`），与既有中文注释保持一致。
3. **不改动业务逻辑**：仅新增注解与必要 import，不修改任何方法签名、字段、SQL 与运行时行为。
4. **保留原注释**：所有原有中文行注释、JavaDoc 一律保留。

## 五、状态管理方式

- 本次改动为**纯声明式注解**，不涉及数据库事务、Redis、ThreadLocal 等状态。文档元数据在应用启动时由 springdoc 扫描装配，仅内存态。

## 六、组件逻辑编排

```
启动时：springdoc 扫描 @RestController → 读取 @Tag/@Operation/@Parameter/@Schema
        → 生成 OpenAPI 3 JSON (/v3/api-docs)
运行时：浏览器访问 /doc.html → knife4j 读取该 JSON → 渲染分组/接口/模型 → 可在线调试
        （调试请求自动带全局 token 请求头，见 OpenApiConfig 的 SecurityScheme）
```

## 七、潜在难点与风险

| 风险 | 影响 | 对策 |
| --- | --- | --- |
| 注解 import 错误（误用 Swagger2 的 `@Api`） | 编译通过但文档不生效 | 统一使用 `io.swagger.v3.oas.annotations.*` |
| 4 个管理端 Controller 共用 `/admin` | 文档分组混乱 | 使用不同 `@Tag(name)` 明确区分 |
| `@JsonIgnore` 字段被文档暴露 | 文档与实际返回不符 | `@Schema` 描述与 `@JsonIgnore` 现状保持一致，不额外暴露 |
| 泛型返回（`ResponseVO<T>`）schema 不直观 | 文档中 data 类型不明 | 在 `@Operation(description)` 中写明 data 的具体类型 |
| 改动面大（约 40 个文件） | 误改业务代码 | 仅新增注解/import，完成后 `mvn compile` 校验 |

## 八、完成记录

### 8.1 改动清单（共 41 个源文件）

**Controller（11 个）**：`AccountController`、`UserInfoController`、`UserContactController`、`GroupInfoController`、`ChatController`、`UpdateController`、`AdminSettingController`、`AdminUserInfoController`、`AdminUserInfoBeautyController`、`AdminGroupController`、`AdminAppUpdateController`。

- 每个 Controller 类新增 `@Tag(name, description)`；
- 每个映射方法新增 `@Operation(summary, description)`（description 中标注 `data` 的具体类型/取值）；
- 语义不自明的参数新增 `@Parameter(description)`（如状态码取值范围、申请信息、灰度 Uid 等）；
- `HttpServletRequest` / `HttpServletResponse` 保持不注解（框架参数，springdoc 默认忽略）。

**模型（30 个）**：PO 9、VO 5、DTO 5、Query 11。

- 每个类新增 `@Schema(description)`，每个字段新增 `@Schema(description)`；
- 枚举型字段把取值含义写入 description；集合字段说明元素含义；
- Query 类的 `xxxFuzzy` 统一标注「（模糊查询）」，联查标记字段标注「是否联查…」。

### 8.2 验证结果

| 项 | 结论 |
| --- | --- |
| `mvn compile -DskipTests` | ✅ 通过（无编译错误） |
| 改动范围 | ✅ 仅 `controller` 与 `entity` 包下 41 个文件新增注解；无业务逻辑、签名、SQL 改动 |
| 原注释保留 | ✅ 既有中文行注释、JavaDoc、`@JsonIgnore` 等全部保留，仅做新增 |
| 注解包正确性 | ✅ 统一使用 `io.swagger.v3.oas.annotations.*`（OpenAPI3），未误用 Swagger2 的 `@Api/@ApiModelProperty` |

### 8.3 注意事项

- 文档渲染效果需应用启动后访问 `/doc.html` 查看；本次仅做静态编译验证，未启动服务（启动依赖 MySQL/Redis 等外部环境）。
- `ResponseVO<T>`、`MessageSendDto<T>`、`PaginationResultVO<T>` 为泛型包装类，Knife4j 中 `data` 的具体类型已在对应接口的 `@Operation(description)` 中逐一写明，便于联调时对照。
- 4 个管理端 Controller 共用 `/admin` 路由，文档中已通过不同 `@Tag(name)` 拆分为独立分组。
