package com.ptu.common.util;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * MinIO工具类
 */
@Slf4j
@Component
public class MinioUtils {

    @Autowired
    private MinioClient minioClient;

    /**
     * 创建存储桶
     *
     * @param bucketName 存储桶名称
     * @throws Exception 异常信息
     */
    public void createBucket(String bucketName) throws Exception {
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            log.info("创建存储桶: {}", bucketName);
        }
    }

    /**
     * 上传文件
     *
     * @param bucketName  存储桶名称
     * @param objectName  对象名称
     * @param file        文件
     * @param contentType 内容类型
     * @return 文件路径
     */
    public String uploadFile(String bucketName, String objectName, MultipartFile file, String contentType) {
        try {
            // 检查存储桶是否存在
            createBucket(bucketName);

            // 使用PutObjectArgs上传
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .contentType(contentType)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .build());

            log.info("文件上传成功，bucketName: {}, objectName: {}", bucketName, objectName);
            return objectName;
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败", e);
        }
    }

    /**
     * 上传文件，自动生成唯一文件名
     *
     * @param bucketName  存储桶名称
     * @param file        文件
     * @param contentType 内容类型
     * @return 文件路径
     */
    public String uploadFile(String bucketName, MultipartFile file, String contentType) {
        // 生成唯一文件名
        String fileName = UUID.randomUUID().toString().replaceAll("-", "");
        // 获取文件后缀
        String originalFilename = file.getOriginalFilename();
        String suffix = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        // 完整的对象名
        String objectName = fileName + suffix;
        return uploadFile(bucketName, objectName, file, contentType);
    }

    /**
     * 获取文件访问URL
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param expires    过期时间（分钟）
     * @return 文件访问URL
     */
    public String getFileUrl(String bucketName, String objectName, Integer expires) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .method(Method.GET)
                            .expiry(expires, TimeUnit.MINUTES)
                            .build());
        } catch (Exception e) {
            log.error("获取文件访问URL失败", e);
            throw new RuntimeException("获取文件访问URL失败", e);
        }
    }

    /**
     * 获取文件流
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @return 文件流
     */
    public InputStream getObject(String bucketName, String objectName) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build());
        } catch (Exception e) {
            log.error("获取文件流失败", e);
            throw new RuntimeException("获取文件流失败", e);
        }
    }

    /**
     * 删除文件
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     */
    public void removeObject(String bucketName, String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build());
            log.info("文件删除成功，bucketName: {}, objectName: {}", bucketName, objectName);
        } catch (Exception e) {
            log.error("文件删除失败", e);
            throw new RuntimeException("文件删除失败", e);
        }
    }

    /**
     * 检查文件是否存在
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @return 是否存在
     */
    public boolean objectExists(String bucketName, String objectName) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
} 