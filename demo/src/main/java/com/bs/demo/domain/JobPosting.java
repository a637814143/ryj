package com.bs.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobPosting {
    private Long id;
    private Long employerId;
    private String title;
    private String description;
    private String salaryRange;
    private String location;
    private String workType;
    @Builder.Default
    private List<String> requirements = new ArrayList<>();
    private JobStatus status;
    private LocalDate publishedDate;
    @Builder.Default
    private List<Long> applicationIds = new ArrayList<>();
    @Builder.Default
    private List<Long> interviewIds = new ArrayList<>();
}
