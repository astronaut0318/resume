package com.ptu.template.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ptu.common.api.R;
import com.ptu.template.common.query.TemplateQuery;
import com.ptu.template.entity.TemplateEntity;
import com.ptu.template.service.TemplateService;
import com.ptu.template.vo.ShareLinkVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 模板控制器
 */
@Slf4j
@Api(tags = "模板接口")
@RestController
@RequestMapping("/templates")
@RequiredArgsConstructor
public class TemplateController {

    @Autowired
    private TemplateService templateService;

    /**
     * 获取模板列表
     *
     * @param query 查询条件
     * @return 模板列表
     */
    @GetMapping
    @ApiOperation("获取模板列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryId", value = "分类ID", paramType = "query", dataTypeClass = Long.class),
            @ApiImplicitParam(name = "isFree", value = "是否免费（0-付费，1-免费）", paramType = "query", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "keyword", value = "搜索关键词", paramType = "query", dataTypeClass = String.class),
            @ApiImplicitParam(name = "page", value = "页码", paramType = "query", dataTypeClass = Integer.class, defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "每页大小", paramType = "query", dataTypeClass = Integer.class, defaultValue = "10")
    })
    public R<IPage<TemplateEntity>> pageTemplates(TemplateQuery query) {
        IPage<TemplateEntity> page = templateService.pageTemplates(query);
        return R.ok(page);
    }

    /**
     * 获取模板详情
     *
     * @param id 模板ID
     * @return 模板详情
     */
    @GetMapping("/{id}")
    @ApiOperation("获取模板详情")
    @ApiImplicitParam(name = "id", value = "模板ID", required = true, paramType = "path", dataTypeClass = Long.class)
    public R<TemplateEntity> getTemplate(@PathVariable Long id) {
        TemplateEntity template = templateService.getTemplateDetail(id);
        if (template == null) {
            return R.notFound();
        }
        return R.ok(template);
    }

    /**
     * 收藏模板
     *
     * @param id     模板ID
     * @param userId 用户ID（从请求头中获取）
     * @return 操作结果
     */
    @PostMapping("/{id}/collect")
    @ApiOperation("收藏模板")
    @ApiImplicitParam(name = "id", value = "模板ID", required = true, paramType = "path", dataTypeClass = Long.class)
    public R<Void> collectTemplate(@PathVariable Long id, @RequestHeader("userId") Long userId) {
        boolean success = templateService.collectTemplate(id, userId);
        return success ? R.ok() : R.failed("收藏失败");
    }

    /**
     * 取消收藏
     *
     * @param id     模板ID
     * @param userId 用户ID（从请求头中获取）
     * @return 操作结果
     */
    @DeleteMapping("/{id}/collect")
    @ApiOperation("取消收藏")
    @ApiImplicitParam(name = "id", value = "模板ID", required = true, paramType = "path", dataTypeClass = Long.class)
    public R<Void> cancelCollect(@PathVariable Long id, @RequestHeader("userId") Long userId) {
        boolean success = templateService.cancelCollect(id, userId);
        return success ? R.ok() : R.failed("取消收藏失败");
    }

    /**
     * 生成分享链接
     *
     * @param id 模板ID
     * @return 分享链接信息
     */
    @GetMapping("/{id}/share")
    @ApiOperation("生成模板分享链接")
    @ApiImplicitParam(name = "id", value = "模板ID", required = true, paramType = "path", dataTypeClass = Long.class)
    public R<ShareLinkVO> generateShareLink(@PathVariable Long id) {
        ShareLinkVO shareLink = templateService.generateShareLink(id);
        if (shareLink == null) {
            return R.notFound();
        }
        return R.ok(shareLink);
    }
} 