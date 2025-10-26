package com.ryj.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 搜索记录，记录所有角色在公共首页发起的搜索条件。
 */
@Data
@TableName("public_search_history")
public class PublicSearchHistory {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String keyword;

    private String roleFilter;

    private String categoryFilter;

    private String locationFilter;

    private String advancedOptions;

    private LocalDateTime createdAt;
}
