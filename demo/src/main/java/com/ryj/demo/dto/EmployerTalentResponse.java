package com.ryj.demo.dto;

import com.ryj.demo.entity.EmploymentIntention;
import com.ryj.demo.entity.Interview;
import com.ryj.demo.entity.JobApplication;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import lombok.Data;

/**
 * 企业人才库响应数据.
 */
@Data
public class EmployerTalentResponse {

    private Summary summary = new Summary();
    private List<TalentCandidate> candidates = Collections.emptyList();

    @Data
    public static class Summary {
        private int totalCandidates;
        private int filteredCandidates;
        private int pendingReviewCount;
        private int interviewingCount;
        private int offerCount;
    }

    @Data
    public static class TalentCandidate {
        private Long studentId;
        private Long latestResumeId;
        private String candidateName;
        private String email;
        private String phone;
        private String major;
        private Integer graduationYear;
        private String expectedPosition;
        private EmploymentIntention.WorkType expectedWorkType;
        private List<String> intentionCities = Collections.emptyList();
        private JobApplication.Status latestStatus;
        private Interview.Status latestInterviewStatus;
        private LocalDateTime lastAppliedAt;
        private String latestJobTitle;
        private int applicationCount;
        private int interviewCount;
    }
}
