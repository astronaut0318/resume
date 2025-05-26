package com.ptu.common.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页工具类
 */
public class PageUtils {
    
    /**
     * 将分页结果转换为指定类型
     * 
     * @param page 原始分页结果
     * @param targetClass 目标类型
     * @param <T> 原始类型
     * @param <R> 目标类型
     * @return 转换后的分页结果
     */
    public static <T, R> IPage<R> convertPage(IPage<T> page, Class<R> targetClass) {
        try {
            // 创建新的分页对象
            IPage<R> resultPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
            
            // 转换Records
            List<R> records = new ArrayList<>(page.getRecords().size());
            for (T item : page.getRecords()) {
                R targetInstance = targetClass.getDeclaredConstructor().newInstance();
                BeanUtils.copyProperties(item, targetInstance);
                records.add(targetInstance);
            }
            
            // 设置转换后的结果
            resultPage.setRecords(records);
            return resultPage;
        } catch (Exception e) {
            throw new RuntimeException("转换分页数据失败", e);
        }
    }
    
    /**
     * 转换列表
     * 
     * @param sourceList 源列表
     * @param targetClass 目标类型
     * @param <T> 源类型
     * @param <R> 目标类型
     * @return 转换后的列表
     */
    public static <T, R> List<R> convertList(List<T> sourceList, Class<R> targetClass) {
        try {
            List<R> resultList = new ArrayList<>(sourceList.size());
            for (T item : sourceList) {
                R targetInstance = targetClass.getDeclaredConstructor().newInstance();
                BeanUtils.copyProperties(item, targetInstance);
                resultList.add(targetInstance);
            }
            return resultList;
        } catch (Exception e) {
            throw new RuntimeException("转换列表数据失败", e);
        }
    }
} 