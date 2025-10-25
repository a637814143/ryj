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
public class Employer {
    private Long id;
    private String name;
    private String contactPerson;
    private String contactEmail;
    private String contactPhone;
    private String description;
    @Builder.Default
    private List<Long> jobIds = new ArrayList<>();
}
