package com.ptu.file.controller;

import com.ptu.file.dto.OnlyOfficeSaveDTO;
import com.ptu.file.service.FileService;
import com.ptu.file.vo.FileUploadVO;
import com.ptu.file.vo.FileVO;
import com.ptu.common.api.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.ArrayList;

/**
 * 文件接口控制器
 * <p>
 * 提供文件上传、下载、删除、列表、OnlyOffice保存回调等API接口。
 * </p>
 */
@RestController
@RequestMapping
public class FileController {

    private static final Logger log = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileService fileService;

    /**
     * 文件上传接口
     * @param file 文件对象（multipart/form-data）
     * @param type 文件类型（已废弃，系统会自动识别：图片文件归类为thumbnail，非图片文件归类为template）
     * @param bizId 业务ID（如模板ID、简历ID）
     * @param userId 用户ID（可从token或参数获取）
     * @return 文件上传响应VO
     */
    @PostMapping("/upload")
    public R<FileUploadVO> uploadFile(@RequestParam("file") MultipartFile file,
                                      @RequestParam(value = "type", required = false, defaultValue = "") String type,
                                      @RequestParam(value = "bizId", required = false, defaultValue = "0") Long bizId,
                                      @RequestParam(value = "userId", required = true) Long userId) {
        // 类型参数现在已废弃，但保留参数以兼容现有客户端调用
        FileUploadVO vo = fileService.uploadFile(file, type, bizId, userId, null);
        return R.ok(vo, "上传成功");
    }

    /**
     * 文件下载接口
     * @param fileId 文件ID
     * @return 文件二进制流
     */
    @GetMapping("/{fileId}/download")
    public ResponseEntity<byte[]> downloadFile(@PathVariable("fileId") Long fileId) {
        byte[] fileBytes = fileService.downloadFile(fileId);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "file_" + fileId);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(fileBytes);
    }

    /**
     * 删除文件接口
     * @param fileId 文件ID
     * @return 操作结果
     */
    @DeleteMapping("/de/{fileId}")
    public R<Void> deleteFile(@PathVariable("fileId") Long fileId) {
        log.info("收到DELETE方法删除文件请求，fileId: {}", fileId);
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
    @GetMapping
    public R<List<FileVO>> listFiles(@RequestParam(value = "type", required = false) String type,
                                     @RequestParam(value = "bizId", required = false) Long bizId,
                                     @RequestParam(value = "userId", required = true) Long userId,
                                     @RequestParam(value = "page", defaultValue = "1") int page,
                                     @RequestParam(value = "size", defaultValue = "10") int size) {
        // 记录请求参数
        log.info("接收到文件列表请求: type={}, bizId={}, userId={}, page={}, size={}", 
                type, bizId, userId, page, size);

        try {
            // 不使用type过滤，直接查询该用户所有文件
            log.info("查询所有类型的文件，不应用type过滤");
            
            // 直接查询数据库，确认用户是否有文件记录
            long totalRecords = fileService.countUserFiles(userId);
            log.info("用户 {} 的文件总数: {}", userId, totalRecords);
            
            if (totalRecords == 0) {
                // 用户没有任何文件记录，直接返回空列表
                log.warn("用户 {} 没有任何文件记录", userId);
                return R.ok(new ArrayList<>(), "用户没有文件");
            }
            
            // 调用服务层方法获取文件列表 - 不传type参数，获取所有类型
            List<FileVO> list = fileService.listFiles(null, bizId, userId, page, size);
            
            // 记录返回结果
            log.info("文件列表查询完成，返回{}条记录", list != null ? list.size() : 0);
            
            // 如果列表为空但数据库有记录，可能是URL生成问题
            if ((list == null || list.isEmpty()) && totalRecords > 0) {
                // 尝试获取所有文件并手动构建URL
                log.warn("查询结果为空但数据库有记录，尝试手动构建文件列表");
                list = fileService.getAllUserFilesWithUrl(userId);
                log.info("手动构建的文件列表大小: {}", list.size());
            }
            
            // 确保返回的list不为null
            List<FileVO> safeList = list != null ? list : new ArrayList<>();
            
            // 检查每个文件记录是否有URL
            for (int i = 0; i < safeList.size(); i++) {
                FileVO vo = safeList.get(i);
                if (vo.getFileUrl() == null || vo.getFileUrl().isEmpty()) {
                    // 文件URL为空，尝试重新获取
                    String fileUrl = fileService.getFileUrlById(vo.getId());
                    if (fileUrl != null) {
                        vo.setFileUrl(fileUrl);
                        log.info("为文件ID={}补充URL: {}", vo.getId(), fileUrl);
                    } else {
                        log.warn("无法为文件ID={}获取URL", vo.getId());
                    }
                }
            }
            
            return R.ok(safeList, "操作成功");
        } catch (Exception e) {
            log.error("获取文件列表出错", e);
            return R.failed("获取文件列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取文件URL接口
     * @param fileId 文件ID
     * @return 文件URL
     */
    @GetMapping("/{fileId}/url")
    public R<String> getFileUrl(@PathVariable("fileId") Long fileId) {
        String fileUrl = fileService.getFileUrlById(fileId);
        return fileUrl != null ? R.ok(fileUrl, "获取成功") : R.failed("获取失败");
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