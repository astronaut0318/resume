-- 为document_version表添加缺失的列
-- 如果表不存在，先创建表
CREATE TABLE IF NOT EXISTS document_version (
    id bigint NOT NULL AUTO_INCREMENT,
    source_type varchar(50) NOT NULL COMMENT '来源类型',
    source_id bigint NOT NULL COMMENT '来源ID',
    version int NOT NULL COMMENT '版本号',
    storage_path varchar(255) NOT NULL COMMENT '存储路径',
    file_size bigint DEFAULT NULL COMMENT '文件大小',
    user_id bigint NOT NULL COMMENT '创建用户ID',
    create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_source (source_type, source_id, version)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文档版本表';

-- 如果表已存在，尝试添加缺失的列（使用兼容性更好的语法）
-- 使用SHOW COLUMNS和ALTER TABLE的组合方式添加缺失的列

-- 检查并添加source_type列
SELECT COUNT(*) INTO @source_type_exists FROM information_schema.columns 
WHERE table_schema = DATABASE() AND table_name = 'document_version' AND column_name = 'source_type';

SET @add_source_type = IF(@source_type_exists = 0, 
    'ALTER TABLE document_version ADD COLUMN source_type varchar(50) NOT NULL COMMENT \'来源类型\' AFTER id', 
    'SELECT \'source_type column already exists\' as message');
PREPARE stmt FROM @add_source_type;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加source_id列
SELECT COUNT(*) INTO @source_id_exists FROM information_schema.columns 
WHERE table_schema = DATABASE() AND table_name = 'document_version' AND column_name = 'source_id';

SET @add_source_id = IF(@source_id_exists = 0, 
    'ALTER TABLE document_version ADD COLUMN source_id bigint NOT NULL COMMENT \'来源ID\' AFTER source_type', 
    'SELECT \'source_id column already exists\' as message');
PREPARE stmt FROM @add_source_id;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加version列
SELECT COUNT(*) INTO @version_exists FROM information_schema.columns 
WHERE table_schema = DATABASE() AND table_name = 'document_version' AND column_name = 'version';

SET @add_version = IF(@version_exists = 0, 
    'ALTER TABLE document_version ADD COLUMN version int NOT NULL COMMENT \'版本号\' AFTER source_id', 
    'SELECT \'version column already exists\' as message');
PREPARE stmt FROM @add_version;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加storage_path列
SELECT COUNT(*) INTO @storage_path_exists FROM information_schema.columns 
WHERE table_schema = DATABASE() AND table_name = 'document_version' AND column_name = 'storage_path';

SET @add_storage_path = IF(@storage_path_exists = 0, 
    'ALTER TABLE document_version ADD COLUMN storage_path varchar(255) NOT NULL COMMENT \'存储路径\' AFTER version', 
    'SELECT \'storage_path column already exists\' as message');
PREPARE stmt FROM @add_storage_path;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加file_size列
SELECT COUNT(*) INTO @file_size_exists FROM information_schema.columns 
WHERE table_schema = DATABASE() AND table_name = 'document_version' AND column_name = 'file_size';

SET @add_file_size = IF(@file_size_exists = 0, 
    'ALTER TABLE document_version ADD COLUMN file_size bigint DEFAULT NULL COMMENT \'文件大小\' AFTER storage_path', 
    'SELECT \'file_size column already exists\' as message');
PREPARE stmt FROM @add_file_size;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加user_id列
SELECT COUNT(*) INTO @user_id_exists FROM information_schema.columns 
WHERE table_schema = DATABASE() AND table_name = 'document_version' AND column_name = 'user_id';

SET @add_user_id = IF(@user_id_exists = 0, 
    'ALTER TABLE document_version ADD COLUMN user_id bigint NOT NULL COMMENT \'创建用户ID\' AFTER file_size', 
    'SELECT \'user_id column already exists\' as message');
PREPARE stmt FROM @add_user_id;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加create_time列
SELECT COUNT(*) INTO @create_time_exists FROM information_schema.columns 
WHERE table_schema = DATABASE() AND table_name = 'document_version' AND column_name = 'create_time';

SET @add_create_time = IF(@create_time_exists = 0, 
    'ALTER TABLE document_version ADD COLUMN create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT \'创建时间\' AFTER user_id', 
    'SELECT \'create_time column already exists\' as message');
PREPARE stmt FROM @add_create_time;
EXECUTE stmt;
DEALLOCATE PREPARE stmt; 