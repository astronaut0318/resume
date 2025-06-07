-- 添加缺失的列到 document_metadata 表
ALTER TABLE document_metadata 
ADD COLUMN mime_type VARCHAR(100) COMMENT 'MIME类型' AFTER file_size,
ADD COLUMN creator_name VARCHAR(50) COMMENT '创建者名称' AFTER creator_id;

-- 为content_type字段添加默认值
ALTER TABLE document_metadata
MODIFY content_type VARCHAR(50) DEFAULT 'document' COMMENT '内容类型'; 