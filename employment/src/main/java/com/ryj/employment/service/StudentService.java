package com.ryj.employment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ryj.employment.entity.Student;

/**
 * 学生服务接口
 */
public interface StudentService extends IService<Student> {
    
    /**
     * 根据用户ID获取学生信息
     * @param userId 用户ID
     * @return 学生信息
     */
    Student getByUserId(Integer userId);
    
    /**
     * 保存或更新学生信息
     * @param student 学生信息
     * @return 保存后的学生信息
     */
    Student saveOrUpdateStudent(Student student);
    
    /**
     * 根据用户ID删除学生信息
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean deleteByUserId(Integer userId);
}

