package com.ryj.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ResumeRequest {
    @NotNull(message = "学生ID不能为空")
    private Long studentId;

    @NotBlank(message = "简历标题不能为空")
    private String title;

    private String summary;
    private String portfolioUrl;

    private List<ResumeSkillRequest> skills;
    private List<ResumeExperienceRequest> experiences;

    @Data
    public static class ResumeSkillRequest {
        @NotBlank(message = "技能名称不能为空")
        private String skill;
        private Integer proficiency;
    }

    @Data
    public static class ResumeExperienceRequest {
        @NotBlank(message = "经历标题不能为空")
        private String title;
        private String organization;
        private java.time.LocalDate startDate;
        private java.time.LocalDate endDate;
        private String description;
    }
}
