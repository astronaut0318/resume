-- 先删除现有表（如果存在）
DROP TABLE IF EXISTS `document_versions`;
DROP TABLE IF EXISTS `document_metadata`;

-- 文档元数据表
CREATE TABLE IF NOT EXISTS `document_metadata` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `source_type` varchar(20) NOT NULL COMMENT '来源类型：TEMPLATE/RESUME/FILE',
  `source_id` bigint NOT NULL COMMENT '来源ID',
  `file_name` varchar(255) NOT NULL COMMENT '文件名',
  `file_ext` varchar(20) NOT NULL COMMENT '文件扩展名',
  `file_size` bigint NOT NULL COMMENT '文件大小(字节)',
  `mime_type` varchar(100) DEFAULT NULL COMMENT 'MIME类型',
  `content_type` varchar(100) DEFAULT NULL COMMENT '内容类型',
  `file_type` varchar(50) DEFAULT NULL COMMENT '文件类型',
  `file_path` varchar(255) NOT NULL COMMENT '文件路径',
  `version` int NOT NULL DEFAULT '1' COMMENT '当前版本号',
  `creator_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `creator_name` varchar(100) DEFAULT NULL COMMENT '创建者名称',
  `last_modifier_id` bigint DEFAULT NULL COMMENT '最后修改者ID',
  `last_modified` datetime DEFAULT NULL COMMENT '最后修改时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_source` (`source_type`,`source_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文档元数据表';

-- 文档版本表
CREATE TABLE IF NOT EXISTS `document_versions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `document_id` bigint DEFAULT NULL COMMENT '文档ID',
  `source_type` varchar(20) NOT NULL COMMENT '来源类型：TEMPLATE/RESUME/FILE',
  `source_id` bigint NOT NULL COMMENT '来源ID',
  `version` int NOT NULL COMMENT '版本号',
  `file_name` varchar(255) NOT NULL COMMENT '文件名',
  `file_type` varchar(50) DEFAULT NULL COMMENT '文件类型',
  `file_size` bigint NOT NULL COMMENT '文件大小(字节)',
  `file_path` varchar(255) NOT NULL COMMENT '文件路径',
  `modifier_id` bigint DEFAULT NULL COMMENT '修改者ID',
  `modifier_name` varchar(100) DEFAULT NULL COMMENT '修改者名称',
  `change_summary` varchar(255) DEFAULT NULL COMMENT '变更摘要',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_source_version` (`source_type`,`source_id`,`version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文档版本表';

-- 为模板添加示例数据
INSERT INTO `document_metadata` (`source_type`, `source_id`, `file_name`, `file_ext`, `file_size`, `mime_type`, `content_type`, `file_type`, `file_path`, `version`, `creator_id`, `creator_name`, `create_time`, `update_time`)
VALUES 
('TEMPLATE', 35, 'template_35.docx', 'docx', 15360, 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 'text', 'templates/template_35.docx', 1, 1, 'admin', NOW(), NOW()),
('TEMPLATE', 37, 'template_37.docx', 'docx', 15360, 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 'text', 'templates/template_37.docx', 1, 1, 'admin', NOW(), NOW()); 