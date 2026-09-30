-- =============================================================================
-- V1__baseline.sql —— EasyChat 数据库基线（仅表结构，不含业务数据）
-- 生成方式：mysqldump --no-data --routines --triggers --events --single-transaction
--           --set-gtid-purged=OFF --no-tablespaces --default-character-set=utf8mb4 easychat
-- 源库：MySQL 8.0.43 / easychat，状态为 V2__reliability.sql 应用【之前】
-- 包含表：app_updation、chat_message、chat_session、chat_session_user、group_info、
--         user_contact、user_contact_apply、user_info、user_info_beauty（共 9 张）
--
-- 后期处理：导出后已手工移除 9 行 `DROP TABLE IF EXISTS` 与文件尾部的 dump 时间戳。
--           移除 DROP 的原因：基线只应在空库执行；若保留 DROP，一旦历史表丢失导致本脚本
--           重跑，会静默删除全部业务表。移除后表已存在时 Flyway 会直接报错 —— 报错优于删数据。
--           移除时间戳的原因：让文件可复现，避免重导出时 checksum 无谓变化。
--
-- 说明：
--   1. 本文件为 Flyway 基线迁移，只含表结构，不导入任何业务数据。
--   2. 存量库：spring.flyway.baseline-on-migrate=true 会把本版本直接记为基线
--      并跳过执行，因此不会重复建表，随后自动执行 V2 及之后的迁移。
--   3. 全新空库：Flyway 检测到空库后不会建立基线，而是正常执行本脚本建表。
--   4. 请勿手工修改本文件，任何改动都会导致 Flyway checksum 校验失败。
-- =============================================================================

-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: easychat
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `app_updation`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `app_updation` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `version` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '版本号',
  `update_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新描述',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `status` tinyint(1) DEFAULT NULL COMMENT '0:未发布 1:灰度发布 2:全网发布',
  `grayscale_uid` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '灰度id',
  `file_type` tinyint(1) DEFAULT NULL COMMENT '文件类型 0:本地文件 1:外链',
  `outer_link` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '外链地址',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_key` (`version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='app发布';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `chat_message`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_message` (
  `message_id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息自增ID',
  `session_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '会话ID',
  `message_type` tinyint(1) NOT NULL COMMENT '消息类型',
  `message_content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '消息内容',
  `send_user_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '发送人工D',
  `send_user_nick_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '发送人昵称',
  `send_time` bigint DEFAULT NULL COMMENT '发送时间',
  `contact_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '接收联系人ID',
  `contact_type` tinyint(1) DEFAULT NULL COMMENT '联系人类型0:单聊1:群聊',
  `file_size` bigint DEFAULT NULL COMMENT '文件大小',
  `file_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文件名',
  `file_type` tinyint(1) DEFAULT NULL COMMENT '文件类型',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态0:正在发送1:已发送',
  PRIMARY KEY (`message_id`) USING BTREE,
  KEY `idx_session_id` (`session_id`) USING BTREE,
  KEY `idx_send_user_id` (`send_user_id`) USING BTREE,
  KEY `idx_receive_contact_id` (`contact_id`) USING BTREE,
  KEY `idx_send_time` (`send_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1698 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='聊天消息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `chat_session`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_session` (
  `session_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '会话ID',
  `last_message` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '最后接收的消息',
  `last_receive_time` bigint DEFAULT NULL COMMENT '最后接收消息时间',
  PRIMARY KEY (`session_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='会话信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `chat_session_user`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_session_user` (
  `user_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
  `contact_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '联系人ID',
  `session_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '会话ID',
  `contact_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '联系人名称',
  PRIMARY KEY (`user_id`,`contact_id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_session_id` (`session_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='会话用户';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `group_info`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_info` (
  `group_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '群ID',
  `group_name` varchar(28) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '群组名',
  `group_owner_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '群主id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `group_notice` varchar(508) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '群公告',
  `join_type` tinyint(1) DEFAULT NULL COMMENT ' 0 :直接加入 1 :管理员同意后加入 ',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态1:正常0:解散',
  PRIMARY KEY (`group_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_contact`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_contact` (
  `user_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
  `contact_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '联系人ID或者群组ID',
  `contact_type` tinyint(1) DEFAULT NULL COMMENT '联系人类型0:好友1:群组',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `status` tinyint(1) DEFAULT NULL COMMENT '状态0:非好友 1:好友 2:已删除好友 3:被好友删除 4: 已拉黑好友 5: 被好友拉黑',
  `last_update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  PRIMARY KEY (`user_id`,`contact_id`) USING BTREE,
  KEY `idx_contact_id` (`contact_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='联系人';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_contact_apply`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_contact_apply` (
  `apply_id` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `apply_user_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '申请人id',
  `receive_user_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '接收人ID',
  `contact_type` tinyint(1) NOT NULL COMMENT '联系人类型0:好友1:群组',
  `contact_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '联系人群组ID',
  `last_apply_time` bigint DEFAULT NULL COMMENT '最后申请时间',
  `status` tinyint(1) DEFAULT NULL COMMENT '状态0:待处理1:已同意2:已拒绝3:已拉黑',
  `apply_info` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '申请信息',
  PRIMARY KEY (`apply_id`) USING BTREE,
  UNIQUE KEY `idx_key` (`apply_user_id`,`receive_user_id`,`contact_id`) USING BTREE,
  KEY `idx_last_apply_time` (`last_apply_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=136893 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='联系人申请';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_info`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_info` (
  `user_id` varchar(12) COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户id',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱',
  `nick_name` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '昵称',
  `join_type` tinyint(1) DEFAULT NULL COMMENT '0:直接加入 1:同意后加入',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0:女 1:男',
  `password` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '密码',
  `personal_signature` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '个性签名',
  `status` tinyint(1) DEFAULT NULL COMMENT '状态',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `area_name` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '地区',
  `area_code` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '地区编号',
  `last_off_time` bigint DEFAULT NULL COMMENT '最后离开时间',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `idx_key_email` (`email`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_info_beauty`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_info_beauty` (
  `id` int NOT NULL AUTO_INCREMENT,
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '邮箱',
  `user_id` varchar(12) COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户id',
  `status` tinyint(1) DEFAULT NULL COMMENT '0: 未启用 1:启用',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_key_user_id` (`user_id`) USING BTREE,
  UNIQUE KEY `idx_key_email` (`email`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='靓号表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping events for database 'easychat'
--

--
-- Dumping routines for database 'easychat'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;


