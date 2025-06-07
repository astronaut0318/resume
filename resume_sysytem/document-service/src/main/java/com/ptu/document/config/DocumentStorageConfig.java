package com.ptu.document.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 文档存储配置
 * 用于初始化文档存储目录
 */
@Slf4j
@Configuration
public class DocumentStorageConfig {

    @Autowired
    private OnlyOfficeConfig onlyOfficeConfig;

    /**
     * 初始化存储目录
     */
    @PostConstruct
    public void init() {
        try {
            // 获取存储路径
            String storagePath = onlyOfficeConfig.getDocument().getStoragePath();
            Path path = Paths.get(storagePath);
            
            // 创建主目录
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                log.info("成功创建文档存储主目录: {}", path.toAbsolutePath());
            }
            
            // 创建模板目录
            Path templatesPath = path.resolve("templates");
            if (!Files.exists(templatesPath)) {
                Files.createDirectories(templatesPath);
                log.info("成功创建模板存储目录: {}", templatesPath.toAbsolutePath());
            }
            
            // 创建简历目录
            Path resumesPath = path.resolve("resumes");
            if (!Files.exists(resumesPath)) {
                Files.createDirectories(resumesPath);
                log.info("成功创建简历存储目录: {}", resumesPath.toAbsolutePath());
            }
            
            // 创建文件目录
            Path filesPath = path.resolve("files");
            if (!Files.exists(filesPath)) {
                Files.createDirectories(filesPath);
                log.info("成功创建文件存储目录: {}", filesPath.toAbsolutePath());
            }
            
            // 创建版本目录
            Path versionsPath = path.resolve("versions");
            if (!Files.exists(versionsPath)) {
                Files.createDirectories(versionsPath);
                log.info("成功创建版本存储目录: {}", versionsPath.toAbsolutePath());
            }
            
            log.info("文档存储目录初始化完成");
        } catch (IOException e) {
            log.error("初始化文档存储目录失败", e);
        }
    }
} 