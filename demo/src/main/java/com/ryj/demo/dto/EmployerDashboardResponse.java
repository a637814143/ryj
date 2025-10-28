package com.ryj.demo.dto;

import com.ryj.demo.entity.JobApplication;
import com.ryj.demo.entity.JobPosting;
import com.ryj.demo.entity.Interview;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class EmployerDashboardResponse {

    private Header header;
    private CompanyProfile companyProfile;
    private List<ModuleInfo> modules;
    private List<JobOverview> jobs;
    private List<ApplicationOverview> applications;
    private List<InterviewOverview> interviews;

    @Data
    public static class Header {
        private String companyName;
        private int totalJobCount;
        private int openJobCount;
        private int activeApplicationCount;
        private int scheduledInterviewCount;
        private int talentPoolSize;
    }

    @Data
    public static class CompanyProfile {
        private Long id;
        private String companyName;
        private String contactPerson;
        private String contactEmail;
        private String contactPhone;
        private String description;
        private String website;
    }

    @Data
    public static class ModuleInfo {
        private String key;
        private String name;
        private String description;
        private List<String> capabilities;
    }

    @Data
    public static class JobOverview {
        private Long id;
        private String title;
        private JobPosting.Status status;
        private JobPosting.WorkType workType;
        private String location;
        private String salaryRange;
        private LocalDateTime publishedDate;
        private LocalDate closingDate;
        private long applicationCount;
    }

    @Data
    public static class ApplicationOverview {
        private Long id;
        private Long jobId;
        private String jobTitle;
        private Long studentId;
        private String candidateName;
        private JobApplication.Status status;
        private LocalDateTime appliedAt;
        private Long resumeId;
    }

    @Data
    public static class InterviewOverview {
        private Long id;
        private Long jobId;
        private String jobTitle;
        private Long applicationId;
        private String candidateName;
        private Interview.Status status;
        private LocalDateTime scheduledTime;
        private String location;
        private String meetingLink;
        private String feedback;
    }
}
