package com.ryj.demo.dto;

import com.ryj.demo.entity.JobApplication;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TeacherDashboardResponse {

    private Overview overview;
    private TeacherProfile profile;
    private List<GuidedStudent> guidedStudents;
    private List<PendingApproval> pendingApprovals;
    private List<EmployerCollaboration> employerCollaborations;
    private List<GuidanceNote> recentGuidanceNotes;
    private List<StudentActivity> recentStudentActivities;

    @Data
    public static class Overview {
        private int totalGuidedStudents;
        private int pendingApprovalCount;
        private int activeInterviewCount;
        private int collaborationCount;
    }

    @Data
    public static class TeacherProfile {
        private Long teacherId;
        private Long userId;
        private String name;
        private String department;
        private String email;
        private String phone;
        private String biography;
        private String focus;
        private String avatarInitials;
    }

    @Data
    public static class GuidedStudent {
        private Long studentId;
        private String studentName;
        private String major;
        private int pendingRequestCount;
        private int activeApplicationCount;
        private String latestInterviewStatus;
        private LocalDateTime latestGuidanceAt;
        private String latestGuidanceNote;
        private List<String> employerNames;
    }

    @Data
    public static class PendingApproval {
        private Long requestId;
        private Long studentId;
        private String studentName;
        private String major;
        private Integer graduationYear;
        private LocalDateTime submittedAt;
        private String biography;
    }

    @Data
    public static class EmployerCollaboration {
        private Long employerId;
        private String companyName;
        private int jobCount;
        private int studentCount;
        private LocalDateTime latestInteraction;
        private List<String> studentNames;
    }

    @Data
    public static class GuidanceNote {
        private Long id;
        private Long studentId;
        private String studentName;
        private String note;
        private LocalDateTime createdAt;
    }

    @Data
    public static class StudentActivity {
        private Long applicationId;
        private Long studentId;
        private String studentName;
        private Long jobId;
        private String jobTitle;
        private String employerName;
        private JobApplication.Status status;
        private LocalDateTime appliedAt;
    }
}
