package com.ryj.demo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TeacherProfileApprovalDetail {

    private Long requestId;
    private Long studentId;
    private String studentName;
    private String status;
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;
    private Long reviewerId;
    private String reviewComment;

    private ProfileSnapshot currentProfile;
    private ProfileSnapshot requestedProfile;

    @Data
    public static class ProfileSnapshot {
        private String gender;
        private Integer age;
        private String major;
        private String biography;
        private Integer graduationYear;
    }
}

