-- 文档元数据表
CREATE TABLE `document_metadata` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `source_type` varchar(50) NOT NULL COMMENT '来源类型：RESUME-简历, TEMPLATE-模板, FILE-普通文件',
  `source_id` bigint NOT NULL COMMENT '来源ID',
  `file_name` varchar(255) NOT NULL COMMENT '文件名',
  `file_ext` varchar(20) NOT NULL COMMENT '文件扩展名',
  `file_size` bigint NOT NULL COMMENT '文件大小(字节)',
  `content_type` varchar(100) NOT NULL COMMENT '内容类型',
  `storage_path` varchar(255) NOT NULL COMMENT '存储路径',
  `version` int NOT NULL DEFAULT '1' COMMENT '当前版本号',
  `creator_id` bigint NOT NULL COMMENT '创建者ID',
  `last_modifier_id` bigint DEFAULT NULL COMMENT '最后修改者ID',
  `last_modified` datetime DEFAULT NULL COMMENT '最后修改时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_source` (`source_type`,`source_id`),
  KEY `idx_creator_id` (`creator_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文档元数据表';

-- 文档版本表
CREATE TABLE `document_version` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `document_id` bigint NOT NULL COMMENT '文档ID',
  `version` int NOT NULL COMMENT '版本号',
  `file_size` bigint NOT NULL COMMENT '文件大小(字节)',
  `storage_path` varchar(255) NOT NULL COMMENT '存储路径',
  `modifier_id` bigint NOT NULL COMMENT '修改者ID',
  `modifier_name` varchar(100) DEFAULT NULL COMMENT '修改者名称',
  `change_summary` varchar(500) DEFAULT NULL COMMENT '变更摘要',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_document_version` (`document_id`,`version`),
  KEY `idx_document_id` (`document_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文档版本表';

-- 文档编辑锁表
CREATE TABLE `document_lock` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `document_id` bigint NOT NULL COMMENT '文档ID',
  `user_id` bigint NOT NULL COMMENT '锁定用户ID',
  `user_name` varchar(100) NOT NULL COMMENT '锁定用户名称',
  `lock_time` datetime NOT NULL COMMENT '锁定时间',
  `expires` datetime NOT NULL COMMENT '过期时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_document_id` (`document_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文档编辑锁表';

-- 文档编辑记录表
CREATE TABLE `document_edit_history` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `document_id` bigint NOT NULL COMMENT '文档ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `user_name` varchar(100) NOT NULL COMMENT '用户名称',
  `action` varchar(50) NOT NULL COMMENT '操作类型：OPEN, EDIT, SAVE, CLOSE',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_document_id` (`document_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文档编辑记录表'; 