package com.ryj.demo.controller;

import com.ryj.demo.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/diagnostic")
@RequiredArgsConstructor
public class DiagnosticController {

    private final JdbcTemplate jdbcTemplate;

    @GetMapping("/check-database")
    public ApiResponse<Map<String, Object>> checkDatabase() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 检查数据库连接
            Integer count = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            result.put("database_connection", "✅ 连接成功");
            
            // 检查当前数据库
            String currentDb = jdbcTemplate.queryForObject("SELECT DATABASE()", String.class);
            result.put("current_database", currentDb);
            
            // 列出所有表
            List<Map<String, Object>> tables = jdbcTemplate.queryForList("SHOW TABLES");
            result.put("tables_count", tables.size());
            result.put("tables", tables);
            
            // 检查 sys_user 表是否存在
            try {
                Long userCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM sys_user", Long.class);
                result.put("sys_user_table", "✅ 存在 (记录数: " + userCount + ")");
            } catch (Exception e) {
                result.put("sys_user_table", "❌ 不存在或无法访问: " + e.getMessage());
            }
            
            return ApiResponse.success(result);
            
        } catch (Exception e) {
            result.put("error", "❌ 数据库检查失败: " + e.getMessage());
            result.put("exception_type", e.getClass().getName());
            e.printStackTrace();
            return ApiResponse.failure(500, "数据库检查失败", result);
        }
    }
}

