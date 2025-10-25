package com.ryj.employment.controller;

import com.ryj.employment.common.Result;
import com.ryj.employment.entity.Student;
import com.ryj.employment.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学生控制器
 * 提供学生信息的增删改查功能
 */
@RestController
@RequestMapping("/student")
public class StudentController {
    
    @Autowired
    private StudentService studentService;
    
    /**
     * 获取所有学生列表
     * @return 学生列表
     */
    @GetMapping("/list")
    public Result<List<Student>> list() {
        try {
            List<Student> students = studentService.list();
            return Result.success(students);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("获取学生列表失败");
        }
    }
    
    /**
     * 根据学生ID获取学生信息
     * @param studentId 学生ID
     * @return 学生信息
     */
    @GetMapping("/{studentId}")
    public Result<Student> getById(@PathVariable Integer studentId) {
        try {
            Student student = studentService.getById(studentId);
            if (student != null) {
                return Result.success(student);
            } else {
                return Result.fail("学生信息不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("获取学生信息失败");
        }
    }
    
    /**
     * 根据用户ID获取学生信息
     * @param userId 用户ID
     * @return 学生信息
     */
    @GetMapping("/user/{userId}")
    public Result<Student> getByUserId(@PathVariable Integer userId) {
        try {
            Student student = studentService.getByUserId(userId);
            if (student != null) {
                return Result.success(student);
            } else {
                return Result.fail("学生信息不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("获取学生信息失败");
        }
    }
    
    /**
     * 获取当前登录学生的信息
     * 注意：这里简化处理，实际应该从session或token中获取当前用户ID
     * @param userId 用户ID（从请求头或参数中获取）
     * @return 学生信息
     */
    @GetMapping("/info")
    public Result<Student> getInfo(@RequestParam(required = false) Integer userId) {
        try {
            // 实际项目中应该从JWT token或session中获取当前登录用户的ID
            // 这里简化处理，从参数中获取
            if (userId == null) {
                return Result.fail("请先登录");
            }
            
            Student student = studentService.getByUserId(userId);
            if (student != null) {
                return Result.success(student);
            } else {
                return Result.fail("学生信息不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("获取学生信息失败");
        }
    }
    
    /**
     * 新增学生信息
     * @param student 学生信息
     * @return 保存后的学生信息
     */
    @PostMapping("/add")
    public Result<Student> add(@RequestBody Student student) {
        try {
            if (student == null) {
                return Result.fail("学生信息不能为空");
            }
            
            // 检查必填字段
            if (student.getUserId() == null) {
                return Result.fail("用户ID不能为空");
            }
            
            // 检查该用户是否已有学生信息
            Student existStudent = studentService.getByUserId(student.getUserId());
            if (existStudent != null) {
                return Result.fail("该用户已有学生信息，请使用更新接口");
            }
            
            // 设置默认值
            if (student.getExamineState() == null || student.getExamineState().isEmpty()) {
                student.setExamineState("已通过");
            }
            if (student.getRecommend() == null) {
                student.setRecommend(0);
            }
            
            boolean success = studentService.save(student);
            if (success) {
                return Result.success(student);
            } else {
                return Result.fail("保存学生信息失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("保存学生信息失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新学生信息
     * @param student 学生信息
     * @return 更新后的学生信息
     */
    @PutMapping("/update")
    public Result<Student> update(@RequestBody Student student) {
        try {
            if (student == null) {
                return Result.fail("学生信息不能为空");
            }
            
            if (student.getStudentId() == null) {
                return Result.fail("学生ID不能为空");
            }
            
            // 检查学生是否存在
            Student existStudent = studentService.getById(student.getStudentId());
            if (existStudent == null) {
                return Result.fail("学生信息不存在");
            }
            
            boolean success = studentService.updateById(student);
            if (success) {
                return Result.success(studentService.getById(student.getStudentId()));
            } else {
                return Result.fail("更新学生信息失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("更新学生信息失败：" + e.getMessage());
        }
    }
    
    /**
     * 保存或更新学生信息（智能判断）
     * @param student 学生信息
     * @return 保存后的学生信息
     */
    @PostMapping("/save")
    public Result<Student> save(@RequestBody Student student) {
        try {
            if (student == null) {
                return Result.fail("学生信息不能为空");
            }
            
            // 检查必填字段
            if (student.getFullName() == null || student.getFullName().isEmpty()) {
                return Result.fail("姓名不能为空");
            }
            if (student.getGender() == null || student.getGender().isEmpty()) {
                return Result.fail("性别不能为空");
            }
            
            // 设置默认值
            if (student.getExamineState() == null || student.getExamineState().isEmpty()) {
                student.setExamineState("已通过");
            }
            if (student.getRecommend() == null) {
                student.setRecommend(0);
            }
            
            Student savedStudent = studentService.saveOrUpdateStudent(student);
            return Result.success(savedStudent);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("保存学生信息失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据学生ID删除学生信息
     * @param studentId 学生ID
     * @return 是否删除成功
     */
    @DeleteMapping("/{studentId}")
    public Result<Boolean> delete(@PathVariable Integer studentId) {
        try {
            boolean success = studentService.removeById(studentId);
            if (success) {
                return Result.success(true, "删除成功");
            } else {
                return Result.fail("删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("删除学生信息失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据用户ID删除学生信息
     * @param userId 用户ID
     * @return 是否删除成功
     */
    @DeleteMapping("/user/{userId}")
    public Result<Boolean> deleteByUserId(@PathVariable Integer userId) {
        try {
            boolean success = studentService.deleteByUserId(userId);
            if (success) {
                return Result.success(true, "删除成功");
            } else {
                return Result.fail("删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("删除学生信息失败：" + e.getMessage());
        }
    }
}

