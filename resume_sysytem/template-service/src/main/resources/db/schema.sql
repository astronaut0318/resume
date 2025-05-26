-- 创建数据库
CREATE DATABASE IF NOT EXISTS resume_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE resume_system;

-- 模板分类表
CREATE TABLE IF NOT EXISTS `template_categories` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `sort` int(11) NOT NULL DEFAULT 0 COMMENT '排序值',
  `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '状态（0-禁用，1-启用）',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE,
  KEY `idx_sort` (`sort`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模板分类表';

-- 模板表
CREATE TABLE IF NOT EXISTS `templates` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `category_id` bigint(20) NOT NULL COMMENT '分类ID',
  `name` varchar(100) NOT NULL COMMENT '模板名称',
  `thumbnail` varchar(255) NOT NULL COMMENT '缩略图URL',
  `file_path` varchar(255) NOT NULL COMMENT '模板文件URL',
  `price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '价格',
  `is_free` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否免费（0-付费，1-免费）',
  `downloads` int(11) NOT NULL DEFAULT 0 COMMENT '下载次数',
  `description` varchar(500) DEFAULT NULL COMMENT '模板描述',
  `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '状态（0-下架，1-上架）',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_category_id` (`category_id`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE,
  KEY `idx_is_free` (`is_free`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模板表';

-- 模板收藏表
CREATE TABLE IF NOT EXISTS `template_collections` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `template_id` bigint(20) NOT NULL COMMENT '模板ID',
  `create_time` datetime NOT NULL COMMENT '收藏时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_user_template` (`user_id`, `template_id`) USING BTREE,
  KEY `idx_template_id` (`template_id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模板收藏表';

-- 初始化数据
INSERT INTO `template_categories` (`name`, `sort`, `status`, `create_time`, `update_time`) VALUES 
('简约风格', 1, 1, NOW(), NOW()),
('商务风格', 2, 1, NOW(), NOW()),
('创意风格', 3, 1, NOW(), NOW()); 