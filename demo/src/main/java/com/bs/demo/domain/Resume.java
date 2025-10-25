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
public class Resume {
    private Long id;
    private Long studentId;
    private String title;
    private String summary;
    @Builder.Default
    private List<String> skills = new ArrayList<>();
    @Builder.Default
    private List<String> experiences = new ArrayList<>();
    @Builder.Default
    private List<String> attachments = new ArrayList<>();
    private String portfolioUrl;
}
