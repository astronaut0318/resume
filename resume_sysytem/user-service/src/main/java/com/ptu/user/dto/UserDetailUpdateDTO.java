package com.ptu.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.time.LocalDate;

/**
 * 用户详细信息更新数据传输对象
 */
@Data
public class UserDetailUpdateDTO {

    /**
     * 真实姓名
     */
    @Size(max = 50, message = "真实姓名长度不能超过50个字符")
    private String realName;

    /**
     * 性别：0-未知，1-男，2-女
     */
    @Min(value = 0, message = "性别不合法")
    @Max(value = 2, message = "性别不合法")
    private Integer gender;

    /**
     * 出生日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    /**
     * 学历
     */
    @Size(max = 50, message = "学历长度不能超过50个字符")
    private String education;

    /**
     * 工作年限
     */
    @Min(value = 0, message = "工作年限不能小于0")
    @Max(value = 100, message = "工作年限不合法")
    private Integer workYears;

    /**
     * 地址
     */
    @Size(max = 255, message = "地址长度不能超过255个字符")
    private String address;

    /**
     * 个人简介
     */
    @Size(max = 1000, message = "个人简介长度不能超过1000个字符")
    private String profile;
} 