package com.ryj.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("employer")
public class Employer {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String companyName;
    private String contactPerson;
    private String contactEmail;
    private String contactPhone;
    private String description;
    private String website;
}
