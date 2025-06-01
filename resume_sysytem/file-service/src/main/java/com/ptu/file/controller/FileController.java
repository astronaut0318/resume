package com.ptu.file.controller;

import com.ptu.file.dto.OnlyOfficeSaveDTO;
import com.ptu.file.service.FileService;
import com.ptu.file.vo.FileUploadVO;
import com.ptu.file.vo.FileVO;
import com.ptu.common.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

/**
 * 文件接口控制器
 * <p>
 * 提供文件上传、下载、删除、列表、OnlyOffice保存回调等API接口。
 * </p>
 */
@RestController
@RequestMapping
public class FileController {

    @Autowired
    private FileService fileService;

    /**
     * 文件上传接口
     * @param file 文件对象（multipart/form-data）
     * @param type 文件类型（template/resume/avatar）
     * @param bizId 业务ID（如模板ID、简历ID、用户ID，头像时为用户ID）
     * @param userId 用户ID（可从token或参数获取）
     * @return 文件上传响应VO
     */
    @PostMapping("/upload")
    public R<FileUploadVO> uploadFile(@RequestParam("file") MultipartFile file,
                                      @RequestParam("type") String type,
                                      @RequestParam("bizId") Long bizId,
                                      @RequestParam(value = "userId", required = false) Long userId) {
        // 参数校验可根据需要补充
        FileUploadVO vo = fileService.uploadFile(file, type, bizId, userId);
        return R.ok(vo, "上传成功");
    }

    /**
     * 文件下载接口
     * @param fileId 文件ID
     * @return 文件二进制流
     */
    @GetMapping("/{fileId}/download")
    public byte[] downloadFile(@PathVariable("fileId") Long fileId) {
        // 实际生产建议用ResponseEntity<byte[]>并设置Content-Type等
        return fileService.downloadFile(fileId);
    }

    /**
     * 删除文件接口
     * @param fileId 文件ID
     * @return 操作结果
     */
    @DeleteMapping("/{fileId}")
    public R<Void> deleteFile(@PathVariable("fileId") Long fileId) {
        boolean success = fileService.deleteFile(fileId);
        return success ? R.ok(null, "删除成功") : R.failed("删除失败");
    }

    /**
     * 文件列表接口
     * @param type 文件类型（template/resume/avatar）
     * @param bizId 业务ID（可选）
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 文件VO列表
     */
    @GetMapping("")
    public R<List<FileVO>> listFiles(@RequestParam("type") String type,
                                     @RequestParam(value = "bizId", required = false) Long bizId,
                                     @RequestParam("userId") Long userId,
                                     @RequestParam(value = "page", defaultValue = "1") int page,
                                     @RequestParam(value = "size", defaultValue = "10") int size) {
        List<FileVO> list = fileService.listFiles(type, bizId, userId, page, size);
        return R.ok(list, "操作成功");
    }

    /**
     * OnlyOffice文档保存回调接口
     * @param saveDTO OnlyOffice保存回调参数
     * @return 操作结果
     */
    @PostMapping("/onlyoffice/save")
    public R<Void> onlyOfficeSave(@RequestBody OnlyOfficeSaveDTO saveDTO) {
        boolean success = fileService.handleOnlyOfficeSave(saveDTO);
        return success ? R.ok(null, "保存成功") : R.failed("保存失败");
    }

    /**
     * 导出简历为Word
     * @param resumeId 简历ID
     * @return Word文件二进制流
     */
    @GetMapping("/resume/{resumeId}/word")
    public ResponseEntity<byte[]> exportResumeToWord(@PathVariable("resumeId") Long resumeId) {
        byte[] wordBytes = fileService.exportResumeToWord(resumeId); // 业务逻辑后续完善
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "resume_" + resumeId + ".docx");
        return ResponseEntity.ok()
                .headers(headers)
                .body(wordBytes);
    }
} 