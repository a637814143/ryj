package com.ryj.demo.controller;

import com.ryj.demo.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public ApiResponse<Map<String, Object>> home() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "高校就业管理系统 API");
        info.put("version", "1.0.0");
        info.put("status", "运行中");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("用户管理", "/api/users");
        endpoints.put("学生档案", "/api/student-profiles");
        endpoints.put("雇主管理", "/api/employers");
        endpoints.put("职位发布", "/api/job-postings");
        endpoints.put("职位申请", "/api/job-applications");
        endpoints.put("简历管理", "/api/resumes");
        endpoints.put("面试管理", "/api/interviews");
        endpoints.put("教师管理", "/api/teachers");
        endpoints.put("教师指导", "/api/teacher-guidances");
        endpoints.put("系统通知", "/api/system-notifications");
        endpoints.put("就业意向", "/api/employment-intentions");
        endpoints.put("学生经历", "/api/student-experiences");
        endpoints.put("学生教育", "/api/student-educations");
        endpoints.put("学生获奖", "/api/student-awards");
        
        info.put("api_endpoints", endpoints);
        
        return ApiResponse.success(info);
    }
    
    @GetMapping("/health")
    public ApiResponse<Map<String, String>> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("message", "服务运行正常");
        return ApiResponse.success(status);
    }
}

