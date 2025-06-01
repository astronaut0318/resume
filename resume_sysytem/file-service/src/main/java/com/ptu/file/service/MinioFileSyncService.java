package com.ptu.file.service;

import io.minio.MinioClient;
import io.minio.ListObjectsArgs;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MinioFileSyncService
 * 用于将MinIO resume-templates桶中的Word文件信息同步到数据库files表
 */
@Service
public class MinioFileSyncService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // MinIO配置
    private static final String ENDPOINT = "http://127.0.0.1:9090";
    private static final String ACCESS_KEY = "minioadmin";
    private static final String SECRET_KEY = "minioadmin";
    private static final String BUCKET = "resume-templates";
    // 匹配 文件名_{id}.docx
    private static final Pattern RESUME_ID_PATTERN = Pattern.compile(".+_(\\d+)\\.docx");

    /**
     * 同步MinIO模板Word文件到数据库files表
     * 可手动调用或定时任务调用
     */
    public void syncFilesFromMinio() throws Exception {
        MinioClient minioClient = MinioClient.builder()
                .endpoint(ENDPOINT)
                .credentials(ACCESS_KEY, SECRET_KEY)
                .build();

        Set<String> minioFiles = new HashSet<>();
        for (io.minio.Result<Item> result : minioClient.listObjects(ListObjectsArgs.builder().bucket(BUCKET).build())) {
            Item item = result.get();
            String fileName = item.objectName();
            if (!fileName.endsWith(".docx")) continue;
            minioFiles.add(fileName);

            // 提取 resumeId
            Long bizId = null;
            Matcher matcher = RESUME_ID_PATTERN.matcher(fileName);
            if (matcher.matches()) {
                bizId = Long.parseLong(matcher.group(1));
            }

            // 检查数据库是否已存在
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM files WHERE file_path = ? AND file_type = 'template'", Integer.class, fileName);

            if (count == null || count == 0) {
                if (bizId != null) {
                    jdbcTemplate.update(
                        "INSERT INTO files (user_id, original_name, file_name, file_path, file_size, file_type, biz_id, create_time) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, NOW())",
                        0L,
                        fileName,
                        fileName,
                        fileName,
                        item.size(),
                        "template",
                        bizId
                    );
                } else {
                    jdbcTemplate.update(
                        "INSERT INTO files (user_id, original_name, file_name, file_path, file_size, file_type, create_time) " +
                        "VALUES (?, ?, ?, ?, ?, ?, NOW())",
                        0L,
                        fileName,
                        fileName,
                        fileName,
                        item.size(),
                        "template"
                    );
                }
            }
        }

        // 可选：删除数据库中已不存在于MinIO的文件
        if (!minioFiles.isEmpty()) {
            String inClause = String.join(",", minioFiles.stream().map(f -> "'" + f + "'").toArray(String[]::new));
            jdbcTemplate.update(
                "DELETE FROM files WHERE file_type='template' AND file_path NOT IN (" + inClause + ")"
            );
        }
    }

    /**
     * 定时任务：每10秒自动同步一次
     */
    @Scheduled(cron = "*/10 * * * * ?")
    public void scheduledSync() {
        try {
            syncFilesFromMinio();
        } catch (Exception e) {
            // 记录日志
            e.printStackTrace();
        }
    }
} 