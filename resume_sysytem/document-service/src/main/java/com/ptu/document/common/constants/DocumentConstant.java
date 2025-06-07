package com.ptu.document.common.constants;

/**
 * 文档常量类
 */
public class DocumentConstant {
    
    /**
     * 来源类型：简历
     */
    public static final String SOURCE_TYPE_RESUME = "RESUME";
    
    /**
     * 来源类型：模板
     */
    public static final String SOURCE_TYPE_TEMPLATE = "TEMPLATE";
    
    /**
     * 来源类型：普通文件
     */
    public static final String SOURCE_TYPE_FILE = "FILE";
    
    /**
     * 文档类型：Word
     */
    public static final String DOC_TYPE_WORD = "word";
    
    /**
     * 文档类型：Excel
     */
    public static final String DOC_TYPE_EXCEL = "cell";
    
    /**
     * 文档类型：PowerPoint
     */
    public static final String DOC_TYPE_PPTX = "slide";
    
    /**
     * 编辑器模式：查看
     */
    public static final String MODE_VIEW = "view";
    
    /**
     * 编辑器模式：编辑
     */
    public static final String MODE_EDIT = "edit";
    
    /**
     * 编辑器模式：评论
     */
    public static final String MODE_COMMENT = "comment";
    
    /**
     * 回调状态：文档已准备就绪
     */
    public static final int CALLBACK_STATUS_READY = 0;
    
    /**
     * 回调状态：文档已准备就绪（强制保存模式）
     */
    public static final int CALLBACK_STATUS_FORCE_SAVE_READY = 1;
    
    /**
     * 回调状态：保存成功
     */
    public static final int CALLBACK_STATUS_SAVED = 2;
    
    /**
     * 回调状态：文档已编辑
     */
    public static final int CALLBACK_STATUS_EDITED = 3;
    
    /**
     * 回调状态：保存失败
     */
    public static final int CALLBACK_STATUS_FAILED = 4;
    
    /**
     * 编辑记录：打开
     */
    public static final String ACTION_OPEN = "OPEN";
    
    /**
     * 编辑记录：编辑
     */
    public static final String ACTION_EDIT = "EDIT";
    
    /**
     * 编辑记录：保存
     */
    public static final String ACTION_SAVE = "SAVE";
    
    /**
     * 编辑记录：关闭
     */
    public static final String ACTION_CLOSE = "CLOSE";
} 