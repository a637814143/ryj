package com.ryj.demo.dto;

import com.ryj.demo.entity.EmploymentIntention;
import com.ryj.demo.entity.EmploymentIntentionCity;
import com.ryj.demo.entity.Interview;
import com.ryj.demo.entity.JobApplication;
import com.ryj.demo.entity.JobPosting;
import com.ryj.demo.entity.StudentAward;
import com.ryj.demo.entity.StudentEducation;
import com.ryj.demo.entity.StudentExperience;
import com.ryj.demo.entity.StudentProfile;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class StudentDashboardResponse {

    private Header header;
    private List<ModuleInfo> modules;

    private StudentProfile profile;
    private List<StudentEducation> educations;
    private List<StudentExperience> experiences;
    private List<StudentAward> awards;

    private EmploymentIntentionSection employmentIntention;

    private List<ResumeOverview> resumes;
    private List<JobApplicationOverview> jobApplications;
    private List<InterviewOverview> interviews;
    private List<RecommendedJob> recommendedJobs;

    @Data
    public static class Header {
        private int completionPercentage;
        private int resumeCount;
        private int jobApplicationCount;
        private int pendingInterviewCount;
        private int awardCount;
        private boolean hasEmploymentIntention;
    }

    @Data
    public static class ModuleInfo {
        private String key;
        private String name;
        private String description;
        private List<String> capabilities;
    }

    @Data
    public static class EmploymentIntentionSection {
        private EmploymentIntention intention;
        private List<EmploymentIntentionCity> cities;
    }

    @Data
    public static class ResumeOverview {
        private Long id;
        private String title;
        private String summary;
        private String portfolioUrl;
        private int experienceCount;
        private int skillCount;
        private LocalDateTime updatedAt;
    }

    @Data
    public static class JobApplicationOverview {
        private Long id;
        private JobApplication.Status status;
        private LocalDateTime appliedAt;
        private Long jobId;
        private Long resumeId;
        private String jobTitle;
        private String jobLocation;
    }

    @Data
    public static class InterviewOverview {
        private Long id;
        private Interview.Status status;
        private LocalDateTime scheduledTime;
        private String location;
        private String meetingLink;
        private Long jobId;
        private String jobTitle;
    }

    @Data
    public static class RecommendedJob {
        private Long id;
        private String title;
        private String location;
        private String salaryRange;
        private JobPosting.WorkType workType;
        private boolean matchesIntention;
    }
}
