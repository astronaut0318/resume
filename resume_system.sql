/*
 Navicat MySQL Data Transfer

 Source Server         : resume_sysytem
 Source Server Type    : MySQL
 Source Server Version : 80028
 Source Host           : localhost:3306
 Source Schema         : resume_system

 Target Server Type    : MySQL
 Target Server Version : 80028
 File Encoding         : 65001

 Date: 02/06/2025 21:58:38
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for document_edit_history
-- ----------------------------
DROP TABLE IF EXISTS `document_edit_history`;
CREATE TABLE `document_edit_history`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `document_id` bigint NOT NULL COMMENT '文档ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名称',
  `action` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作类型：OPEN, EDIT, SAVE, CLOSE',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_document_id`(`document_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文档编辑记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of document_edit_history
-- ----------------------------

-- ----------------------------
-- Table structure for document_lock
-- ----------------------------
DROP TABLE IF EXISTS `document_lock`;
CREATE TABLE `document_lock`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `document_id` bigint NOT NULL COMMENT '文档ID',
  `user_id` bigint NOT NULL COMMENT '锁定用户ID',
  `user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '锁定用户名称',
  `lock_time` datetime NOT NULL COMMENT '锁定时间',
  `expires` datetime NOT NULL COMMENT '过期时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_document_id`(`document_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文档编辑锁表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of document_lock
-- ----------------------------

-- ----------------------------
-- Table structure for document_metadata
-- ----------------------------
DROP TABLE IF EXISTS `document_metadata`;
CREATE TABLE `document_metadata`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `source_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '来源类型：RESUME-简历, TEMPLATE-模板, FILE-普通文件',
  `source_id` bigint NOT NULL COMMENT '来源ID',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件名',
  `file_ext` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件扩展名',
  `file_size` bigint NOT NULL COMMENT '文件大小(字节)',
  `content_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内容类型',
  `storage_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '存储路径',
  `version` int NOT NULL DEFAULT 1 COMMENT '当前版本号',
  `creator_id` bigint NOT NULL COMMENT '创建者ID',
  `last_modifier_id` bigint NULL DEFAULT NULL COMMENT '最后修改者ID',
  `last_modified` datetime NULL DEFAULT NULL COMMENT '最后修改时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_source`(`source_type`, `source_id`) USING BTREE,
  INDEX `idx_creator_id`(`creator_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文档元数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of document_metadata
-- ----------------------------

-- ----------------------------
-- Table structure for document_version
-- ----------------------------
DROP TABLE IF EXISTS `document_version`;
CREATE TABLE `document_version`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `document_id` bigint NOT NULL COMMENT '文档ID',
  `version` int NOT NULL COMMENT '版本号',
  `file_size` bigint NOT NULL COMMENT '文件大小(字节)',
  `storage_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '存储路径',
  `modifier_id` bigint NOT NULL COMMENT '修改者ID',
  `modifier_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '修改者名称',
  `change_summary` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '变更摘要',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_document_version`(`document_id`, `version`) USING BTREE,
  INDEX `idx_document_id`(`document_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文档版本表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of document_version
-- ----------------------------

-- ----------------------------
-- Table structure for files
-- ----------------------------
DROP TABLE IF EXISTS `files`;
CREATE TABLE `files`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `original_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '原始文件名',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '存储文件名',
  `file_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件路径',
  `file_size` bigint NOT NULL COMMENT '文件大小(字节)',
  `file_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件类型（template/resume/avatar）',
  `biz_id` bigint NULL DEFAULT NULL COMMENT '业务ID（如模板ID、简历ID、用户ID，头像时为用户ID）',
  `file_ext` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件扩展名',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_file_type`(`file_type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 54 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文件表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of files
-- ----------------------------
INSERT INTO `files` VALUES (28, 0, '会计助理word简历模板(1)_25.docx', '会计助理word简历模板(1)_25.docx', '会计助理word简历模板(1)_25.docx', 40105, 'template', 25, NULL, '2025-06-01 20:04:10');
INSERT INTO `files` VALUES (29, 0, '市场营销相关免费简历模板.docx', '市场营销相关免费简历模板.docx', '市场营销相关免费简历模板.docx', 257769, 'template', 27, NULL, '2025-06-01 20:04:10');
INSERT INTO `files` VALUES (30, 0, '应届毕业生免费简历模板.docx', '应届毕业生免费简历模板.docx', '应届毕业生免费简历模板.docx', 78072, 'template', NULL, NULL, '2025-06-01 20:04:10');
INSERT INTO `files` VALUES (31, 0, '应聘岗位JAVA开发工程师简历模板.docx', '应聘岗位JAVA开发工程师简历模板.docx', '应聘岗位JAVA开发工程师简历模板.docx', 45957, 'template', NULL, NULL, '2025-06-01 20:04:10');
INSERT INTO `files` VALUES (32, 0, '护士一页求职简历模板.docx', '护士一页求职简历模板.docx', '护士一页求职简历模板.docx', 350084, 'template', NULL, NULL, '2025-06-01 20:04:10');
INSERT INTO `files` VALUES (33, 0, '砖红色通用免费简历模板(4).docx', '砖红色通用免费简历模板(4).docx', '砖红色通用免费简历模板(4).docx', 203051, 'template', NULL, NULL, '2025-06-01 20:04:10');
INSERT INTO `files` VALUES (34, 0, '砖红色通用免费简历模板.docx', '砖红色通用免费简历模板.docx', '砖红色通用免费简历模板.docx', 203051, 'template', NULL, NULL, '2025-06-01 20:04:10');
INSERT INTO `files` VALUES (35, 0, '简约风格软件开发员简历样板.docx', '简约风格软件开发员简历样板.docx', '简约风格软件开发员简历样板.docx', 178340, 'template', NULL, NULL, '2025-06-01 20:04:10');
INSERT INTO `files` VALUES (36, 0, '简约风求职简历模板.docx', '简约风求职简历模板.docx', '简约风求职简历模板.docx', 130773, 'template', NULL, NULL, '2025-06-01 20:04:10');
INSERT INTO `files` VALUES (37, 0, '蓝色求职风免费简历模板.docx', '蓝色求职风免费简历模板.docx', '蓝色求职风免费简历模板.docx', 72078, 'template', NULL, NULL, '2025-06-01 20:04:10');
INSERT INTO `files` VALUES (38, 0, '金融分析师简历模板.docx', '金融分析师简历模板.docx', '金融分析师简历模板.docx', 29669, 'template', NULL, NULL, '2025-06-01 20:04:10');
INSERT INTO `files` VALUES (39, 0, '非常好用的免费简历模板.docx', '非常好用的免费简历模板.docx', '非常好用的免费简历模板.docx', 84738, 'template', NULL, NULL, '2025-06-01 20:04:10');
INSERT INTO `files` VALUES (40, 0, 'ZW-00065简约风求职简历模板.docx', 'ZW-00065简约风求职简历模板.docx', 'ZW-00065简约风求职简历模板.docx', 130773, 'template', NULL, NULL, '2025-06-01 20:04:40');
INSERT INTO `files` VALUES (41, 0, '人事经理求职必备免费简历模板.docx', '人事经理求职必备免费简历模板.docx', '人事经理求职必备免费简历模板.docx', 1439249, 'template', NULL, NULL, '2025-06-01 20:04:40');
INSERT INTO `files` VALUES (42, 0, '人事行政工作简历模板.docx', '人事行政工作简历模板.docx', '人事行政工作简历模板.docx', 160876, 'template', NULL, NULL, '2025-06-01 20:04:40');
INSERT INTO `files` VALUES (43, 0, '会计助理word简历模板(2).docx', '会计助理word简历模板(2).docx', '会计助理word简历模板(2).docx', 56266, 'template', NULL, NULL, '2025-06-01 20:04:40');
INSERT INTO `files` VALUES (44, 0, '会计助理word简历模板.docx', '会计助理word简历模板.docx', '会计助理word简历模板.docx', 40105, 'template', NULL, NULL, '2025-06-01 20:04:40');
INSERT INTO `files` VALUES (45, 0, '创意免费求职简历模板(3).docx', '创意免费求职简历模板(3).docx', '创意免费求职简历模板(3).docx', 470109, 'template', NULL, NULL, '2025-06-01 20:04:40');
INSERT INTO `files` VALUES (46, 0, '创意免费求职简历模板(4).docx', '创意免费求职简历模板(4).docx', '创意免费求职简历模板(4).docx', 470109, 'template', NULL, NULL, '2025-06-01 20:04:40');
INSERT INTO `files` VALUES (47, 0, '创意免费求职简历模板.docx', '创意免费求职简历模板.docx', '创意免费求职简历模板.docx', 470109, 'template', NULL, NULL, '2025-06-01 20:04:40');
INSERT INTO `files` VALUES (48, 0, '双栏个人求职简历.docx', '双栏个人求职简历.docx', '双栏个人求职简历.docx', 30902, 'template', NULL, NULL, '2025-06-01 20:04:40');
INSERT INTO `files` VALUES (49, 0, '定制行政助理求职模板.docx', '定制行政助理求职模板.docx', '定制行政助理求职模板.docx', 274864, 'template', NULL, NULL, '2025-06-01 20:04:40');
INSERT INTO `files` VALUES (50, 0, '左右双栏风免费简历模板.docx', '左右双栏风免费简历模板.docx', '左右双栏风免费简历模板.docx', 192214, 'template', NULL, NULL, '2025-06-01 20:04:40');
INSERT INTO `files` VALUES (51, 0, '市场营销求职简历模板.docx', '市场营销求职简历模板.docx', '市场营销求职简历模板.docx', 106343, 'template', NULL, NULL, '2025-06-01 20:04:40');
INSERT INTO `files` VALUES (52, 0, '销售员岗位简历模板.docx', '销售员岗位简历模板.docx', '销售员岗位简历模板.docx', 69599, 'template', NULL, NULL, '2025-06-01 20:04:40');
INSERT INTO `files` VALUES (53, 0, '高端商务风格人事经理简历.docx', '高端商务风格人事经理简历.docx', '高端商务风格人事经理简历.docx', 141428, 'template', NULL, NULL, '2025-06-01 20:04:40');

-- ----------------------------
-- Table structure for notifications
-- ----------------------------
DROP TABLE IF EXISTS `notifications`;
CREATE TABLE `notifications`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内容',
  `type` tinyint NOT NULL COMMENT '类型：1-系统通知，2-订单通知，3-其他',
  `is_read` tinyint NOT NULL DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '通知表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notifications
-- ----------------------------

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单编号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `template_id` bigint NOT NULL COMMENT '模板ID',
  `amount` decimal(10, 2) NOT NULL COMMENT '订单金额',
  `pay_type` tinyint NULL DEFAULT NULL COMMENT '支付方式：1-支付宝，2-微信',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0-待支付，1-已支付，2-已取消',
  `pay_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of orders
-- ----------------------------

-- ----------------------------
-- Table structure for payment_records
-- ----------------------------
DROP TABLE IF EXISTS `payment_records`;
CREATE TABLE `payment_records`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单编号',
  `trade_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '交易流水号',
  `pay_type` tinyint NOT NULL COMMENT '支付方式：1-支付宝，2-微信',
  `amount` decimal(10, 2) NOT NULL COMMENT '支付金额',
  `status` tinyint NOT NULL COMMENT '状态：0-处理中，1-成功，2-失败',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_no`(`order_no`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '支付记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of payment_records
-- ----------------------------

-- ----------------------------
-- Table structure for resume_versions
-- ----------------------------
DROP TABLE IF EXISTS `resume_versions`;
CREATE TABLE `resume_versions`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `resume_id` bigint NOT NULL COMMENT '简历ID',
  `version` int NOT NULL COMMENT '版本号',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '简历内容（JSON格式）',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_resume_version`(`resume_id`, `version`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '简历版本表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of resume_versions
-- ----------------------------

-- ----------------------------
-- Table structure for resumes
-- ----------------------------
DROP TABLE IF EXISTS `resumes`;
CREATE TABLE `resumes`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '简历标题',
  `template_id` bigint NULL DEFAULT NULL COMMENT '模板ID',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '简历内容（JSON格式）',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0-草稿，1-已完成',
  `is_default` tinyint NOT NULL DEFAULT 0 COMMENT '是否默认简历：0-否，1-是',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除 0-未删 1-已删',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '简历表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of resumes
-- ----------------------------

-- ----------------------------
-- Table structure for template_categories
-- ----------------------------
DROP TABLE IF EXISTS `template_categories`;
CREATE TABLE `template_categories`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '模板分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of template_categories
-- ----------------------------

-- ----------------------------
-- Table structure for template_collections
-- ----------------------------
DROP TABLE IF EXISTS `template_collections`;
CREATE TABLE `template_collections`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `template_id` bigint NOT NULL COMMENT '模板ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_template`(`user_id`, `template_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '模板收藏表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of template_collections
-- ----------------------------
INSERT INTO `template_collections` VALUES (1, 11, 27, '2025-06-02 21:37:16');

-- ----------------------------
-- Table structure for templates
-- ----------------------------
DROP TABLE IF EXISTS `templates`;
CREATE TABLE `templates`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '模板名称',
  `thumbnail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '缩略图',
  `file_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '模板文件路径',
  `price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '价格',
  `is_free` tinyint NOT NULL DEFAULT 0 COMMENT '是否免费：0-否，1-是',
  `downloads` int NOT NULL DEFAULT 0 COMMENT '下载次数',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '描述',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-下架，1-上架',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category_id`(`category_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 28 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '模板表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of templates
-- ----------------------------
INSERT INTO `templates` VALUES (1, 1, 'ZW-00065简约风求职简历模板', 'http://127.0.0.1:9090/resume-thumbnails/ZW-00065简约风求职简历模板.png', 'ZW-00065简约风求职简历模板.docx', 0.00, 1, 0, '', 1, '2025-05-29 10:49:20', '2025-05-29 10:49:20');
INSERT INTO `templates` VALUES (2, 1, '人事经理求职必备免费简历模板', '/static/default-thumbnail.png', '人事经理求职必备免费简历模板.docx', 0.00, 1, 0, '', 1, '2025-05-29 10:49:20', '2025-05-29 10:49:20');
INSERT INTO `templates` VALUES (3, 1, '人事行政工作简历模板', '/static/default-thumbnail.png', '人事行政工作简历模板.docx', 0.00, 1, 0, '', 1, '2025-05-29 10:49:20', '2025-05-29 10:49:20');
INSERT INTO `templates` VALUES (4, 1, '会计助理word简历模板', '/static/default-thumbnail.png', '会计助理word简历模板.docx', 0.00, 1, 0, '', 1, '2025-05-29 10:49:20', '2025-05-29 10:49:20');
INSERT INTO `templates` VALUES (5, 1, '创意免费求职简历模板(3)', '/static/default-thumbnail.png', '创意免费求职简历模板(3).docx', 0.00, 1, 0, '', 1, '2025-05-29 10:49:20', '2025-05-29 10:49:20');
INSERT INTO `templates` VALUES (6, 1, '创意免费求职简历模板(4)', '/static/default-thumbnail.png', '创意免费求职简历模板(4).docx', 0.00, 1, 0, '', 1, '2025-05-29 10:49:20', '2025-05-29 10:49:20');
INSERT INTO `templates` VALUES (7, 1, '创意免费求职简历模板', '/static/default-thumbnail.png', '创意免费求职简历模板.docx', 0.00, 1, 0, '', 1, '2025-05-29 10:49:20', '2025-05-29 10:49:20');
INSERT INTO `templates` VALUES (8, 1, '双栏个人求职简历', '/static/default-thumbnail.png', '双栏个人求职简历.docx', 0.00, 1, 0, '', 1, '2025-05-29 10:49:20', '2025-05-29 10:49:20');
INSERT INTO `templates` VALUES (9, 1, '定制行政助理求职模板', '/static/default-thumbnail.png', '定制行政助理求职模板.docx', 0.00, 1, 0, '', 1, '2025-05-29 10:49:20', '2025-05-29 10:49:20');
INSERT INTO `templates` VALUES (10, 1, '左右双栏风免费简历模板', '/static/default-thumbnail.png', '左右双栏风免费简历模板.docx', 0.00, 1, 0, '', 1, '2025-05-29 10:49:20', '2025-05-29 10:49:20');
INSERT INTO `templates` VALUES (11, 1, '市场营销求职简历模板', '/static/default-thumbnail.png', '市场营销求职简历模板.docx', 0.00, 1, 0, '', 1, '2025-05-29 10:49:20', '2025-05-29 10:49:20');
INSERT INTO `templates` VALUES (12, 1, '市场营销相关免费简历模板', '/static/default-thumbnail.png', '市场营销相关免费简历模板.docx', 0.00, 1, 0, '', 1, '2025-05-29 10:49:20', '2025-05-29 10:49:20');
INSERT INTO `templates` VALUES (13, 1, '应届毕业生免费简历模板', '/static/default-thumbnail.png', '应届毕业生免费简历模板.docx', 0.00, 1, 0, '', 1, '2025-05-29 10:49:20', '2025-05-29 10:49:20');
INSERT INTO `templates` VALUES (14, 1, '应聘岗位JAVA开发工程师简历模板', '/static/default-thumbnail.png', '应聘岗位JAVA开发工程师简历模板.docx', 0.00, 1, 0, '', 1, '2025-05-29 10:49:20', '2025-05-29 10:49:20');
INSERT INTO `templates` VALUES (15, 1, '护士一页求职简历模板', '/static/default-thumbnail.png', '护士一页求职简历模板.docx', 0.00, 1, 0, '', 1, '2025-05-29 10:49:20', '2025-05-29 10:49:20');
INSERT INTO `templates` VALUES (16, 1, '砖红色通用免费简历模板(4)', '/static/default-thumbnail.png', '砖红色通用免费简历模板(4).docx', 0.00, 1, 0, '', 1, '2025-05-29 10:49:20', '2025-05-29 10:49:20');
INSERT INTO `templates` VALUES (17, 1, '砖红色通用免费简历模板', '/static/default-thumbnail.png', '砖红色通用免费简历模板.docx', 0.00, 1, 0, '', 1, '2025-05-29 10:49:20', '2025-05-29 10:49:20');
INSERT INTO `templates` VALUES (18, 1, '简约风格软件开发员简历样板', '/static/default-thumbnail.png', '简约风格软件开发员简历样板.docx', 0.00, 1, 0, '', 1, '2025-05-29 10:49:20', '2025-05-29 10:49:20');
INSERT INTO `templates` VALUES (19, 1, '简约风求职简历模板', '/static/default-thumbnail.png', '简约风求职简历模板.docx', 0.00, 1, 0, '', 1, '2025-05-29 10:49:20', '2025-05-29 10:49:20');
INSERT INTO `templates` VALUES (20, 1, '蓝色求职风免费简历模板', '/static/default-thumbnail.png', '蓝色求职风免费简历模板.docx', 0.00, 1, 0, '', 1, '2025-05-29 10:49:20', '2025-05-29 10:49:20');
INSERT INTO `templates` VALUES (21, 1, '金融分析师简历模板', '/static/default-thumbnail.png', '金融分析师简历模板.docx', 0.00, 1, 0, '', 1, '2025-05-29 10:49:20', '2025-05-29 10:49:20');
INSERT INTO `templates` VALUES (22, 1, '销售员岗位简历模板', '/static/default-thumbnail.png', '销售员岗位简历模板.docx', 0.00, 1, 0, '', 1, '2025-05-29 10:49:20', '2025-05-29 10:49:20');
INSERT INTO `templates` VALUES (23, 1, '非常好用的免费简历模板', '/static/default-thumbnail.png', '非常好用的免费简历模板.docx', 0.00, 1, 0, '', 1, '2025-05-29 10:49:20', '2025-05-29 10:49:20');
INSERT INTO `templates` VALUES (24, 1, '高端商务风格人事经理简历', '/static/default-thumbnail.png', '高端商务风格人事经理简历.docx', 0.00, 1, 0, '', 1, '2025-05-29 10:49:20', '2025-05-29 10:49:20');
INSERT INTO `templates` VALUES (26, 1, '会计助理word简历模板(2)', '/static/default-thumbnail.png', '会计助理word简历模板(2).docx', 0.00, 1, 0, '', 1, '2025-05-29 11:56:40', '2025-05-29 11:56:40');
INSERT INTO `templates` VALUES (27, 1, '会计助理word简历模板(1)_25', '/static/default-thumbnail.png', '会计助理word简历模板(1)_25.docx', 0.00, 1, 0, '', 1, '2025-06-01 20:00:30', '2025-06-01 20:00:30');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像',
  `role` tinyint NOT NULL DEFAULT 0 COMMENT '角色：0-普通用户，1-VIP用户，2-管理员',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username`) USING BTREE,
  UNIQUE INDEX `uk_email`(`email`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (10, 'zhangsan', '482c811da5d5b4bc6d497ffa98491e38', 'zgssan@example.com', '13800199000', NULL, 0, 1, '2025-06-01 16:57:06', '2025-06-01 16:57:06', NULL, 0);
INSERT INTO `user` VALUES (11, 'lisi', '482c811da5d5b4bc6d497ffa98491e38', 'astronaut@stu.ptu.edu.cn', '18888888888', NULL, 1, 1, '2025-06-01 17:02:15', '2025-06-01 17:02:15', NULL, 0);

-- ----------------------------
-- Table structure for user_bak
-- ----------------------------
DROP TABLE IF EXISTS `user_bak`;
CREATE TABLE `user_bak`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像',
  `role` tinyint NOT NULL DEFAULT 0 COMMENT '角色：0-普通用户，1-VIP用户，2-管理员',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username`) USING BTREE,
  UNIQUE INDEX `uk_email`(`email`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_bak
-- ----------------------------

-- ----------------------------
-- Table structure for user_details
-- ----------------------------
DROP TABLE IF EXISTS `user_details`;
CREATE TABLE `user_details`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `gender` tinyint NULL DEFAULT NULL COMMENT '性别：0-未知，1-男，2-女',
  `birthday` date NULL DEFAULT NULL COMMENT '生日',
  `education` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学历',
  `work_years` int NULL DEFAULT NULL COMMENT '工作年限',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '地址',
  `profile` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '个人简介',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除标志 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户详情表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_details
-- ----------------------------
INSERT INTO `user_details` VALUES (3, 10, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2025-06-01 16:57:06', '2025-06-01 16:57:06', 0);
INSERT INTO `user_details` VALUES (4, 11, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2025-06-01 17:02:15', '2025-06-01 17:02:15', 0);

-- ----------------------------
-- Table structure for vip_members
-- ----------------------------
DROP TABLE IF EXISTS `vip_members`;
CREATE TABLE `vip_members`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `level` tinyint NOT NULL DEFAULT 1 COMMENT 'VIP等级',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除标志 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'VIP会员表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of vip_members
-- ----------------------------
INSERT INTO `vip_members` VALUES (1, 10, 0, '2025-06-01 16:57:06', '2025-07-01 16:57:06', '2025-06-01 16:57:06', '2025-06-01 16:57:06', 0);
INSERT INTO `vip_members` VALUES (2, 11, 0, '2025-06-01 17:02:15', '2025-07-01 17:02:15', '2025-06-01 17:02:15', '2025-06-01 17:02:15', 0);

SET FOREIGN_KEY_CHECKS = 1;
