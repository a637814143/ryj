package com.bs.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmploymentIntention {
    private String expectedPosition;
    private String salaryRange;
    @Builder.Default
    private List<String> preferredCities = new ArrayList<>();
    private String workType;
    private String notes;
}
