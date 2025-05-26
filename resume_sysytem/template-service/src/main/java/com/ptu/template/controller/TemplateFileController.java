package com.ptu.template.controller;

import com.ptu.common.api.R;
import com.ptu.common.util.MinioUtils;
import com.ptu.template.entity.TemplateEntity;
import com.ptu.template.service.TemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 模板文件控制器
 * 用于处理模板文件的下载和预览
 */
@Slf4j
@Api(tags = "模板文件接口")
@RestController
@RequestMapping("/template-files")
public class TemplateFileController {

    @Autowired
    private TemplateService templateService;

    @Autowired
    private MinioUtils minioUtils;

    @Value("${minio.bucket.template}")
    private String templateBucket;

    /**
     * 获取模板文件预览URL
     *
     * @param templateId 模板ID
     * @return 预览URL
     */
    @GetMapping("/{templateId}/preview-url")
    @ApiOperation("获取模板文件预览URL")
    @ApiImplicitParam(name = "templateId", value = "模板ID", required = true, paramType = "path", dataTypeClass = Long.class)
    public R<String> getPreviewUrl(@PathVariable Long templateId) {
        TemplateEntity template = templateService.getTemplateDetail(templateId);
        if (template == null) {
            return R.notFound("模板不存在");
        }

        // 检查文件是否存在
        if (template.getFilePath() == null || !minioUtils.objectExists(templateBucket, template.getFilePath())) {
            return R.failed("模板文件不存在");
        }

        // 生成临时访问URL（30分钟有效）
        String previewUrl = minioUtils.getFileUrl(templateBucket, template.getFilePath(), 30);
        
        // 增加下载统计
        templateService.incrementDownloads(templateId);
        
        return R.ok(previewUrl);
    }

    /**
     * 下载模板文件
     *
     * @param templateId 模板ID
     * @return 文件下载响应
     */
    @GetMapping("/{templateId}/download")
    @ApiOperation("下载模板文件")
    @ApiImplicitParam(name = "templateId", value = "模板ID", required = true, paramType = "path", dataTypeClass = Long.class)
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable Long templateId) {
        try {
            TemplateEntity template = templateService.getTemplateDetail(templateId);
            if (template == null) {
                return ResponseEntity.notFound().build();
            }

            // 检查文件是否存在
            if (template.getFilePath() == null || !minioUtils.objectExists(templateBucket, template.getFilePath())) {
                return ResponseEntity.notFound().build();
            }

            // 获取文件流
            InputStream fileStream = minioUtils.getObject(templateBucket, template.getFilePath());
            
            // 提取文件名（如果filePath包含完整路径）
            String fileName = template.getFilePath();
            if (fileName.contains("/")) {
                fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
            }
            
            // 设置Content-Disposition头，指定文件名
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", encodedFileName);
            
            // 尝试确定内容类型
            MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
            if (fileName.endsWith(".pdf")) {
                mediaType = MediaType.APPLICATION_PDF;
            } else if (fileName.endsWith(".docx")) {
                mediaType = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            }
            
            // 增加下载统计
            templateService.incrementDownloads(templateId);
            
            // 返回文件流
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(mediaType)
                    .body(new InputStreamResource(fileStream));
        } catch (Exception e) {
            log.error("下载模板文件失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }
} 