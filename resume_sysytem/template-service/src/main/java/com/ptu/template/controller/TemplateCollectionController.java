package com.ptu.template.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ptu.common.api.R;
import com.ptu.template.service.TemplateCollectionService;
import com.ptu.template.vo.TemplateCollectionVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 模板收藏控制器
 */
@Slf4j
@Api(tags = "模板收藏接口")
@RestController
@RequestMapping("/templates/collections")
@RequiredArgsConstructor
public class TemplateCollectionController {

    @Autowired
    private TemplateCollectionService templateCollectionService;

    /**
     * 获取收藏列表
     *
     * @param userId 用户ID（从请求头中获取）
     * @param page   页码
     * @param size   每页大小
     * @return 收藏列表
     */
    @GetMapping
    @ApiOperation("获取收藏列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", paramType = "query", dataTypeClass = Integer.class, defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "每页大小", paramType = "query", dataTypeClass = Integer.class, defaultValue = "10")
    })
    public R<IPage<TemplateCollectionVO>> pageCollections(@RequestHeader("userId") Long userId,
                                                        @RequestParam(required = false, defaultValue = "1") Integer page,
                                                        @RequestParam(required = false, defaultValue = "10") Integer size) {
        IPage<TemplateCollectionVO> result = templateCollectionService.pageUserCollections(userId, page, size);
        return R.ok(result);
    }
} 