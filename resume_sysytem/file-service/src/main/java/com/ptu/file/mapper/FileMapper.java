package com.ptu.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ptu.file.entity.FileEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;
import java.util.List;
import java.util.Map;

@Mapper
public interface FileMapper extends BaseMapper<FileEntity> {
    
    /**
     * 使用原生SQL通过ID查询文件
     * @param fileId 文件ID（字符串形式）
     * @return 文件实体
     */
    @Select("SELECT * FROM files WHERE CAST(id AS CHAR) LIKE CONCAT('0', LEFT(#{fileId}, 10), '%')")
    FileEntity selectByRawId(@Param("fileId") String fileId);
    
    /**
     * 使用原生SQL删除文件
     * @param fileId 文件ID（字符串形式）
     * @return 影响的行数
     */
    @Delete("DELETE FROM files WHERE CAST(id AS CHAR) LIKE CONCAT('0', LEFT(#{fileId}, 10), '%')")
    int deleteByRawId(@Param("fileId") String fileId);
    
    /**
     * 获取样本记录，用于诊断
     * @return 样本记录列表
     */
    @Select("SELECT * FROM files LIMIT 5")
    List<Map<String, Object>> getSampleRecord();
    
    /**
     * 获取表结构信息
     * @return 表结构信息
     */
    @Select("SHOW COLUMNS FROM files")
    List<Map<String, Object>> getTableStructure();
}
