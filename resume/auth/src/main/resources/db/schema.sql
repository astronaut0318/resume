-- 创建数据库
CREATE DATABASE IF NOT EXISTS resume_auth DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE resume_auth;

-- 用户表
CREATE TABLE IF NOT EXISTS `auth_user` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` varchar(50) NOT NULL COMMENT '用户名',
    `password` varchar(100) NOT NULL COMMENT '密码',
    `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
    `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
    `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
    `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
    `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
    `status` int DEFAULT '0' COMMENT '账号状态（0正常 1停用）',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    `is_deleted` int DEFAULT '0' COMMENT '是否删除（0未删除 1已删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS `auth_role` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `name` varchar(50) NOT NULL COMMENT '角色名称',
    `code` varchar(50) NOT NULL COMMENT '角色编码',
    `description` varchar(255) DEFAULT NULL COMMENT '角色描述',
    `sort` int DEFAULT '0' COMMENT '显示顺序',
    `status` int DEFAULT '0' COMMENT '角色状态（0正常 1停用）',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    `is_deleted` int DEFAULT '0' COMMENT '是否删除（0未删除 1已删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 权限表
CREATE TABLE IF NOT EXISTS `auth_permission` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '权限ID',
    `name` varchar(50) NOT NULL COMMENT '权限名称',
    `code` varchar(50) NOT NULL COMMENT '权限编码',
    `type` int DEFAULT NULL COMMENT '权限类型（1菜单 2按钮 3API）',
    `parent_id` bigint DEFAULT '0' COMMENT '父权限ID',
    `path` varchar(255) DEFAULT NULL COMMENT '路径',
    `icon` varchar(50) DEFAULT NULL COMMENT '图标',
    `component` varchar(255) DEFAULT NULL COMMENT '组件路径',
    `sort` int DEFAULT '0' COMMENT '显示顺序',
    `status` int DEFAULT '0' COMMENT '权限状态（0正常 1停用）',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    `is_deleted` int DEFAULT '0' COMMENT '是否删除（0未删除 1已删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS `auth_user_role` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `role_id` bigint NOT NULL COMMENT '角色ID',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_user_role` (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS `auth_role_permission` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `role_id` bigint NOT NULL COMMENT '角色ID',
    `permission_id` bigint NOT NULL COMMENT '权限ID',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_role_perm` (`role_id`,`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 初始化数据
-- 管理员角色
INSERT INTO `auth_role` (`id`, `name`, `code`, `description`, `sort`, `status`, `create_time`, `update_time`, `is_deleted`)
VALUES 
(1, '管理员', 'ROLE_ADMIN', '系统管理员', 1, 0, NOW(), NOW(), 0),
  (2, '普通用户', 'ROLE_USER', '普通用户', 2, 0, NOW(), NOW(), 0),
  (3, 'VIP用户', 'ROLE_VIP', 'VIP用户', 3, 0, NOW(), NOW(), 0);

-- 权限数据
INSERT INTO `auth_permission` (`id`, `name`, `code`, `type`, `parent_id`, `path`, `sort`, `status`, `create_time`, `update_time`, `is_deleted`)
VALUES 
  (1, '系统管理', 'system:manage', 1, 0, '/system', 1, 0, NOW(), NOW(), 0),
  (2, '用户管理', 'system:user:manage', 1, 1, '/system/user', 1, 0, NOW(), NOW(), 0),
  (3, '角色管理', 'system:role:manage', 1, 1, '/system/role', 2, 0, NOW(), NOW(), 0),
  (4, '权限管理', 'system:permission:manage', 1, 1, '/system/permission', 3, 0, NOW(), NOW(), 0),
  (5, '查看用户', 'system:user:view', 2, 2, NULL, 1, 0, NOW(), NOW(), 0),
  (6, '添加用户', 'system:user:add', 2, 2, NULL, 2, 0, NOW(), NOW(), 0),
  (7, '编辑用户', 'system:user:edit', 2, 2, NULL, 3, 0, NOW(), NOW(), 0),
  (8, '删除用户', 'system:user:delete', 2, 2, NULL, 4, 0, NOW(), NOW(), 0),
  (9, '查看角色', 'system:role:view', 2, 3, NULL, 1, 0, NOW(), NOW(), 0),
  (10, '添加角色', 'system:role:add', 2, 3, NULL, 2, 0, NOW(), NOW(), 0),
  (11, '编辑角色', 'system:role:edit', 2, 3, NULL, 3, 0, NOW(), NOW(), 0),
  (12, '删除角色', 'system:role:delete', 2, 3, NULL, 4, 0, NOW(), NOW(), 0),
  (13, '分配权限', 'system:role:assign', 2, 3, NULL, 5, 0, NOW(), NOW(), 0),
  (14, '查看权限', 'system:permission:view', 2, 4, NULL, 1, 0, NOW(), NOW(), 0),
  (15, '添加权限', 'system:permission:add', 2, 4, NULL, 2, 0, NOW(), NOW(), 0),
  (16, '编辑权限', 'system:permission:edit', 2, 4, NULL, 3, 0, NOW(), NOW(), 0),
  (17, '删除权限', 'system:permission:delete', 2, 4, NULL, 4, 0, NOW(), NOW(), 0);

-- 管理员用户(密码: admin123)
INSERT INTO `auth_user` (`id`, `username`, `password`, `nickname`, `real_name`, `status`, `create_time`, `update_time`, `is_deleted`)
VALUES 
  (1, 'admin', '$2a$10$X9XZ0Uz4jDHR8yH7wGMsIe8AB5P562kc13VxYg3RbE1hUQtzMYSu.', '管理员', '系统管理员', 0, NOW(), NOW(), 0);

-- 为管理员分配角色
INSERT INTO `auth_user_role` (`user_id`, `role_id`, `create_time`, `update_time`)
VALUES
  (1, 1, NOW(), NOW());

-- 为管理员角色分配权限
INSERT INTO `auth_role_permission` (`role_id`, `permission_id`, `create_time`, `update_time`)
VALUES 
(1, 1, NOW(), NOW()),
(1, 2, NOW(), NOW()),
(1, 3, NOW(), NOW()),
(1, 4, NOW(), NOW()),
(1, 5, NOW(), NOW()),
(1, 6, NOW(), NOW()),
(1, 7, NOW(), NOW()),
  (1, 8, NOW(), NOW()),
  (1, 9, NOW(), NOW()),
  (1, 10, NOW(), NOW()),
  (1, 11, NOW(), NOW()),
  (1, 12, NOW(), NOW()),
  (1, 13, NOW(), NOW()),
  (1, 14, NOW(), NOW()),
  (1, 15, NOW(), NOW()),
  (1, 16, NOW(), NOW()),
  (1, 17, NOW(), NOW());