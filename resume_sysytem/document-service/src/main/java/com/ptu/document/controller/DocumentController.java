package com.ptu.document.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptu.common.api.R;
import com.ptu.document.common.constants.DocumentConstant;
import com.ptu.document.dto.DocumentEditorConfig;
import com.ptu.document.service.DocumentService;
import com.ptu.document.vo.DocumentVersionVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文档控制器
 */
@Slf4j
@Api(tags = "文档服务接口")
@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {

    /**
     * 导出简历为Word
     * @param resumeId 简历ID
     * @param userId 用户ID
     * @param response 响应
     */
    @ApiOperation("导出简历为Word")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "resumeId", value = "简历ID", required = true, paramType = "path"),
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true, paramType = "query")
    })
    @GetMapping("/resume/{resumeId}/export/word")
    public void exportResumeAsWord(@PathVariable Long resumeId, @RequestParam Long userId, HttpServletResponse response) throws IOException {
        try (InputStream inputStream = documentService.exportResumeAsWord(resumeId, userId)) {
            if (inputStream == null) {
                response.sendError(HttpStatus.NOT_FOUND.value(), "简历不存在或无法导出");
                return;
            }
            response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            response.setHeader("Content-Disposition", "attachment; filename=resume_" + resumeId + ".docx");
            StreamUtils.copy(inputStream, response.getOutputStream());
        } catch (Exception e) {
            log.error("导出简历为Word异常: {}", e.getMessage(), e);
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "导出简历为Word失败");
        }
    }

    /**
     * 导出简历为PDF
     * @param resumeId 简历ID
     * @param userId 用户ID
     * @param response 响应
     */
    @ApiOperation("导出简历为PDF")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "resumeId", value = "简历ID", required = true, paramType = "path"),
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true, paramType = "query")
    })
    @GetMapping("/resume/{resumeId}/export/pdf")
    public void exportResumeAsPdf(@PathVariable Long resumeId, @RequestParam Long userId, HttpServletResponse response) throws IOException {
        try (InputStream inputStream = documentService.exportResumeAsPdf(resumeId, userId)) {
            if (inputStream == null) {
                response.sendError(HttpStatus.NOT_FOUND.value(), "简历不存在或无法导出");
                return;
            }
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=resume_" + resumeId + ".pdf");
            StreamUtils.copy(inputStream, response.getOutputStream());
        } catch (Exception e) {
            log.error("导出简历为PDF异常: {}", e.getMessage(), e);
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "导出简历为PDF失败");
        }
    }


    private final DocumentService documentService;
    private final ObjectMapper objectMapper;

    /**
     * 获取文档编辑器配置
     *
     * @param sourceType 来源类型
     * @param sourceId 来源ID
     * @param mode 编辑模式
     * @param userId 用户ID
     * @param userName 用户名称
     * @return 编辑器配置
     */
    @ApiOperation("获取文档编辑器配置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sourceType", value = "来源类型", required = true, paramType = "path"),
            @ApiImplicitParam(name = "sourceId", value = "来源ID", required = true, paramType = "path"),
            @ApiImplicitParam(name = "mode", value = "编辑模式", required = true, paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true, paramType = "query"),
            @ApiImplicitParam(name = "userName", value = "用户名称", required = false, paramType = "query")
    })
    @GetMapping("/{sourceType}/{sourceId}/config")
    public R<DocumentEditorConfig> getEditorConfig(
            @PathVariable String sourceType,
            @PathVariable Long sourceId,
            @RequestParam String mode,
            @RequestParam Long userId,
            @RequestParam(required = false) String userName) {
        
        // 检查参数
        if (!StringUtils.hasText(sourceType) || sourceId == null || userId == null) {
            return R.paramError("参数错误");
        }
        
        // 检查模式
        if (!DocumentConstant.MODE_VIEW.equals(mode) && !DocumentConstant.MODE_EDIT.equals(mode)
                && !DocumentConstant.MODE_COMMENT.equals(mode)) {
            return R.paramError("不支持的模式");
        }
        
        // 获取编辑器配置
        DocumentEditorConfig config = documentService.getEditorConfig(sourceType, sourceId, mode, userId, userName);
        if (config == null) {
            return R.error("获取编辑器配置失败");
        }
        
        return R.ok(config);
    }
    
    /**
     * 文档回调处理
     *
     * @param request 请求
     * @return 处理结果
     */
    @ApiOperation("文档回调处理")
    @PostMapping("/callback")
    public Map<String, Object> handleCallback(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 读取请求体
            String body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
            
            // 处理回调
            boolean success = documentService.handleCallback(body);
            
            // 返回结果
            result.put("error", success ? 0 : 1);
            
            return result;
        } catch (Exception e) {
            log.error("处理回调异常: {}", e.getMessage(), e);
            result.put("error", 1);
            return result;
        }
    }
    
    /**
     * 创建文档版本
     *
     * @param sourceType 来源类型
     * @param sourceId 来源ID
     * @param userId 用户ID
     * @return 版本ID
     */
    @ApiOperation("创建文档版本")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sourceType", value = "来源类型", required = true, paramType = "path"),
            @ApiImplicitParam(name = "sourceId", value = "来源ID", required = true, paramType = "path"),
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true, paramType = "query")
    })
    @PostMapping("/{sourceType}/{sourceId}/versions")
    public R<Long> createVersion(
            @PathVariable String sourceType,
            @PathVariable Long sourceId,
            @RequestParam Long userId) {
        
        // 检查参数
        if (!StringUtils.hasText(sourceType) || sourceId == null || userId == null) {
            return R.paramError("参数错误");
        }
        
        // 创建版本
        Long versionId = documentService.createDocumentVersion(sourceType, sourceId, userId);
        if (versionId == null) {
            return R.error("创建版本失败");
        }
        
        return R.ok(versionId);
    }
    
    /**
     * 获取文档版本列表
     *
     * @param sourceType 来源类型
     * @param sourceId 来源ID
     * @return 版本列表
     */
    @ApiOperation("获取文档版本列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sourceType", value = "来源类型", required = true, paramType = "path"),
            @ApiImplicitParam(name = "sourceId", value = "来源ID", required = true, paramType = "path")
    })
    @GetMapping("/{sourceType}/{sourceId}/versions")
    public R<List<DocumentVersionVO>> getVersionList(
            @PathVariable String sourceType,
            @PathVariable Long sourceId) {
        
        // 检查参数
        if (!StringUtils.hasText(sourceType) || sourceId == null) {
            return R.paramError("参数错误");
        }
        
        // 获取版本列表
        List<DocumentVersionVO> versions = documentService.getVersionList(sourceType, sourceId);
        
        return R.ok(versions);
    }
    
    /**
     * 获取文档版本详情
     *
     * @param versionId 版本ID
     * @return 版本详情
     */
    @ApiOperation("获取文档版本详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "versionId", value = "版本ID", required = true, paramType = "path")
    })
    @GetMapping("/versions/{versionId}")
    public R<DocumentVersionVO> getVersion(@PathVariable Long versionId) {
        
        // 检查参数
        if (versionId == null) {
            return R.paramError("参数错误");
        }
        
        // 获取版本详情
        DocumentVersionVO version = documentService.getVersion(versionId);
        if (version == null) {
            return R.notFound("版本不存在");
        }
        
        return R.ok(version);
    }
    
    /**
     * 预览文档版本
     *
     * @param versionId 版本ID
     * @param userId 用户ID
     * @param userName 用户名称
     * @return 预览配置
     */
    @ApiOperation("预览文档版本")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "versionId", value = "版本ID", required = true, paramType = "path"),
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true, paramType = "query"),
            @ApiImplicitParam(name = "userName", value = "用户名称", required = false, paramType = "query")
    })
    @GetMapping("/versions/{versionId}/preview")
    public R<DocumentEditorConfig> previewVersion(
            @PathVariable Long versionId,
            @RequestParam Long userId,
            @RequestParam(required = false) String userName) {
        
        // 检查参数
        if (versionId == null || userId == null) {
            return R.paramError("参数错误");
        }
        
        // 获取预览配置
        DocumentEditorConfig config = documentService.previewVersion(versionId, userId, userName);
        if (config == null) {
            return R.error("获取预览配置失败");
        }
        
        return R.ok(config);
    }
    
    /**
     * 下载文档
     *
     * @param sourceType 来源类型
     * @param sourceId 来源ID
     * @param response 响应
     */
    @ApiOperation("下载文档")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sourceType", value = "来源类型", required = true, paramType = "path"),
            @ApiImplicitParam(name = "sourceId", value = "来源ID", required = true, paramType = "path")
    })
    @GetMapping("/{sourceType}/{sourceId}/download")
    public ResponseEntity<byte[]> downloadDocument(
            @PathVariable String sourceType,
            @PathVariable Long sourceId,
            HttpServletResponse response) throws IOException {
        
        // 检查参数
        if (!StringUtils.hasText(sourceType) || sourceId == null) {
            response.sendError(HttpStatus.BAD_REQUEST.value(), "参数错误");
            return null;
        }
        
        // 获取文档流
        try (InputStream inputStream = documentService.downloadDocument(sourceType, sourceId)) {
            if (inputStream == null) {
                response.sendError(HttpStatus.NOT_FOUND.value(), "文档不存在或无法访问");
                return null;
            }
            
            // 获取文件名
            String fileName = "document";
            if (DocumentConstant.SOURCE_TYPE_TEMPLATE.equals(sourceType)) {
                fileName = "template_" + sourceId;
            } else if (DocumentConstant.SOURCE_TYPE_RESUME.equals(sourceType)) {
                fileName = "resume_" + sourceId;
            } else if (DocumentConstant.SOURCE_TYPE_FILE.equals(sourceType)) {
                fileName = "file_" + sourceId;
            }
            
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()));
            
            // 返回文件流
            return new ResponseEntity<>(StreamUtils.copyToByteArray(inputStream), headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("下载文档异常: {}", e.getMessage(), e);
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "下载文档失败");
            return null;
        }
    }
    
    /**
     * 下载文档版本
     *
     * @param versionId 版本ID
     * @param response 响应
     */
    @ApiOperation("下载文档版本")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "versionId", value = "版本ID", required = true, paramType = "path")
    })
    @GetMapping("/versions/{versionId}/download")
    public ResponseEntity<byte[]> downloadVersion(
            @PathVariable Long versionId,
            HttpServletResponse response) throws IOException {
        
        // 检查参数
        if (versionId == null) {
            response.sendError(HttpStatus.BAD_REQUEST.value(), "参数错误");
            return null;
        }
        
        // 获取文档版本流
        try (InputStream inputStream = documentService.downloadVersion(versionId)) {
            if (inputStream == null) {
                response.sendError(HttpStatus.NOT_FOUND.value(), "版本不存在或无法访问");
                return null;
            }
            
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", URLEncoder.encode("version_" + versionId, StandardCharsets.UTF_8.name()));
            
            // 返回文件流
            return new ResponseEntity<>(StreamUtils.copyToByteArray(inputStream), headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("下载文档版本异常: {}", e.getMessage(), e);
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "下载文档版本失败");
            return null;
        }
    }
} 