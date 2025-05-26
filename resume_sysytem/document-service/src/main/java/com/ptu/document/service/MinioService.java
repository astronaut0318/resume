package com.ptu.document.service;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * MinIO服务接口
 */
public interface MinioService {
    
    /**
     * 检查存储桶是否存在
     *
     * @param bucketName 存储桶名称
     * @return 是否存在
     */
    boolean bucketExists(String bucketName);
    
    /**
     * 创建存储桶
     *
     * @param bucketName 存储桶名称
     * @return 是否成功
     */
    boolean createBucket(String bucketName);
    
    /**
     * 上传文件
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param stream 文件流
     * @param size 文件大小
     * @param contentType 内容类型
     * @return 是否成功
     */
    boolean putObject(String bucketName, String objectName, InputStream stream, long size, String contentType);
    
    /**
     * 上传文件
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param stream 文件流
     * @return 是否成功
     */
    boolean putObject(String bucketName, String objectName, InputStream stream);
    
    /**
     * 上传文件
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param bytes 文件字节数组
     * @return 是否成功
     */
    boolean putObject(String bucketName, String objectName, byte[] bytes);
    
    /**
     * 获取文件
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @return 文件流
     */
    InputStream getObject(String bucketName, String objectName);
    
    /**
     * 获取文件字节数组
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @return 文件字节数组
     */
    byte[] getObjectBytes(String bucketName, String objectName);
    
    /**
     * 删除文件
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @return 是否成功
     */
    boolean removeObject(String bucketName, String objectName);
    
    /**
     * 生成临时访问URL
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param expiry 有效期
     * @param unit 时间单位
     * @return 临时访问URL
     */
    String generatePresignedUrl(String bucketName, String objectName, int expiry, TimeUnit unit);
    
    /**
     * 获取文件大小
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @return 文件大小
     */
    long getObjectSize(String bucketName, String objectName);
    
    /**
     * 获取文件内容类型
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @return 内容类型
     */
    String getObjectContentType(String bucketName, String objectName);
} 