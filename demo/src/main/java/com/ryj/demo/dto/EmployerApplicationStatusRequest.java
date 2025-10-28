package com.ryj.demo.dto;

import com.ryj.demo.entity.JobApplication;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EmployerApplicationStatusRequest {

    @NotNull(message = "状态不能为空")
    private JobApplication.Status status;
}
