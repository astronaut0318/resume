-- 创建文档版本表
CREATE TABLE document_version (
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