package com.ryj.demo.dto;

import com.ryj.demo.entity.EmploymentIntention;
import lombok.Data;

import java.util.List;

@Data
public class EmploymentIntentionResponse {
    private Long id;
    private Long studentId;
    private String expectedPosition;
    private String salaryRange;
    private String workType;
    private List<String> cities;
    private String notes;
    
    public static EmploymentIntentionResponse from(EmploymentIntention intention, List<String> cities) {
        EmploymentIntentionResponse response = new EmploymentIntentionResponse();
        response.setId(intention.getId());
        response.setStudentId(intention.getStudentId());
        response.setExpectedPosition(intention.getExpectedPosition());
        response.setSalaryRange(intention.getSalaryRange());
        response.setWorkType(intention.getWorkType() != null ? intention.getWorkType().name() : null);
        response.setCities(cities);
        response.setNotes(intention.getNotes());
        return response;
    }
}

