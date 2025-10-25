package com.ryj.employment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ryj.employment.entity.Student;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学生Mapper接口
 */
@Mapper
public interface StudentMapper extends BaseMapper<Student> {
    // MyBatis-Plus提供了基本的CRUD方法
    // 如需自定义SQL，可以在这里添加方法
}

