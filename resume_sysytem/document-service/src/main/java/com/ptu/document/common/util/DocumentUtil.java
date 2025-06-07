package com.ptu.document.common.util;

import com.ptu.document.config.OnlyOfficeConfig;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 文档工具类
 */
public class DocumentUtil {

    /**
     * 文档类型：文本
     */
    public static final String TYPE_TEXT = "text";
    
    /**
     * 文档类型：电子表格
     */
    public static final String TYPE_SPREADSHEET = "spreadsheet";
    
    /**
     * 文档类型：演示文稿
     */
    public static final String TYPE_PRESENTATION = "presentation";
    
    /**
     * 编辑模式
     */
    public static final String MODE_EDIT = "edit";
    
    /**
     * 查看模式
     */
    public static final String MODE_VIEW = "view";

    /**
     * 根据文件扩展名获取文档类型
     * @param fileName 文件名
     * @param config OnlyOffice配置
     * @return 文档类型
     */
    public static String getDocumentType(String fileName, OnlyOfficeConfig config) {
        if (!StringUtils.hasText(fileName)) {
            return null;
        }
        
        String ext = getFileExtension(fileName).toLowerCase();
        
        Map<String, String> formats = config.getFormats().getEditable();
        if (formats == null) {
            return getDefaultDocumentType(ext);
        }
        
        for (Map.Entry<String, String> entry : formats.entrySet()) {
            List<String> exts = getExtensions(entry.getValue());
            if (exts.contains(ext)) {
                return entry.getKey();
            }
        }
        
        return getDefaultDocumentType(ext);
    }
    
    /**
     * 根据默认规则获取文档类型
     * @param ext 文件扩展名
     * @return 文档类型
     */
    private static String getDefaultDocumentType(String ext) {
        if (Arrays.asList("doc", "docx", "odt", "rtf", "txt", "html", "htm", "pdf").contains(ext)) {
            return TYPE_TEXT;
        } else if (Arrays.asList("xls", "xlsx", "ods", "csv").contains(ext)) {
            return TYPE_SPREADSHEET;
        } else if (Arrays.asList("ppt", "pptx", "odp").contains(ext)) {
            return TYPE_PRESENTATION;
        }
        return null;
    }
    
    /**
     * 获取文件扩展名
     * @param fileName 文件名
     * @return 扩展名
     */
    public static String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
    
    /**
     * 将扩展名字符串拆分为列表
     * @param extensions 扩展名字符串（逗号分隔）
     * @return 扩展名列表
     */
    public static List<String> getExtensions(String extensions) {
        if (!StringUtils.hasText(extensions)) {
            return java.util.Collections.emptyList();
        }
        return Arrays.stream(extensions.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }
    
    /**
     * 检查文件是否可编辑
     * @param fileName 文件名
     * @param config OnlyOffice配置
     * @return 是否可编辑
     */
    public static boolean isEditable(String fileName, OnlyOfficeConfig config) {
        if (!StringUtils.hasText(fileName)) {
            return false;
        }
        
        String ext = getFileExtension(fileName).toLowerCase();
        String type = getDocumentType(fileName, config);
        
        if (type == null) {
            return false;
        }
        
        Map<String, String> formats = config.getFormats().getEditable();
        if (formats == null) {
            return false;
        }
        
        String extensions = formats.get(type);
        return getExtensions(extensions).contains(ext);
    }
    
    /**
     * 检查是否为有损编辑格式
     * @param fileName 文件名
     * @param config OnlyOffice配置
     * @return 是否为有损编辑格式
     */
    public static boolean isLossyEditFormat(String fileName, OnlyOfficeConfig config) {
        if (!StringUtils.hasText(fileName) || !StringUtils.hasText(config.getLossyEditFormats())) {
            return false;
        }
        
        String ext = getFileExtension(fileName).toLowerCase();
        List<String> lossyFormats = getExtensions(config.getLossyEditFormats());
        
        return lossyFormats.contains(ext);
    }
    
    /**
     * 获取文档编辑器Key
     * @param docType 文档类型
     * @return 编辑器Key (必须是 word/cell/slide/pdf 之一)
     */
    public static String getDocumentEditorKey(String docType) {
        Map<String, String> map = new HashMap<>();
        map.put(TYPE_TEXT, "word");
        map.put(TYPE_SPREADSHEET, "cell");
        map.put(TYPE_PRESENTATION, "slide");
        
        return map.getOrDefault(docType, "word");
    }
} 