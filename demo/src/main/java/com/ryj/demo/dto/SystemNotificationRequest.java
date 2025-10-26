package com.ryj.demo.dto;

import com.ryj.demo.entity.SystemNotification;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SystemNotificationRequest {
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    private SystemNotification.Category category;

    @NotBlank(message = "通知标题不能为空")
    private String title;

    @NotBlank(message = "通知内容不能为空")
    private String content;

    private Boolean readFlag;
}
