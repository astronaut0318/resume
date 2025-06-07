package com.ptu.document.common.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 文件工具类
 */
@Slf4j
public class FileUtils {

    /**
     * 创建最小的Word文档文件(docx)
     * 
     * @param path 文件路径
     * @throws IOException IO异常
     */
    public static void createMinimalWordDocument(Path path) throws IOException {
        // docx是一个zip文件，包含多个XML文件
        try (OutputStream fos = Files.newOutputStream(path);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            
            // 添加[Content_Types].xml
            ZipEntry contentTypes = new ZipEntry("[Content_Types].xml");
            zos.putNextEntry(contentTypes);
            String contentTypesXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                    "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">" +
                    "<Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>" +
                    "<Default Extension=\"xml\" ContentType=\"application/xml\"/>" +
                    "<Override PartName=\"/word/document.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml\"/>" +
                    "</Types>";
            zos.write(contentTypesXml.getBytes());
            zos.closeEntry();
            
            // 添加_rels/.rels
            ZipEntry rels = new ZipEntry("_rels/.rels");
            zos.putNextEntry(rels);
            String relsXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                    "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">" +
                    "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"word/document.xml\"/>" +
                    "</Relationships>";
            zos.write(relsXml.getBytes());
            zos.closeEntry();
            
            // 添加word/document.xml
            ZipEntry document = new ZipEntry("word/document.xml");
            zos.putNextEntry(document);
            String documentXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                    "<w:document xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\">" +
                    "<w:body><w:p><w:r><w:t>空白模板文件</w:t></w:r></w:p></w:body>" +
                    "</w:document>";
            zos.write(documentXml.getBytes());
            zos.closeEntry();
            
            // 添加word/_rels/document.xml.rels
            ZipEntry documentRels = new ZipEntry("word/_rels/document.xml.rels");
            zos.putNextEntry(documentRels);
            String documentRelsXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                    "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">" +
                    "</Relationships>";
            zos.write(documentRelsXml.getBytes());
            zos.closeEntry();
        }
        
        log.info("创建最小Word文档成功: {}", path);
    }
} 