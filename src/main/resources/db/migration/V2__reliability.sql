-- =============================================================================
-- EasyChat 数据库变更脚本 V2 —— 消息可靠性 + IM 功能补全（M0-3）
-- 对应计划书：easy-chat/docs/ROADMAP.md 第二节 M0-3
-- 数据库：MySQL 8.0+
--
-- 说明：
--   1. 本脚本仅做「新增列 / 新增索引 / 新增表」，不删除、不修改既有列，可在线执行。
--   2. 不显式指定 ALGORITHM / LOCK：MySQL 8.0.12+ 的 ADD COLUMN 默认走 INSTANT（仅改元数据，
--      不重建表）；显式写 ALGORITHM=INPLACE 反而会降级为重建表路径。MySQL 8.0.29+ 起支持
--      单条 ALTER 内多列同时 INSTANT（本项目 MySQL 8.0.43 满足）。
--   3. 建议执行前先备份 chat_message / chat_session_user 两张表。
--   4. MySQL 8 的 ADD COLUMN 不支持 IF NOT EXISTS，重复执行会报
--      "Duplicate column name" / "Duplicate key name"，可忽略或分批执行。
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 1. chat_message：新增幂等 ID、引用回复、@提醒、撤回相关字段
-- -----------------------------------------------------------------------------
ALTER TABLE chat_message
    ADD COLUMN client_message_id VARCHAR(64)  NULL COMMENT '客户端幂等ID，用于消息去重（M1-R1）',
    ADD COLUMN quote_message_id  BIGINT       NULL COMMENT '引用的消息ID，用于引用回复（M2-F3）',
    ADD COLUMN at_user_ids       VARCHAR(512) NULL COMMENT '被@的用户ID列表，逗号分隔（M2-F3）',
    ADD COLUMN revoke_time       BIGINT       NULL COMMENT '撤回时间戳，NULL 表示未撤回（M2-F1）',
    ADD COLUMN revoke_user_id    VARCHAR(12)  NULL COMMENT '执行撤回操作的用户ID，与 user_id 长度一致（M2-F1）';

-- 幂等唯一索引：保证同一 client_message_id 只能落库一次（M1-R1）
-- 注意：若历史数据中存在重复的非空 client_message_id，需先去重再建索引
CREATE UNIQUE INDEX uk_client_msg_id ON chat_message (client_message_id);

-- 离线增量补偿与按会话查询的联合索引（M1-R4）
CREATE INDEX idx_contact_sendtime ON chat_message (contact_id, send_time);

-- 按会话 + 消息ID 做游标增量拉取与本地去重（M1-R4）
CREATE INDEX idx_session_msgid ON chat_message (session_id, message_id);

-- -----------------------------------------------------------------------------
-- 2. chat_session_user：新增「被@未读」计数（M2-F3）
-- -----------------------------------------------------------------------------
ALTER TABLE chat_session_user
    ADD COLUMN no_read_at_count INT NOT NULL DEFAULT 0 COMMENT '被@的未读消息数（M2-F3）';

-- -----------------------------------------------------------------------------
-- 3. 新增 chat_message_delete：单方删除消息记录表（M2-F2）
--
-- 语义说明（已确认决策：仅删本地）：
--   本表按「用户维度」记录删除行为，即 A 删除后不影响 B 的视图，
--   与「消息撤回」职责分离。撤回走 chat_message.revoke_time 字段。
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS chat_message_delete (
    id          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    user_id     VARCHAR(12) NOT NULL                COMMENT '执行删除的用户ID，与 user_id 长度一致',
    message_id  BIGINT      NOT NULL                COMMENT '被删除的消息ID',
    session_id  VARCHAR(32) NOT NULL                COMMENT '所属会话ID，长度与 chat_message.session_id 一致',
    delete_time BIGINT      NOT NULL                COMMENT '删除时间戳',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_message (user_id, message_id),
    KEY idx_user_session (user_id, session_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户单方删除消息记录表';

-- -----------------------------------------------------------------------------
-- 4. 验证查询（执行后可用于自检）
-- -----------------------------------------------------------------------------
-- 4.1 确认 chat_message 新增列
-- SHOW COLUMNS FROM chat_message LIKE 'client_message_id';
-- SHOW COLUMNS FROM chat_message LIKE 'quote_message_id';
-- SHOW COLUMNS FROM chat_message LIKE 'at_user_ids';
-- SHOW COLUMNS FROM chat_message LIKE 'revoke_time';
-- SHOW COLUMNS FROM chat_message LIKE 'revoke_user_id';

-- 4.2 确认索引
-- SHOW INDEX FROM chat_message WHERE Key_name IN ('uk_client_msg_id', 'idx_contact_sendtime', 'idx_session_msgid');

-- 4.3 确认 chat_session_user 新增列
-- SHOW COLUMNS FROM chat_session_user LIKE 'no_read_at_count';

-- 4.4 确认新表
-- SHOW CREATE TABLE chat_message_delete;