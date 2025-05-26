package com.ptu.resume.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ptu.common.utils.R;
import com.ptu.resume.dto.ResumeDTO;
import com.ptu.resume.service.ResumeService;
import com.ptu.resume.vo.ResumeVO;
import com.ptu.resume.vo.ResumeVersionVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 简历控制器
 */
@Api(tags = "简历管理")
@RestController
@RequestMapping("/resumes")
public class ResumeController {

    @Autowired
    private ResumeService resumeService;

    @ApiOperation("创建简历")
    @PostMapping
    public R<Long> createResume(@RequestBody @Valid ResumeDTO resumeDTO) {
        Long resumeId = resumeService.createResume(resumeDTO);
        return R.ok(resumeId);
    }

    @ApiOperation("获取简历列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "userId", value = "用户ID", required = true, paramType = "path"),
        @ApiImplicitParam(name = "page", value = "页码", defaultValue = "1"),
        @ApiImplicitParam(name = "size", value = "每页数量", defaultValue = "10")
    })
    @GetMapping("/user/{userId}")
    public R<IPage<ResumeVO>> getUserResumes(
            @PathVariable("userId") Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        IPage<ResumeVO> pageResult = resumeService.pageUserResumes(userId, page, size);
        return R.ok(pageResult);
    }

    @ApiOperation("获取简历详情")
    @ApiImplicitParam(name = "id", value = "简历ID", required = true, paramType = "path")
    @GetMapping("/{id}")
    public R<ResumeVO> getResumeDetail(@PathVariable("id") Long id) {
        ResumeVO resumeVO = resumeService.getResumeDetail(id);
        return R.ok(resumeVO);
    }

    @ApiOperation("获取用户默认简历")
    @ApiImplicitParam(name = "userId", value = "用户ID", required = true, paramType = "path")
    @GetMapping("/default/{userId}")
    public R<ResumeVO> getUserDefaultResume(@PathVariable("userId") Long userId) {
        ResumeVO resumeVO = resumeService.getUserDefaultResume(userId);
        return R.ok(resumeVO);
    }

    @ApiOperation("更新简历")
    @PutMapping("/{id}")
    public R<Boolean> updateResume(@PathVariable("id") Long id, @RequestBody @Valid ResumeDTO resumeDTO) {
        resumeDTO.setId(id);
        boolean result = resumeService.updateResume(resumeDTO);
        return R.ok(result);
    }

    @ApiOperation("删除简历")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "简历ID", required = true, paramType = "path"),
        @ApiImplicitParam(name = "userId", value = "用户ID", required = true)
    })
    @DeleteMapping("/{id}")
    public R<Boolean> deleteResume(@PathVariable("id") Long id, @RequestParam Long userId) {
        boolean result = resumeService.deleteResume(id, userId);
        return R.ok(result);
    }

    @ApiOperation("设置默认简历")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "简历ID", required = true, paramType = "path"),
        @ApiImplicitParam(name = "userId", value = "用户ID", required = true)
    })
    @PutMapping("/{id}/default")
    public R<Boolean> setDefaultResume(@PathVariable("id") Long id, @RequestParam Long userId) {
        boolean result = resumeService.setDefaultResume(id, userId);
        return R.ok(result);
    }

    @ApiOperation("获取简历版本历史")
    @ApiImplicitParam(name = "resumeId", value = "简历ID", required = true, paramType = "path")
    @GetMapping("/{resumeId}/versions")
    public R<List<ResumeVersionVO>> listResumeVersions(@PathVariable("resumeId") Long resumeId) {
        List<ResumeVersionVO> versions = resumeService.listResumeVersions(resumeId);
        return R.ok(versions);
    }

    @ApiOperation("获取指定版本的简历内容")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "resumeId", value = "简历ID", required = true, paramType = "path"),
        @ApiImplicitParam(name = "version", value = "版本号", required = true, paramType = "path")
    })
    @GetMapping("/{resumeId}/versions/{version}")
    public R<ResumeVersionVO> getResumeVersion(
            @PathVariable("resumeId") Long resumeId,
            @PathVariable("version") Integer version) {
        ResumeVersionVO versionVO = resumeService.getResumeVersion(resumeId, version);
        return R.ok(versionVO);
    }
} 