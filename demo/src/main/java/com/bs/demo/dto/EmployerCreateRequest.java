package com.bs.demo.dto;

import lombok.Data;

@Data
public class EmployerCreateRequest {
    private String name;
    private String contactPerson;
    private String contactEmail;
    private String contactPhone;
    private String description;
}
