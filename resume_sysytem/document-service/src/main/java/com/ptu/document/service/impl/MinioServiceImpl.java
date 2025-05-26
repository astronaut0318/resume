package com.ptu.document.service.impl;

import com.ptu.document.config.MinioConfig;
import com.ptu.document.service.MinioService;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * MinIO服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MinioServiceImpl implements MinioService {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    @Override
    public boolean bucketExists(String bucketName) {
        try {
            return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            log.error("检查存储桶是否存在异常: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean createBucket(String bucketName) {
        try {
            if (!bucketExists(bucketName)) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
            return true;
        } catch (Exception e) {
            log.error("创建存储桶异常: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean putObject(String bucketName, String objectName, InputStream stream, long size, String contentType) {
        try {
            // 检查并创建桶
            if (!bucketExists(bucketName)) {
                createBucket(bucketName);
            }

            // 上传文件
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(stream, size, -1)
                            .contentType(contentType)
                            .build()
            );
            return true;
        } catch (Exception e) {
            log.error("上传文件异常: {}", e.getMessage());
            return false;
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    log.error("关闭流异常: {}", e.getMessage());
                }
            }
        }
    }

    @Override
    public boolean putObject(String bucketName, String objectName, InputStream stream) {
        try {
            // 默认使用application/octet-stream
            String contentType = "application/octet-stream";
            if (objectName.endsWith(".pdf")) {
                contentType = "application/pdf";
            } else if (objectName.endsWith(".doc") || objectName.endsWith(".docx")) {
                contentType = "application/msword";
            } else if (objectName.endsWith(".xls") || objectName.endsWith(".xlsx")) {
                contentType = "application/vnd.ms-excel";
            } else if (objectName.endsWith(".ppt") || objectName.endsWith(".pptx")) {
                contentType = "application/vnd.ms-powerpoint";
            }

            // 获取流大小
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = stream.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            byte[] buf = baos.toByteArray();
            
            return putObject(bucketName, objectName, new ByteArrayInputStream(buf), buf.length, contentType);
        } catch (Exception e) {
            log.error("上传文件异常: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean putObject(String bucketName, String objectName, byte[] bytes) {
        try (ByteArrayInputStream stream = new ByteArrayInputStream(bytes)) {
            // 默认使用application/octet-stream
            String contentType = "application/octet-stream";
            if (objectName.endsWith(".pdf")) {
                contentType = "application/pdf";
            } else if (objectName.endsWith(".doc") || objectName.endsWith(".docx")) {
                contentType = "application/msword";
            } else if (objectName.endsWith(".xls") || objectName.endsWith(".xlsx")) {
                contentType = "application/vnd.ms-excel";
            } else if (objectName.endsWith(".ppt") || objectName.endsWith(".pptx")) {
                contentType = "application/vnd.ms-powerpoint";
            }

            return putObject(bucketName, objectName, stream, bytes.length, contentType);
        } catch (Exception e) {
            log.error("上传文件异常: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public InputStream getObject(String bucketName, String objectName) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            log.error("获取文件异常: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public byte[] getObjectBytes(String bucketName, String objectName) {
        try (InputStream stream = getObject(bucketName, objectName)) {
            if (stream == null) {
                return null;
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = stream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("获取文件异常: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public boolean removeObject(String bucketName, String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
            return true;
        } catch (Exception e) {
            log.error("删除文件异常: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public String generatePresignedUrl(String bucketName, String objectName, int expiry, TimeUnit unit) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(expiry, unit)
                            .build()
            );
        } catch (Exception e) {
            log.error("生成临时访问URL异常: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public long getObjectSize(String bucketName, String objectName) {
        try {
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
            return stat.size();
        } catch (Exception e) {
            log.error("获取文件大小异常: {}", e.getMessage());
            return 0;
        }
    }

    @Override
    public String getObjectContentType(String bucketName, String objectName) {
        try {
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
            return stat.contentType();
        } catch (Exception e) {
            log.error("获取文件内容类型异常: {}", e.getMessage());
            return null;
        }
    }
} 