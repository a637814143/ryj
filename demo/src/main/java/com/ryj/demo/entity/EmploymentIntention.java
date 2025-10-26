package com.ryj.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("employment_intention")
public class EmploymentIntention {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long studentId;
    
    private String expectedPosition;
    
    private String salaryRange;
    
    private WorkType workType;
    
    private String notes;
    
    public enum WorkType {
        FULL_TIME,
        PART_TIME,
        INTERNSHIP,
        FLEXIBLE
    }
}
