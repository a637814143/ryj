package com.ryj.employment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ryj.employment.entity.Student;
import com.ryj.employment.mapper.StudentMapper;
import com.ryj.employment.service.StudentService;
import org.springframework.stereotype.Service;

/**
 * 学生服务实现类
 */
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {
    
    @Override
    public Student getByUserId(Integer userId) {
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return this.getOne(queryWrapper);
    }
    
    @Override
    public Student saveOrUpdateStudent(Student student) {
        // 如果有studentId，则更新；否则新增
        if (student.getStudentId() != null && student.getStudentId() > 0) {
            this.updateById(student);
        } else {
            // 检查该用户是否已有学生信息
            if (student.getUserId() != null) {
                Student existStudent = this.getByUserId(student.getUserId());
                if (existStudent != null) {
                    // 已存在，更新
                    student.setStudentId(existStudent.getStudentId());
                    this.updateById(student);
                } else {
                    // 不存在，新增
                    this.save(student);
                }
            } else {
                // 新增
                this.save(student);
            }
        }
        
        // 返回最新的学生信息
        if (student.getStudentId() != null) {
            return this.getById(student.getStudentId());
        } else if (student.getUserId() != null) {
            return this.getByUserId(student.getUserId());
        }
        return student;
    }
    
    @Override
    public boolean deleteByUserId(Integer userId) {
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return this.remove(queryWrapper);
    }
}

