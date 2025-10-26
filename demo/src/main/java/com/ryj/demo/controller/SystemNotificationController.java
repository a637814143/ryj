package com.ryj.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ryj.demo.common.ApiResponse;
import com.ryj.demo.dto.SystemNotificationRequest;
import com.ryj.demo.entity.SystemNotification;
import com.ryj.demo.service.SystemNotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class SystemNotificationController {

    private final SystemNotificationService notificationService;

    @PostMapping
    public ApiResponse<SystemNotification> create(@Valid @RequestBody SystemNotificationRequest request) {
        SystemNotification notification = new SystemNotification();
        notification.setUserId(request.getUserId());
        notification.setCategory(request.getCategory());
        notification.setTitle(request.getTitle());
        notification.setContent(request.getContent());
        notification.setReadFlag(request.getReadFlag());
        notificationService.save(notification);
        return ApiResponse.success(notification);
    }

    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Long id, @RequestBody SystemNotificationRequest request) {
        SystemNotification notification = notificationService.getById(id);
        if (notification == null) {
            throw new IllegalArgumentException("通知不存在");
        }
        if (request.getCategory() != null) {
            notification.setCategory(request.getCategory());
        }
        if (request.getTitle() != null) {
            notification.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            notification.setContent(request.getContent());
        }
        if (request.getReadFlag() != null) {
            notification.setReadFlag(request.getReadFlag());
        }
        return ApiResponse.success(notificationService.updateById(notification));
    }

    @GetMapping
    public ApiResponse<Page<SystemNotification>> list(@RequestParam(defaultValue = "1") long page,
                                                      @RequestParam(defaultValue = "10") long size,
                                                      @RequestParam(required = false) Long userId,
                                                      @RequestParam(required = false) Boolean unreadOnly) {
        LambdaQueryWrapper<SystemNotification> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(SystemNotification::getUserId, userId);
        }
        if (Boolean.TRUE.equals(unreadOnly)) {
            wrapper.eq(SystemNotification::getReadFlag, false);
        }
        Page<SystemNotification> result = notificationService.page(new Page<>(page, size), wrapper.orderByDesc(SystemNotification::getCreatedAt));
        return ApiResponse.success(result);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.success(notificationService.removeById(id));
    }
}
