package com.ryj.demo.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class PublicOverviewResponse {

    private Hero hero;

    private List<HighlightModule> modules;

    private Statistics statistics;

    private List<String> focusAreas;

    private List<QuickLink> quickLinks;

    private List<NotificationItem> notifications;

    private List<JobSummary> trendingJobs;

    private List<ResourceSummary> resources;

    @Data
    public static class Hero {
        private String title;
        private String subtitle;
        private String description;
        private List<String> badges;
    }

    @Data
    public static class HighlightModule {
        private String name;
        private String description;
        private List<String> features;
        private String accentColor;
    }

    @Data
    public static class Statistics {
        private long totalJobs;
        private long activeEmployers;
        private long registeredStudents;
        private long serviceTeachers;
    }

    @Data
    public static class QuickLink {
        private String label;
        private String description;
        private String target;
    }

    @Data
    public static class NotificationItem {
        private Long id;
        private String title;
        private String content;
        private String category;
        private LocalDateTime createdAt;
    }

    @Data
    public static class JobSummary {
        private Long id;
        private String title;
        private String location;
        private String salaryRange;
        private String workType;
        private LocalDateTime publishedDate;
    }

    @Data
    public static class ResourceSummary {
        private Long id;
        private String fileName;
        private String description;
        private String fileType;
        private Long fileSize;
        private LocalDateTime createdAt;
        private String downloadUrl;
    }
}
