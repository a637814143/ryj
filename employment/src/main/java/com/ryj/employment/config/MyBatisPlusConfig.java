package com.ryj.employment.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.ryj.employment.mapper")
public class MyBatisPlusConfig {
    // 配置类，用于扫描Mapper接口
}