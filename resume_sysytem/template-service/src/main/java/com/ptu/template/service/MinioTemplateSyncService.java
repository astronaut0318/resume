package com.ptu.template.service;

import io.minio.MinioClient;
import io.minio.ListObjectsArgs;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * MinioTemplateSyncService
 * 用于将MinIO resume-templates桶中的文件信息同步到数据库templates表
 */
@Service
public class MinioTemplateSyncService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // MinIO配置
    private static final String ENDPOINT = "http://127.0.0.1:9090";
    private static final String ACCESS_KEY = "ZMlUgS6Z3d4raCN3pVeT";
    private static final String SECRET_KEY = "VOHLtWjk1WBFdiCpG06VPVTVgKsSp5Nl9w2D8vg8";
    private static final String BUCKET = "resume-templates";
    private static final String DEFAULT_THUMBNAIL = "/static/default-thumbnail.png";
    private static final long DEFAULT_CATEGORY_ID = 1L;

    /**
     * 同步MinIO模板文件到数据库templates表
     * 可手动调用或定时任务调用
     */
    public void syncTemplatesFromMinio() throws Exception {
        MinioClient minioClient = MinioClient.builder()
                .endpoint(ENDPOINT)
                .credentials(ACCESS_KEY, SECRET_KEY)
                .build();

        // 1. 获取MinIO所有对象名
        Set<String> minioFiles = new HashSet<>();
        for (io.minio.Result<Item> result : minioClient.listObjects(ListObjectsArgs.builder().bucket(BUCKET).build())) {
            Item item = result.get();
            String fileName = item.objectName();
            String templateName = fileName.replaceAll("\\.docx$", ""); // 去除扩展名
            minioFiles.add(fileName);

            // 2. 检查数据库是否已存在
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM templates WHERE file_path = ?", Integer.class, fileName);

            if (count == null || count == 0) {
                // 3. 插入新模板
                jdbcTemplate.update(
                    "INSERT INTO templates (category_id, name, thumbnail, file_path, price, is_free, downloads, description, status, create_time, update_time) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())",
                    DEFAULT_CATEGORY_ID,
                    templateName, // 用去除扩展名的中文名
                    DEFAULT_THUMBNAIL,
                    fileName,
                    0.00,
                    1,
                    0,
                    "",
                    1
                );
            }
        }

        // 4. 可选：删除数据库中已不存在于MinIO的模板
        if (!minioFiles.isEmpty()) {
            String inClause = String.join(",", minioFiles.stream().map(f -> "'" + f + "'").toArray(String[]::new));
            jdbcTemplate.update(
                "DELETE FROM templates WHERE file_path NOT IN (" + inClause + ")"
            );
        }
    }

    /**
     * 扫描MinIO缩略图桶，将缩略图URL补全到templates表的thumbnail字段
     * 假设缩略图文件名与模板file_path或模板名有可匹配关系
     */
    public void syncThumbnailsFromMinio() throws Exception {
        MinioClient minioClient = MinioClient.builder()
                .endpoint(ENDPOINT)
                .credentials(ACCESS_KEY, SECRET_KEY)
                .build();
        String thumbnailBucket = "resume-thumbnails";
        // 1. 获取所有缩略图对象名
        Set<String> thumbnailFiles = new HashSet<>();
        for (io.minio.Result<Item> result : minioClient.listObjects(ListObjectsArgs.builder().bucket(thumbnailBucket).build())) {
            Item item = result.get();
            String objectName = item.objectName();
            thumbnailFiles.add(objectName);
        }
        // 2. 遍历templates表，尝试为每个模板补全thumbnail字段
        jdbcTemplate.query("SELECT id, file_path, name FROM templates", rs -> {
            Long id = rs.getLong("id");
            String filePath = rs.getString("file_path");
            String name = rs.getString("name");
            // 匹配缩略图文件名（可根据实际规则调整）
            String possibleThumbnail = name + ".png"; // 例如模板名.png
            if (thumbnailFiles.contains(possibleThumbnail)) {
                // 生成可访问URL（如公开桶可直接拼接，否则用presigned url）
                String url = ENDPOINT + "/" + thumbnailBucket + "/" + possibleThumbnail;
                jdbcTemplate.update("UPDATE templates SET thumbnail = ? WHERE id = ?", url, id);
            }
        });
    }

    /**
     * 定时任务：每10秒自动同步一次
     */
    @Scheduled(cron = "*/10 * * * * ?")
    public void scheduledSync() {
        try {
            syncTemplatesFromMinio();
        } catch (Exception e) {
            // 记录日志
            e.printStackTrace();
        }
    }

    /**
     * 定时任务：每10秒自动同步缩略图到数据库
     */
    @Scheduled(cron = "*/10 * * * * ?")
    public void scheduledThumbnailSync() {
        try {
            syncThumbnailsFromMinio();
        } catch (Exception e) {
            // 记录日志
            e.printStackTrace();
        }
    }
} 