package com.ptu.resume.core.model;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果封装
 *
 * @author PTU开发团队
 */
public class PageResult<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 总记录数
     */
    private long total;
    
    /**
     * 当前页数据
     */
    private List<T> list;
    
    /**
     * 当前页码
     */
    private int pageNum;
    
    /**
     * 每页条数
     */
    private int pageSize;
    
    public PageResult() {
    }
    
    public PageResult(List<T> list, long total) {
        this.list = list;
        this.total = total;
    }
    
    public PageResult(List<T> list, long total, int pageNum, int pageSize) {
        this.list = list;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }
    
    public long getTotal() {
        return total;
    }
    
    public void setTotal(long total) {
        this.total = total;
    }
    
    public List<T> getList() {
        return list;
    }
    
    public void setList(List<T> list) {
        this.list = list;
    }
    
    public int getPageNum() {
        return pageNum;
    }
    
    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }
    
    public int getPageSize() {
        return pageSize;
    }
    
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
} 