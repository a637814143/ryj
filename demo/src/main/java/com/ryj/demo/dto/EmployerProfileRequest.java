package com.ryj.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmployerProfileRequest {

    @NotBlank(message = "企业名称不能为空")
    private String companyName;

    private String contactPerson;
    private String contactEmail;
    private String contactPhone;
    private String description;
    private String website;
}
