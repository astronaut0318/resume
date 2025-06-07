package com.ptu.document.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptu.document.common.util.DocumentUtil;
import com.ptu.document.common.util.FileUtils;
import com.ptu.document.common.util.JwtUtil;
import com.ptu.document.config.OnlyOfficeConfig;
import com.ptu.document.dao.DocumentMetadataMapper;
import com.ptu.document.dao.DocumentVersionMapper;
import com.ptu.document.dto.CallbackData;
import com.ptu.document.dto.DocumentEditorConfig;
import com.ptu.document.entity.DocumentMetadataEntity;
import com.ptu.document.entity.DocumentVersionEntity;
import com.ptu.document.service.DocumentService;
import com.ptu.document.vo.DocumentVersionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 文档服务实现类
 */
@Slf4j
@Service
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    private OnlyOfficeConfig onlyOfficeConfig;
    
    @Autowired
    private DocumentMetadataMapper documentMetadataMapper;
    
    @Autowired
    private DocumentVersionMapper documentVersionMapper;
    
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 导出简历为Word
     *
     * @param resumeId 简历ID
     * @param userId   用户ID
     * @return Word文件流
     */
    @Override
    public InputStream exportResumeAsWord(Long resumeId, Long userId) {
        // TODO: 实现导出简历为Word的功能
        log.info("导出简历为Word: resumeId={}, userId={}", resumeId, userId);
        return new ByteArrayInputStream(new byte[0]);
    }

    /**
     * 导出简历为PDF
     *
     * @param resumeId 简历ID
     * @param userId   用户ID
     * @return PDF文件流
     */
    @Override
    public InputStream exportResumeAsPdf(Long resumeId, Long userId) {
        // TODO: 实现导出简历为PDF的功能
        log.info("导出简历为PDF: resumeId={}, userId={}", resumeId, userId);
        return new ByteArrayInputStream(new byte[0]);
    }

    /**
     * 获取文档元数据
     *
     * @param sourceType 来源类型
     * @param sourceId   来源ID
     * @return 文档元数据
     */
    @Override
    public DocumentMetadataEntity getDocumentMetadata(String sourceType, Long sourceId) {
        if (sourceType == null || sourceId == null) {
            return null;
        }
        
        LambdaQueryWrapper<DocumentMetadataEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocumentMetadataEntity::getSourceType, sourceType.toUpperCase())
               .eq(DocumentMetadataEntity::getSourceId, sourceId);
        
        return documentMetadataMapper.selectOne(wrapper);
    }

    /**
     * 保存文档元数据
     *
     * @param metadata 文档元数据
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveDocumentMetadata(DocumentMetadataEntity metadata) {
        if (metadata == null) {
            return false;
        }
        
        if (metadata.getId() != null) {
            // 更新
            return documentMetadataMapper.updateById(metadata) > 0;
        } else {
            // 新增
            return documentMetadataMapper.insert(metadata) > 0;
        }
    }

    /**
     * 获取文档编辑器配置
     *
     * @param sourceType 来源类型：template/resume/file
     * @param sourceId   来源ID
     * @param mode       模式：view/edit/comment
     * @param userId     用户ID
     * @param userName   用户名称
     * @return 编辑器配置
     */
    @Override
    public DocumentEditorConfig getEditorConfig(String sourceType, Long sourceId, String mode, Long userId, String userName) {
        try {
            // 获取文档元数据
            DocumentMetadataEntity metadata = getDocumentMetadata(sourceType, sourceId);
            if (metadata == null) {
                log.error("获取文档元数据失败: sourceType={}, sourceId={}", sourceType, sourceId);
                return null;
            }
            
            // 检查权限
            if (!checkPermission(sourceType, sourceId, userId, mode)) {
                log.error("权限检查失败: sourceType={}, sourceId={}, userId={}, mode={}", sourceType, sourceId, userId, mode);
                return null;
            }
            
            // 确保文件存在
            Path filePath = Paths.get(onlyOfficeConfig.getDocument().getStoragePath(), metadata.getFilePath());
            if (!Files.exists(filePath)) {
                log.warn("文件不存在: {}，将创建空白文件", filePath);
                try {
                    // 确保目录存在
                    Files.createDirectories(filePath.getParent());
                    
                    // 根据文件类型创建不同的文件
                    String fileExt = metadata.getFileExt().toLowerCase();
                    if ("docx".equals(fileExt)) {
                        // 创建Word文档
                        FileUtils.createMinimalWordDocument(filePath);
                    } else {
                        // 创建普通空白文件
                        Files.createFile(filePath);
                        log.info("已创建空白文件: {}", filePath);
                    }
                } catch (IOException e) {
                    log.error("创建文档文件失败: {}", filePath, e);
                    return null;
                }
            }
            
            // 构建文档编辑器配置
            DocumentEditorConfig config = new DocumentEditorConfig();
            
            // 设置文档类型
            String docType = DocumentUtil.getDocumentType(metadata.getFileName(), onlyOfficeConfig);
            config.setDocumentType(DocumentUtil.getDocumentEditorKey(docType));
            
            // 设置文档信息
            String fileUrl = String.format("/api/documents/%s/%s/content", sourceType.toLowerCase(), sourceId);
            DocumentEditorConfig.Document document = new DocumentEditorConfig.Document(
                    metadata.getFileName(),
                    fileUrl,
                    metadata.getFileExt()
            );
            
            // 设置文档权限
            Map<String, Object> permissions = new HashMap<>();
            permissions.put("download", true);
            permissions.put("print", true);
            if (DocumentUtil.MODE_EDIT.equals(mode) && DocumentUtil.isEditable(metadata.getFileName(), onlyOfficeConfig)) {
                permissions.put("edit", true);
                permissions.put("review", true);
            } else {
                permissions.put("edit", false);
                permissions.put("review", false);
            }
            document.setPermissions(permissions);
            
            // 设置文档信息
            Map<String, Object> info = new HashMap<>();
            info.put("owner", userName);
            info.put("uploaded", metadata.getCreateTime().toString());
            info.put("favorite", false);
            document.setInfo(info);
            
            config.setDocument(document);
            
            // 设置编辑器配置
            DocumentEditorConfig.EditorConfig editorConfig = new DocumentEditorConfig.EditorConfig(
                    mode,
                    userId.toString(),
                    userName
            );
            
            // 设置回调URL
            editorConfig.setCallbackUrl(onlyOfficeConfig.getCallbackUrl());
            
            config.setEditorConfig(editorConfig);
            config.setDocserviceApiUrl(onlyOfficeConfig.getDocumentServerUrl());
            
            // 生成令牌
            if (StringUtils.hasText(onlyOfficeConfig.getJwtSecret())) {
                Map<String, Object> tokenData = new HashMap<>();
                tokenData.put("document", document);
                tokenData.put("editorConfig", editorConfig);
                String token = JwtUtil.generateToken(tokenData, onlyOfficeConfig);
                config.setToken(token);
            }
            
            return config;
        } catch (Exception e) {
            log.error("获取编辑器配置异常", e);
            return null;
        }
    }

    /**
     * 处理回调
     *
     * @param body 回调请求体
     * @return 处理结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleCallback(String body) {
        try {
            log.info("OnlyOffice回调请求: {}", body);
            
            // 解析回调数据
            CallbackData callbackData = objectMapper.readValue(body, CallbackData.class);
            if (callbackData == null) {
                log.error("解析回调数据失败: {}", body);
                return false;
            }
            
            // 验证token
            if (StringUtils.hasText(onlyOfficeConfig.getJwtSecret()) && callbackData.getToken() != null) {
                boolean valid = JwtUtil.validateToken(callbackData.getToken(), onlyOfficeConfig);
                if (!valid) {
                    log.error("验证token失败: {}", callbackData.getToken());
                    return false;
                }
            }
            
            // 记录用户信息
            if (callbackData.getUsers() != null && !callbackData.getUsers().isEmpty()) {
                log.info("文档操作用户: {}", callbackData.getUsers());
            }
            
            // 记录操作信息
            if (callbackData.getActions() != null && !callbackData.getActions().isEmpty()) {
                for (CallbackData.Action action : callbackData.getActions()) {
                    log.info("文档操作: 类型={}, 用户ID={}", 
                            action.getType() == 0 ? "打开文档" : 
                            action.getType() == 1 ? "保存文档" : "其他操作", 
                            action.getUserId());
                }
            }
            
            // 处理不同的回调状态
            switch (callbackData.getStatus()) {
                case 0: // 文档编辑开始
                    log.info("文档编辑开始: key={}", callbackData.getKey());
                    break;
                case 1: // 文档已准备就绪（强制保存模式）
                    log.info("文档已准备就绪(强制保存模式): url={}", callbackData.getUrl());
                    return saveDocument(callbackData);
                case 2: // 文档已保存
                    log.info("文档已保存: url={}", callbackData.getUrl());
                    return saveDocument(callbackData);
                case 3: // 文档保存错误
                    log.error("文档保存错误: url={}, 错误码={}", callbackData.getUrl(), callbackData.getError());
                    return false;
                case 4: // 文档已编辑
                    log.info("文档已编辑: key={}", callbackData.getKey());
                    break;
                default:
                    log.warn("未知的回调状态: {}", callbackData.getStatus());
                    break;
            }
            
            return true;
        } catch (Exception e) {
            log.error("处理回调异常", e);
            return false;
        }
    }
    
    /**
     * 保存文档
     * @param callbackData 回调数据
     * @return 是否成功
     */
    private boolean saveDocument(CallbackData callbackData) {
        try {
            if (callbackData == null) {
                log.error("保存文档失败: 回调数据为空");
                return false;
            }
            
            // 处理状态=1的强制保存模式，此时可能没有URL，直接返回成功
            if (callbackData.getStatus() != null && callbackData.getStatus() == 1 
                    && !StringUtils.hasText(callbackData.getUrl())) {
                log.info("强制保存模式，尚未有URL，返回成功");
                return true;
            }
            
            // 确保URL不为空
            if (!StringUtils.hasText(callbackData.getUrl())) {
                log.error("保存文档失败: URL为空");
                return false;
            }
            
            String url = callbackData.getUrl();
            log.info("开始下载并保存文档: url={}", url);
            
            // 解析URL中的文档信息
            // 假设URL格式: /api/documents/{sourceType}/{sourceId}/content
            String[] parts = url.split("/");
            if (parts.length < 5) {
                log.error("无法从URL解析文档信息: {}", url);
                return false;
            }
            
            String sourceType = parts[parts.length - 3];
            String sourceIdStr = parts[parts.length - 2];
            
            Long sourceId;
            try {
                sourceId = Long.parseLong(sourceIdStr);
            } catch (NumberFormatException e) {
                log.error("无效的sourceId: {}", sourceIdStr);
                return false;
            }
            
            // 获取文档元数据
            DocumentMetadataEntity metadata = getDocumentMetadata(sourceType, sourceId);
            if (metadata == null) {
                log.error("获取文档元数据失败: sourceType={}, sourceId={}", sourceType, sourceId);
                return false;
            }
            
            // 下载文件
            try (InputStream fileStream = downloadFromUrl(url)) {
                if (fileStream == null) {
                    log.error("下载文件失败: {}", url);
                    return false;
                }
                
                // 确保存储目录存在
                Path storagePath = Paths.get(onlyOfficeConfig.getDocument().getStoragePath());
                if (!Files.exists(storagePath)) {
                    Files.createDirectories(storagePath);
                }
                
                // 保存文件
                Path filePath = Paths.get(onlyOfficeConfig.getDocument().getStoragePath(), metadata.getFilePath());
                Files.createDirectories(filePath.getParent());
                Files.copy(fileStream, filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                
                log.info("文档保存成功: {}", filePath);
                return true;
            }
        } catch (Exception e) {
            log.error("保存文档异常", e);
            return false;
        }
    }

    /**
     * 创建文档版本
     *
     * @param sourceType 来源类型
     * @param sourceId   来源ID
     * @param userId     用户ID
     * @return 版本ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDocumentVersion(String sourceType, Long sourceId, Long userId) {
        try {
            // 获取文档元数据
            DocumentMetadataEntity metadata = getDocumentMetadata(sourceType, sourceId);
            if (metadata == null) {
                log.error("获取文档元数据失败: sourceType={}, sourceId={}", sourceType, sourceId);
                return null;
            }
            
            // 检查权限
            if (!checkPermission(sourceType, sourceId, userId, DocumentUtil.MODE_VIEW)) {
                log.error("权限检查失败: sourceType={}, sourceId={}, userId={}", sourceType, sourceId, userId);
                return null;
            }
            
            // 获取最大版本号
            Integer maxVersion = documentVersionMapper.getMaxVersionNumber(sourceType, sourceId);
            int newVersion = (maxVersion == null) ? 1 : maxVersion + 1;
            
            // 创建新版本
            DocumentVersionEntity versionEntity = new DocumentVersionEntity();
            versionEntity.setSourceType(sourceType.toUpperCase());
            versionEntity.setSourceId(sourceId);
            versionEntity.setVersion(newVersion);
            versionEntity.setFileName(metadata.getFileName());
            versionEntity.setFilePath(metadata.getFilePath());
            versionEntity.setFileSize(metadata.getFileSize());
            versionEntity.setFileType(metadata.getFileType());
            versionEntity.setModifierId(userId);
            versionEntity.setModifierName("用户" + userId); // 实际应该从用户服务获取用户名
            versionEntity.setCreateTime(LocalDateTime.now());
            
            documentVersionMapper.insert(versionEntity);
            
            return versionEntity.getId();
        } catch (Exception e) {
            log.error("创建文档版本异常", e);
            return null;
        }
    }

    /**
     * 获取文档版本列表
     *
     * @param sourceType 来源类型
     * @param sourceId   来源ID
     * @return 版本列表
     */
    @Override
    public List<DocumentVersionVO> getVersionList(String sourceType, Long sourceId) {
        try {
            // 查询版本列表
            LambdaQueryWrapper<DocumentVersionEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DocumentVersionEntity::getSourceType, sourceType.toUpperCase())
                   .eq(DocumentVersionEntity::getSourceId, sourceId)
                   .orderByDesc(DocumentVersionEntity::getVersion);
            
            List<DocumentVersionEntity> versionEntities = documentVersionMapper.selectList(wrapper);
            
            // 如果版本列表为空，则从元数据自动创建初始版本
            if (versionEntities == null || versionEntities.isEmpty()) {
                log.info("版本列表为空，创建初始版本: sourceType={}, sourceId={}", sourceType, sourceId);
                DocumentMetadataEntity metadata = getDocumentMetadata(sourceType, sourceId);
                if (metadata != null) {
                    // 创建初始版本
                    DocumentVersionEntity initialVersion = new DocumentVersionEntity();
                    initialVersion.setSourceType(sourceType.toUpperCase());
                    initialVersion.setSourceId(sourceId);
                    initialVersion.setVersion(1);
                    initialVersion.setFileName(metadata.getFileName());
                    initialVersion.setFilePath(metadata.getFilePath());
                    initialVersion.setFileSize(metadata.getFileSize());
                    initialVersion.setFileType(metadata.getFileType());
                    initialVersion.setModifierId(metadata.getCreatorId());
                    initialVersion.setModifierName(metadata.getCreatorName());
                    initialVersion.setChangeSummary("初始版本");
                    initialVersion.setCreateTime(LocalDateTime.now());
                    
                    documentVersionMapper.insert(initialVersion);
                    
                    // 重新查询
                    versionEntities = documentVersionMapper.selectList(wrapper);
                    log.info("已创建初始版本: {}", initialVersion);
                }
            }
            
            // 转换为VO
            return versionEntities.stream().map(entity -> {
                DocumentVersionVO vo = new DocumentVersionVO();
                BeanUtils.copyProperties(entity, vo);
                return vo;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取文档版本列表异常", e);
            return Collections.emptyList();
        }
    }

    /**
     * 获取指定版本
     *
     * @param versionId 版本ID
     * @return 版本信息
     */
    @Override
    public DocumentVersionVO getVersion(Long versionId) {
        try {
            DocumentVersionEntity entity = documentVersionMapper.selectById(versionId);
            if (entity == null) {
                return null;
            }
            
            DocumentVersionVO vo = new DocumentVersionVO();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        } catch (Exception e) {
            log.error("获取指定版本异常", e);
            return null;
        }
    }

    /**
     * 预览指定版本
     *
     * @param versionId 版本ID
     * @param userId    用户ID
     * @param userName  用户名
     * @return 预览配置
     */
    @Override
    public DocumentEditorConfig previewVersion(Long versionId, Long userId, String userName) {
        try {
            // 获取版本信息
            DocumentVersionEntity version = documentVersionMapper.selectById(versionId);
            if (version == null) {
                log.error("获取版本信息失败: versionId={}", versionId);
                return null;
            }
            
            // 检查权限
            if (!checkPermission(version.getSourceType(), version.getSourceId(), userId, DocumentUtil.MODE_VIEW)) {
                log.error("权限检查失败: versionId={}, userId={}", versionId, userId);
                return null;
            }
            
            // 构建文档编辑器配置
            DocumentEditorConfig config = new DocumentEditorConfig();
            
            // 设置文档类型
            String fileExt = DocumentUtil.getFileExtension(version.getFileName());
            String docType = DocumentUtil.getDocumentType(version.getFileName(), onlyOfficeConfig);
            config.setDocumentType(DocumentUtil.getDocumentEditorKey(docType));
            
            // 设置文档信息
            String fileUrl = String.format("/api/documents/versions/%s/content", versionId);
            DocumentEditorConfig.Document document = new DocumentEditorConfig.Document(
                    version.getFileName(),
                    fileUrl,
                    fileExt
            );
            
            // 设置文档权限（只读）
            Map<String, Object> permissions = new HashMap<>();
            permissions.put("download", true);
            permissions.put("print", true);
            permissions.put("edit", false);
            permissions.put("review", false);
            document.setPermissions(permissions);
            
            // 设置文档信息
            Map<String, Object> info = new HashMap<>();
            info.put("owner", version.getModifierName());
            info.put("uploaded", version.getCreateTime().toString());
            info.put("favorite", false);
            document.setInfo(info);
            
            config.setDocument(document);
            
            // 设置编辑器配置
            DocumentEditorConfig.EditorConfig editorConfig = new DocumentEditorConfig.EditorConfig(
                    DocumentUtil.MODE_VIEW,
                    userId.toString(),
                    userName
            );
            
            config.setEditorConfig(editorConfig);
            config.setDocserviceApiUrl(onlyOfficeConfig.getDocumentServerUrl());
            
            // 生成令牌
            if (StringUtils.hasText(onlyOfficeConfig.getJwtSecret())) {
                Map<String, Object> tokenData = new HashMap<>();
                tokenData.put("document", document);
                tokenData.put("editorConfig", editorConfig);
                String token = JwtUtil.generateToken(tokenData, onlyOfficeConfig);
                config.setToken(token);
            }
            
            return config;
        } catch (Exception e) {
            log.error("预览指定版本异常", e);
            return null;
        }
    }

    /**
     * 下载文档
     *
     * @param sourceType 来源类型
     * @param sourceId   来源ID
     * @return 文件流
     */
    @Override
    public InputStream downloadDocument(String sourceType, Long sourceId) {
        try {
            // 获取文档元数据
            DocumentMetadataEntity metadata = getDocumentMetadata(sourceType, sourceId);
            if (metadata == null) {
                log.error("获取文档元数据失败: sourceType={}, sourceId={}", sourceType, sourceId);
                return null;
            }
            
            // 读取文件
            Path filePath = Paths.get(onlyOfficeConfig.getDocument().getStoragePath(), metadata.getFilePath());
            if (!Files.exists(filePath)) {
                log.error("文件不存在: {}", filePath);
                return null;
            }
            
            return Files.newInputStream(filePath);
        } catch (Exception e) {
            log.error("下载文档异常", e);
            return null;
        }
    }

    /**
     * 下载指定版本
     *
     * @param versionId 版本ID
     * @return 文件流
     */
    @Override
    public InputStream downloadVersion(Long versionId) {
        try {
            // 获取版本信息
            DocumentVersionEntity version = documentVersionMapper.selectById(versionId);
            if (version == null) {
                log.error("获取版本信息失败: versionId={}", versionId);
                return null;
            }
            
            // 读取文件
            Path filePath = Paths.get(onlyOfficeConfig.getDocument().getStoragePath(), version.getFilePath());
            if (!Files.exists(filePath)) {
                log.error("文件不存在: {}", filePath);
                return null;
            }
            
            return Files.newInputStream(filePath);
        } catch (Exception e) {
            log.error("下载指定版本异常", e);
            return null;
        }
    }

    /**
     * 检查权限
     *
     * @param sourceType 来源类型
     * @param sourceId   来源ID
     * @param userId     用户ID
     * @param mode       操作模式
     * @return 是否有权限
     */
    @Override
    public boolean checkPermission(String sourceType, Long sourceId, Long userId, String mode) {
        // TODO: 实现权限检查逻辑
        // 暂时直接返回true，后续根据业务需求实现
        return true;
    }

    /**
     * 从URL下载文件
     *
     * @param url 文件URL
     * @return 文件输入流
     */
    private InputStream downloadFromUrl(String url) {
        try {
            URL fileUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) fileUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(30000);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 ResumeSystem/1.0");
            
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return connection.getInputStream();
            } else {
                log.error("下载文件失败: HTTP状态码={}, url={}", connection.getResponseCode(), url);
                return null;
            }
        } catch (Exception e) {
            log.error("从URL下载文件异常: {}", url, e);
            return null;
        }
    }
} 