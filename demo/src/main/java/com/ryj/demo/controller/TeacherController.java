package com.ryj.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ryj.demo.common.ApiResponse;
import com.ryj.demo.dto.StudentProfileReviewDecision;
import com.ryj.demo.dto.TeacherDashboardResponse;
import com.ryj.demo.entity.Employer;
import com.ryj.demo.entity.Interview;
import com.ryj.demo.entity.JobApplication;
import com.ryj.demo.entity.JobPosting;
import com.ryj.demo.entity.StudentProfile;
import com.ryj.demo.entity.StudentProfileUpdateRequest;
import com.ryj.demo.entity.SysUser;
import com.ryj.demo.entity.Teacher;
import com.ryj.demo.entity.TeacherGuidance;
import com.ryj.demo.service.EmployerService;
import com.ryj.demo.service.InterviewService;
import com.ryj.demo.service.JobApplicationService;
import com.ryj.demo.service.JobPostingService;
import com.ryj.demo.service.StudentProfileService;
import com.ryj.demo.service.StudentProfileUpdateRequestService;
import com.ryj.demo.service.SysUserService;
import com.ryj.demo.service.TeacherGuidanceService;
import com.ryj.demo.service.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;
    private final TeacherGuidanceService teacherGuidanceService;
    private final StudentProfileService studentProfileService;
    private final StudentProfileUpdateRequestService updateRequestService;
    private final JobApplicationService jobApplicationService;
    private final JobPostingService jobPostingService;
    private final EmployerService employerService;
    private final InterviewService interviewService;
    private final SysUserService sysUserService;

    @PostMapping
    public ApiResponse<Teacher> create(@Valid @RequestBody Teacher teacher) {
        teacher.setId(null);
        teacherService.save(teacher);
        return ApiResponse.success(teacher);
    }

    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Long id, @RequestBody Teacher teacher) {
        teacher.setId(id);
        return ApiResponse.success(teacherService.updateById(teacher));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.success(teacherService.removeById(id));
    }

    @GetMapping
    public ApiResponse<Page<Teacher>> list(@RequestParam(defaultValue = "1") long page,
                                           @RequestParam(defaultValue = "10") long size,
                                           @RequestParam(required = false) Long userId,
                                           @RequestParam(required = false) String department) {
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(Teacher::getUserId, userId);
        }
        if (department != null && !department.isBlank()) {
            wrapper.like(Teacher::getDepartment, department);
        }
        Page<Teacher> result = teacherService.page(new Page<>(page, size), wrapper);
        return ApiResponse.success(result);
    }

    @GetMapping("/{id}")
    public ApiResponse<Teacher> detail(@PathVariable Long id) {
        return ApiResponse.success(teacherService.getById(id));
    }

    @GetMapping("/by-user/{userId}")
    public ApiResponse<Teacher> findByUserId(@PathVariable Long userId) {
        Teacher teacher = teacherService.getOne(new LambdaQueryWrapper<Teacher>().eq(Teacher::getUserId, userId));
        if (teacher == null) {
            return ApiResponse.failure(404, "未找到对应的教师信息");
        }
        return ApiResponse.success(teacher);
    }

    @GetMapping("/{id}/dashboard")
    public ApiResponse<TeacherDashboardResponse> dashboard(@PathVariable Long id) {
        Teacher teacher = teacherService.getById(id);
        if (teacher == null) {
            return ApiResponse.failure(404, "未找到教师信息");
        }

        SysUser teacherUser = sysUserService.getById(teacher.getUserId());
        TeacherDashboardResponse response = new TeacherDashboardResponse();

        List<TeacherGuidance> guidanceList = teacherGuidanceService.list(new LambdaQueryWrapper<TeacherGuidance>()
                .eq(TeacherGuidance::getTeacherId, teacher.getId())
                .orderByDesc(TeacherGuidance::getCreatedAt));

        Set<Long> studentIds = guidanceList.stream()
                .map(TeacherGuidance::getStudentId)
                .collect(Collectors.toCollection(HashSet::new));

        Map<Long, List<TeacherGuidance>> guidanceByStudent = guidanceList.stream()
                .collect(Collectors.groupingBy(TeacherGuidance::getStudentId));

        Map<Long, StudentProfile> profileMap = studentIds.isEmpty()
                ? Collections.emptyMap()
                : studentProfileService.listByIds(studentIds).stream()
                .collect(Collectors.toMap(StudentProfile::getId, profile -> profile));

        Map<Long, SysUser> studentUserMap = studentIds.isEmpty()
                ? Collections.emptyMap()
                : sysUserService.listByIds(studentIds).stream()
                .collect(Collectors.toMap(SysUser::getId, user -> user));

        List<StudentProfileUpdateRequest> pendingRequests = studentIds.isEmpty()
                ? Collections.emptyList()
                : updateRequestService.list(new LambdaQueryWrapper<StudentProfileUpdateRequest>()
                .eq(StudentProfileUpdateRequest::getStatus, "PENDING")
                .in(StudentProfileUpdateRequest::getStudentId, studentIds)
                .orderByDesc(StudentProfileUpdateRequest::getCreatedAt));

        Map<Long, Long> pendingCountByStudent = pendingRequests.stream()
                .collect(Collectors.groupingBy(StudentProfileUpdateRequest::getStudentId, Collectors.counting()));

        List<JobApplication> jobApplications = studentIds.isEmpty()
                ? Collections.emptyList()
                : jobApplicationService.list(new LambdaQueryWrapper<JobApplication>()
                .in(JobApplication::getStudentId, studentIds)
                .orderByDesc(JobApplication::getAppliedAt));

        Map<Long, List<JobApplication>> applicationsByStudent = jobApplications.stream()
                .collect(Collectors.groupingBy(JobApplication::getStudentId));

        Set<Long> jobIds = jobApplications.stream()
                .map(JobApplication::getJobId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, JobPosting> jobPostingMap = jobIds.isEmpty()
                ? Collections.emptyMap()
                : jobPostingService.listByIds(jobIds).stream()
                .collect(Collectors.toMap(JobPosting::getId, posting -> posting));

        Set<Long> employerIds = jobPostingMap.values().stream()
                .map(JobPosting::getEmployerId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, Employer> employerMap = employerIds.isEmpty()
                ? Collections.emptyMap()
                : employerService.listByIds(employerIds).stream()
                .collect(Collectors.toMap(Employer::getId, employer -> employer));

        Set<Long> applicationIds = jobApplications.stream()
                .map(JobApplication::getId)
                .collect(Collectors.toSet());

        List<Interview> interviews = applicationIds.isEmpty()
                ? Collections.emptyList()
                : interviewService.list(new LambdaQueryWrapper<Interview>()
                .in(Interview::getApplicationId, applicationIds));

        Map<Long, List<Interview>> interviewsByApplication = interviews.stream()
                .collect(Collectors.groupingBy(Interview::getApplicationId));

        response.setOverview(buildOverview(studentIds, pendingRequests, interviews, employerIds));
        response.setProfile(buildProfile(teacher, teacherUser, studentIds.size()));
        response.setGuidedStudents(buildGuidedStudents(studentIds, studentUserMap, profileMap, guidanceByStudent,
                pendingCountByStudent, applicationsByStudent, interviewsByApplication, jobPostingMap, employerMap));
        response.setPendingApprovals(buildPendingApprovals(pendingRequests, studentUserMap, profileMap));
        response.setEmployerCollaborations(buildEmployerCollaborations(jobApplications, jobPostingMap, employerMap, studentUserMap));
        response.setRecentGuidanceNotes(buildGuidanceNotes(guidanceList, studentUserMap));
        response.setRecentStudentActivities(buildStudentActivities(jobApplications, studentUserMap, jobPostingMap, employerMap));

        return ApiResponse.success(response);
    }

    @PutMapping("/{teacherId}/requests/{requestId}/approve")
    public ApiResponse<Boolean> approve(@PathVariable Long teacherId,
                                        @PathVariable Long requestId,
                                        @RequestBody(required = false) StudentProfileReviewDecision decision) {
        return reviewRequest(teacherId, requestId, decision, true);
    }

    @PutMapping("/{teacherId}/requests/{requestId}/reject")
    public ApiResponse<Boolean> reject(@PathVariable Long teacherId,
                                       @PathVariable Long requestId,
                                       @RequestBody StudentProfileReviewDecision decision) {
        if (decision == null || decision.getReviewComment() == null || decision.getReviewComment().isBlank()) {
            return ApiResponse.failure(400, "请填写审核意见");
        }
        return reviewRequest(teacherId, requestId, decision, false);
    }

    private ApiResponse<Boolean> reviewRequest(Long teacherId, Long requestId, StudentProfileReviewDecision decision, boolean approve) {
        Teacher teacher = teacherService.getById(teacherId);
        if (teacher == null) {
            return ApiResponse.failure(404, "未找到教师信息");
        }

        StudentProfileUpdateRequest request = updateRequestService.getById(requestId);
        if (request == null) {
            return ApiResponse.failure(404, "未找到档案更新申请");
        }

        if (!Objects.equals("PENDING", request.getStatus())) {
            return ApiResponse.failure(400, "仅能审核待处理的申请");
        }

        if (!isTeacherAuthorizedForStudent(teacherId, request.getStudentId())) {
            return ApiResponse.failure(403, "您无权审核该学生的档案申请");
        }

        if (approve) {
            StudentProfile profile = Optional.ofNullable(studentProfileService.getById(request.getStudentId()))
                    .orElseGet(() -> {
                        StudentProfile created = new StudentProfile();
                        created.setId(request.getStudentId());
                        return created;
                    });
            profile.setGender(request.getGender());
            profile.setAge(request.getAge());
            profile.setMajor(request.getMajor());
            profile.setBiography(request.getBiography());
            profile.setGraduationYear(request.getGraduationYear());
            studentProfileService.saveOrUpdate(profile);
        }

        if (decision != null && decision.getReviewerId() != null) {
            request.setReviewerId(decision.getReviewerId());
        } else {
            request.setReviewerId(teacher.getUserId());
        }
        if (decision != null && decision.getReviewComment() != null) {
            request.setReviewComment(decision.getReviewComment());
        }

        request.setStatus(approve ? "APPROVED" : "REJECTED");
        request.setReviewedAt(LocalDateTime.now());

        return ApiResponse.success(updateRequestService.updateById(request));
    }

    private TeacherDashboardResponse.Overview buildOverview(Set<Long> studentIds,
                                                            List<StudentProfileUpdateRequest> pendingRequests,
                                                            List<Interview> interviews,
                                                            Set<Long> employerIds) {
        TeacherDashboardResponse.Overview overview = new TeacherDashboardResponse.Overview();
        overview.setTotalGuidedStudents(studentIds.size());
        overview.setPendingApprovalCount(pendingRequests.size());
        long activeInterviewCount = interviews.stream()
                .filter(interview -> interview.getStatus() == Interview.Status.SCHEDULED)
                .count();
        overview.setActiveInterviewCount((int) activeInterviewCount);
        overview.setCollaborationCount(employerIds.size());
        return overview;
    }

    private TeacherDashboardResponse.TeacherProfile buildProfile(Teacher teacher, SysUser teacherUser, int studentCount) {
        TeacherDashboardResponse.TeacherProfile profile = new TeacherDashboardResponse.TeacherProfile();
        profile.setTeacherId(teacher.getId());
        profile.setUserId(teacher.getUserId());
        profile.setDepartment(teacher.getDepartment());
        profile.setEmail(teacher.getEmail());
        profile.setPhone(teacher.getPhone());

        String teacherName = teacherUser != null ? teacherUser.getFullName() : null;
        profile.setName(teacherName != null ? teacherName : "教师");
        profile.setAvatarInitials(extractInitials(teacherName));

        String department = teacher.getDepartment();
        profile.setBiography(String.format("专注引导%s学生的职业规划与发展，关注每一位学子的成长轨迹。",
                department != null ? department : "校园"));
        profile.setFocus(String.format("已结对 %d 位学生 · 持续推进校企协同", studentCount));
        return profile;
    }

    private String extractInitials(String name) {
        if (name == null || name.isBlank()) {
            return "T";
        }
        String trimmed = name.trim();
        if (trimmed.length() == 1) {
            return trimmed;
        }
        return trimmed.substring(0, Math.min(2, trimmed.length()));
    }

    private List<TeacherDashboardResponse.GuidedStudent> buildGuidedStudents(Set<Long> studentIds,
                                                                             Map<Long, SysUser> studentUserMap,
                                                                             Map<Long, StudentProfile> profileMap,
                                                                             Map<Long, List<TeacherGuidance>> guidanceByStudent,
                                                                             Map<Long, Long> pendingCountByStudent,
                                                                             Map<Long, List<JobApplication>> applicationsByStudent,
                                                                             Map<Long, List<Interview>> interviewsByApplication,
                                                                             Map<Long, JobPosting> jobPostingMap,
                                                                             Map<Long, Employer> employerMap) {
        if (studentIds == null || studentIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<TeacherDashboardResponse.GuidedStudent> students = new ArrayList<>();
        for (Long studentId : studentIds) {
            TeacherDashboardResponse.GuidedStudent guidedStudent = new TeacherDashboardResponse.GuidedStudent();
            guidedStudent.setStudentId(studentId);

            SysUser studentUser = studentUserMap.get(studentId);
            guidedStudent.setStudentName(studentUser != null ? studentUser.getFullName() : "学生");

            StudentProfile profile = profileMap.get(studentId);
            guidedStudent.setMajor(profile != null ? profile.getMajor() : null);

            guidedStudent.setPendingRequestCount(pendingCountByStudent.getOrDefault(studentId, 0L).intValue());

            List<JobApplication> applications = applicationsByStudent.getOrDefault(studentId, Collections.emptyList());
            long activeApplications = applications.stream()
                    .filter(app -> app.getStatus() != JobApplication.Status.REJECTED)
                    .count();
            guidedStudent.setActiveApplicationCount((int) activeApplications);

            List<Interview> studentInterviews = applications.stream()
                    .flatMap(app -> interviewsByApplication.getOrDefault(app.getId(), Collections.emptyList()).stream())
                    .collect(Collectors.toList());
            studentInterviews.sort(Comparator.comparing(Interview::getScheduledTime, Comparator.nullsLast(Comparator.naturalOrder()))
                    .reversed());
            Interview latestInterview = studentInterviews.stream().findFirst().orElse(null);
            guidedStudent.setLatestInterviewStatus(latestInterview != null ? latestInterview.getStatus().name() : null);

            List<TeacherGuidance> guidanceRecords = guidanceByStudent.getOrDefault(studentId, Collections.emptyList());
            guidanceRecords.sort(Comparator.comparing(TeacherGuidance::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()))
                    .reversed());
            TeacherGuidance recentGuidance = guidanceRecords.stream().findFirst().orElse(null);
            if (recentGuidance != null) {
                guidedStudent.setLatestGuidanceAt(recentGuidance.getCreatedAt());
                guidedStudent.setLatestGuidanceNote(recentGuidance.getNote());
            }

            Set<String> employerNames = new HashSet<>();
            for (JobApplication application : applications) {
                JobPosting posting = jobPostingMap.get(application.getJobId());
                if (posting == null) {
                    continue;
                }
                Employer employer = employerMap.get(posting.getEmployerId());
                if (employer != null) {
                    employerNames.add(employer.getCompanyName());
                }
            }
            guidedStudent.setEmployerNames(new ArrayList<>(employerNames));

            students.add(guidedStudent);
        }

        students.sort(Comparator.comparing(TeacherDashboardResponse.GuidedStudent::getLatestGuidanceAt,
                Comparator.nullsLast(Comparator.naturalOrder())).reversed());
        return students;
    }

    private List<TeacherDashboardResponse.PendingApproval> buildPendingApprovals(List<StudentProfileUpdateRequest> pendingRequests,
                                                                                Map<Long, SysUser> studentUserMap,
                                                                                Map<Long, StudentProfile> profileMap) {
        if (pendingRequests.isEmpty()) {
            return Collections.emptyList();
        }

        List<TeacherDashboardResponse.PendingApproval> approvals = new ArrayList<>();
        for (StudentProfileUpdateRequest request : pendingRequests) {
            TeacherDashboardResponse.PendingApproval approval = new TeacherDashboardResponse.PendingApproval();
            approval.setRequestId(request.getId());
            approval.setStudentId(request.getStudentId());
            SysUser student = studentUserMap.get(request.getStudentId());
            approval.setStudentName(student != null ? student.getFullName() : "学生");
            StudentProfile profile = profileMap.get(request.getStudentId());
            approval.setMajor(request.getMajor() != null ? request.getMajor() : profile != null ? profile.getMajor() : null);
            approval.setGraduationYear(request.getGraduationYear() != null
                    ? request.getGraduationYear()
                    : profile != null ? profile.getGraduationYear() : null);
            approval.setSubmittedAt(request.getCreatedAt());
            approval.setBiography(request.getBiography());
            approvals.add(approval);
        }
        return approvals;
    }

    private List<TeacherDashboardResponse.EmployerCollaboration> buildEmployerCollaborations(List<JobApplication> jobApplications,
                                                                                            Map<Long, JobPosting> jobPostingMap,
                                                                                            Map<Long, Employer> employerMap,
                                                                                            Map<Long, SysUser> studentUserMap) {
        if (jobApplications.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, CollaborationAccumulator> accumulatorMap = new HashMap<>();
        for (JobApplication application : jobApplications) {
            JobPosting posting = jobPostingMap.get(application.getJobId());
            if (posting == null) {
                continue;
            }
            Employer employer = employerMap.get(posting.getEmployerId());
            if (employer == null) {
                continue;
            }

            CollaborationAccumulator accumulator = accumulatorMap.computeIfAbsent(employer.getId(), key -> {
                CollaborationAccumulator acc = new CollaborationAccumulator();
                acc.companyName = employer.getCompanyName();
                return acc;
            });

            accumulator.jobIds.add(posting.getId());
            accumulator.studentIds.add(application.getStudentId());
            if (application.getAppliedAt() != null && (accumulator.latestInteraction == null
                    || application.getAppliedAt().isAfter(accumulator.latestInteraction))) {
                accumulator.latestInteraction = application.getAppliedAt();
            }
        }

        List<TeacherDashboardResponse.EmployerCollaboration> collaborations = new ArrayList<>();
        for (Map.Entry<Long, CollaborationAccumulator> entry : accumulatorMap.entrySet()) {
            CollaborationAccumulator accumulator = entry.getValue();
            TeacherDashboardResponse.EmployerCollaboration collaboration = new TeacherDashboardResponse.EmployerCollaboration();
            collaboration.setEmployerId(entry.getKey());
            collaboration.setCompanyName(accumulator.companyName);
            collaboration.setJobCount(accumulator.jobIds.size());
            collaboration.setStudentCount(accumulator.studentIds.size());
            collaboration.setLatestInteraction(accumulator.latestInteraction);

            List<String> studentNames = accumulator.studentIds.stream()
                    .map(studentUserMap::get)
                    .filter(Objects::nonNull)
                    .map(SysUser::getFullName)
                    .collect(Collectors.toList());
            collaboration.setStudentNames(studentNames);
            collaborations.add(collaboration);
        }

        collaborations.sort(Comparator.comparing(TeacherDashboardResponse.EmployerCollaboration::getLatestInteraction,
                Comparator.nullsLast(Comparator.naturalOrder())).reversed());
        return collaborations;
    }

    private List<TeacherDashboardResponse.GuidanceNote> buildGuidanceNotes(List<TeacherGuidance> guidanceList,
                                                                          Map<Long, SysUser> studentUserMap) {
        if (guidanceList.isEmpty()) {
            return Collections.emptyList();
        }

        return guidanceList.stream()
                .limit(6)
                .map(guidance -> {
                    TeacherDashboardResponse.GuidanceNote note = new TeacherDashboardResponse.GuidanceNote();
                    note.setId(guidance.getId());
                    note.setStudentId(guidance.getStudentId());
                    SysUser student = studentUserMap.get(guidance.getStudentId());
                    note.setStudentName(student != null ? student.getFullName() : "学生");
                    note.setNote(guidance.getNote());
                    note.setCreatedAt(guidance.getCreatedAt());
                    return note;
                })
                .collect(Collectors.toList());
    }

    private List<TeacherDashboardResponse.StudentActivity> buildStudentActivities(List<JobApplication> jobApplications,
                                                                                Map<Long, SysUser> studentUserMap,
                                                                                Map<Long, JobPosting> jobPostingMap,
                                                                                Map<Long, Employer> employerMap) {
        if (jobApplications.isEmpty()) {
            return Collections.emptyList();
        }

        return jobApplications.stream()
                .sorted(Comparator.comparing(JobApplication::getAppliedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .limit(8)
                .map(application -> {
                    TeacherDashboardResponse.StudentActivity activity = new TeacherDashboardResponse.StudentActivity();
                    activity.setApplicationId(application.getId());
                    activity.setStudentId(application.getStudentId());
                    SysUser student = studentUserMap.get(application.getStudentId());
                    activity.setStudentName(student != null ? student.getFullName() : "学生");
                    activity.setJobId(application.getJobId());
                    JobPosting posting = jobPostingMap.get(application.getJobId());
                    if (posting != null) {
                        activity.setJobTitle(posting.getTitle());
                        Employer employer = employerMap.get(posting.getEmployerId());
                        if (employer != null) {
                            activity.setEmployerName(employer.getCompanyName());
                        }
                    }
                    activity.setStatus(application.getStatus());
                    activity.setAppliedAt(application.getAppliedAt());
                    return activity;
                })
                .collect(Collectors.toList());
    }

    private boolean isTeacherAuthorizedForStudent(Long teacherId, Long studentId) {
        return teacherGuidanceService.count(new LambdaQueryWrapper<TeacherGuidance>()
                .eq(TeacherGuidance::getTeacherId, teacherId)
                .eq(TeacherGuidance::getStudentId, studentId)) > 0;
    }

    private static class CollaborationAccumulator {
        private final Set<Long> jobIds = new HashSet<>();
        private final Set<Long> studentIds = new HashSet<>();
        private String companyName;
        private LocalDateTime latestInteraction;
    }
}
