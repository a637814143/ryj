package com.ryj.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ryj.demo.common.ApiResponse;
import com.ryj.demo.dto.StudentDashboardResponse;
import com.ryj.demo.entity.EmploymentIntention;
import com.ryj.demo.entity.EmploymentIntentionCity;
import com.ryj.demo.entity.Interview;
import com.ryj.demo.entity.JobApplication;
import com.ryj.demo.entity.JobPosting;
import com.ryj.demo.entity.Resume;
import com.ryj.demo.entity.ResumeExperience;
import com.ryj.demo.entity.ResumeSkill;
import com.ryj.demo.entity.StudentAward;
import com.ryj.demo.entity.StudentEducation;
import com.ryj.demo.entity.StudentExperience;
import com.ryj.demo.entity.StudentProfile;
import com.ryj.demo.service.EmploymentIntentionCityService;
import com.ryj.demo.service.EmploymentIntentionService;
import com.ryj.demo.service.InterviewService;
import com.ryj.demo.service.JobApplicationService;
import com.ryj.demo.service.JobPostingService;
import com.ryj.demo.service.ResumeExperienceService;
import com.ryj.demo.service.ResumeService;
import com.ryj.demo.service.ResumeSkillService;
import com.ryj.demo.service.StudentAwardService;
import com.ryj.demo.service.StudentEducationService;
import com.ryj.demo.service.StudentExperienceService;
import com.ryj.demo.service.StudentProfileService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student-dashboard")
@RequiredArgsConstructor
public class StudentDashboardController {

    private final StudentProfileService studentProfileService;
    private final StudentEducationService studentEducationService;
    private final StudentExperienceService studentExperienceService;
    private final StudentAwardService studentAwardService;
    private final EmploymentIntentionService employmentIntentionService;
    private final EmploymentIntentionCityService employmentIntentionCityService;
    private final ResumeService resumeService;
    private final ResumeExperienceService resumeExperienceService;
    private final ResumeSkillService resumeSkillService;
    private final JobApplicationService jobApplicationService;
    private final InterviewService interviewService;
    private final JobPostingService jobPostingService;

    @GetMapping("/{studentId}")
    public ApiResponse<StudentDashboardResponse> dashboard(@PathVariable Long studentId) {
        StudentDashboardResponse response = new StudentDashboardResponse();

        StudentProfile profile = studentProfileService.getById(studentId);
        List<StudentEducation> educations = studentEducationService.list(new LambdaQueryWrapper<StudentEducation>()
                .eq(StudentEducation::getStudentId, studentId)
                .orderByDesc(StudentEducation::getEndDate));
        List<StudentExperience> experiences = studentExperienceService.list(new LambdaQueryWrapper<StudentExperience>()
                .eq(StudentExperience::getStudentId, studentId)
                .orderByDesc(StudentExperience::getEndDate));
        List<StudentAward> awards = studentAwardService.list(new LambdaQueryWrapper<StudentAward>()
                .eq(StudentAward::getStudentId, studentId)
                .orderByDesc(StudentAward::getAwardDate));

        EmploymentIntention intention = employmentIntentionService.getOne(new LambdaQueryWrapper<EmploymentIntention>()
                .eq(EmploymentIntention::getStudentId, studentId));
        List<EmploymentIntentionCity> intentionCities = intention == null
                ? List.of()
                : employmentIntentionCityService.findByIntentionId(intention.getId());

        List<Resume> resumes = resumeService.list(new LambdaQueryWrapper<Resume>()
                .eq(Resume::getStudentId, studentId)
                .orderByDesc(Resume::getUpdatedAt));
        List<StudentDashboardResponse.ResumeOverview> resumeOverviews = buildResumeOverviews(resumes);

        List<JobApplication> applications = jobApplicationService.list(new LambdaQueryWrapper<JobApplication>()
                .eq(JobApplication::getStudentId, studentId)
                .orderByDesc(JobApplication::getAppliedAt));
        List<StudentDashboardResponse.JobApplicationOverview> jobApplicationOverviews = buildJobApplicationOverviews(applications);

        List<StudentDashboardResponse.InterviewOverview> interviewOverviews = buildInterviewOverviews(applications);

        StudentDashboardResponse.EmploymentIntentionSection intentionSection = new StudentDashboardResponse.EmploymentIntentionSection();
        intentionSection.setIntention(intention);
        intentionSection.setCities(intentionCities);

        response.setModules(buildModules());
        response.setProfile(profile);
        response.setEducations(educations);
        response.setExperiences(experiences);
        response.setAwards(awards);
        response.setEmploymentIntention(intentionSection);
        response.setResumes(resumeOverviews);
        response.setJobApplications(jobApplicationOverviews);
        response.setInterviews(interviewOverviews);
        response.setRecommendedJobs(buildRecommendedJobs(intention, intentionCities));

        response.setHeader(buildHeader(profile, educations, experiences, awards, intention, resumes, interviewOverviews, applications));

        return ApiResponse.success(response);
    }

    private StudentDashboardResponse.Header buildHeader(
            StudentProfile profile,
            List<StudentEducation> educations,
            List<StudentExperience> experiences,
            List<StudentAward> awards,
            EmploymentIntention intention,
            List<Resume> resumes,
            List<StudentDashboardResponse.InterviewOverview> interviews,
            List<JobApplication> applications) {
        StudentDashboardResponse.Header header = new StudentDashboardResponse.Header();
        int completedSections = 0;
        int totalSections = 5;
        if (profile != null && (StringUtils.hasText(profile.getMajor()) || StringUtils.hasText(profile.getBiography()))) {
            completedSections++;
        }
        if (!educations.isEmpty()) {
            completedSections++;
        }
        if (!experiences.isEmpty()) {
            completedSections++;
        }
        if (intention != null) {
            completedSections++;
        }
        if (!resumes.isEmpty()) {
            completedSections++;
        }
        header.setCompletionPercentage(totalSections == 0 ? 0 : (int) Math.round(completedSections * 100.0 / totalSections));
        header.setResumeCount(resumes.size());
        header.setJobApplicationCount(applications.size());
        long pendingInterviews = interviews.stream()
                .filter(item -> item.getStatus() == Interview.Status.SCHEDULED)
                .count();
        header.setPendingInterviewCount((int) pendingInterviews);
        header.setAwardCount(awards.size());
        header.setHasEmploymentIntention(intention != null);
        return header;
    }

    private List<StudentDashboardResponse.ModuleInfo> buildModules() {
        List<StudentDashboardResponse.ModuleInfo> modules = new ArrayList<>();
        modules.add(module("overview", "学生总览", "掌握求职进度与模块完成情况", List.of("数据看板", "模块导航")));
        modules.add(module("profile", "个人档案", "维护基础信息与学籍资料", List.of("基本信息", "个人简介")));
        modules.add(module("education", "教育经历", "记录在校期间的教育背景", List.of("学历", "专业方向")));
        modules.add(module("experience", "实践经历", "管理实习、项目等实践经验", List.of("实践类型", "成果描述")));
        modules.add(module("awards", "获奖荣誉", "展示重要获奖与荣誉", List.of("获奖级别", "荣誉说明")));
        modules.add(module("intention", "就业意向", "配置期望岗位与地点", List.of("工作类型", "目标城市")));
        modules.add(module("resume", "简历管理", "维护多份简历并追踪完善度", List.of("内容编辑", "技能标签")));
        modules.add(module("applications", "投递记录", "查看岗位投递与状态", List.of("投递状态", "使用简历")));
        modules.add(module("interviews", "面试管理", "追踪面试安排与反馈", List.of("面试进度", "反馈记录")));
        modules.add(module("jobs", "职位推荐", "根据意向匹配岗位", List.of("岗位匹配", "快速投递")));
        return modules;
    }

    private StudentDashboardResponse.ModuleInfo module(String key, String name, String description, List<String> capabilities) {
        StudentDashboardResponse.ModuleInfo module = new StudentDashboardResponse.ModuleInfo();
        module.setKey(key);
        module.setName(name);
        module.setDescription(description);
        module.setCapabilities(capabilities);
        return module;
    }

    private List<StudentDashboardResponse.ResumeOverview> buildResumeOverviews(List<Resume> resumes) {
        List<StudentDashboardResponse.ResumeOverview> results = new ArrayList<>();
        for (Resume resume : resumes) {
            StudentDashboardResponse.ResumeOverview overview = new StudentDashboardResponse.ResumeOverview();
            overview.setId(resume.getId());
            overview.setTitle(resume.getTitle());
            overview.setSummary(resume.getSummary());
            overview.setPortfolioUrl(resume.getPortfolioUrl());
            overview.setUpdatedAt(resume.getUpdatedAt());
            long experienceCount = resumeExperienceService.count(new LambdaQueryWrapper<ResumeExperience>()
                    .eq(ResumeExperience::getResumeId, resume.getId()));
            overview.setExperienceCount((int) experienceCount);
            List<ResumeSkill> skills = resumeSkillService.findByResumeId(resume.getId());
            overview.setSkillCount(skills.size());
            results.add(overview);
        }
        return results;
    }

    private List<StudentDashboardResponse.JobApplicationOverview> buildJobApplicationOverviews(List<JobApplication> applications) {
        Map<Long, JobPosting> jobCache = new HashMap<>();
        List<StudentDashboardResponse.JobApplicationOverview> results = new ArrayList<>();
        for (JobApplication application : applications) {
            StudentDashboardResponse.JobApplicationOverview overview = new StudentDashboardResponse.JobApplicationOverview();
            overview.setId(application.getId());
            overview.setStatus(application.getStatus());
            overview.setAppliedAt(application.getAppliedAt());
            overview.setJobId(application.getJobId());
            overview.setResumeId(application.getResumeId());
            JobPosting jobPosting = null;
            if (application.getJobId() != null) {
                jobPosting = jobCache.computeIfAbsent(application.getJobId(), jobPostingService::getById);
            }
            if (jobPosting != null) {
                overview.setJobTitle(jobPosting.getTitle());
                overview.setJobLocation(jobPosting.getLocation());
            }
            results.add(overview);
        }
        return results;
    }

    private List<StudentDashboardResponse.InterviewOverview> buildInterviewOverviews(List<JobApplication> applications) {
        if (applications.isEmpty()) {
            return List.of();
        }
        List<Long> applicationIds = applications.stream()
                .map(JobApplication::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (applicationIds.isEmpty()) {
            return List.of();
        }
        List<Interview> interviews = interviewService.list(new LambdaQueryWrapper<Interview>()
                .in(Interview::getApplicationId, applicationIds)
                .orderByDesc(Interview::getScheduledTime));
        Map<Long, JobApplication> applicationMap = applications.stream()
                .collect(Collectors.toMap(JobApplication::getId, app -> app));
        Map<Long, JobPosting> jobCache = new HashMap<>();
        List<StudentDashboardResponse.InterviewOverview> results = new ArrayList<>();
        for (Interview interview : interviews) {
            StudentDashboardResponse.InterviewOverview overview = new StudentDashboardResponse.InterviewOverview();
            overview.setId(interview.getId());
            overview.setStatus(interview.getStatus());
            overview.setScheduledTime(interview.getScheduledTime());
            overview.setLocation(interview.getLocation());
            overview.setMeetingLink(interview.getMeetingLink());
            overview.setJobId(interview.getJobId());
            JobPosting jobPosting = null;
            if (interview.getJobId() != null) {
                jobPosting = jobCache.computeIfAbsent(interview.getJobId(), jobPostingService::getById);
            }
            if (jobPosting != null) {
                overview.setJobTitle(jobPosting.getTitle());
            } else {
                JobApplication application = applicationMap.get(interview.getApplicationId());
                if (application != null && application.getJobId() != null) {
                    JobPosting fallback = jobCache.computeIfAbsent(application.getJobId(), jobPostingService::getById);
                    if (fallback != null) {
                        overview.setJobTitle(fallback.getTitle());
                        overview.setJobId(fallback.getId());
                    }
                }
            }
            results.add(overview);
        }
        return results;
    }

    private List<StudentDashboardResponse.RecommendedJob> buildRecommendedJobs(
            EmploymentIntention intention,
            List<EmploymentIntentionCity> intentionCities) {
        Set<String> targetCities = intentionCities.stream()
                .map(EmploymentIntentionCity::getCity)
                .filter(StringUtils::hasText)
                .map(String::trim)
                .collect(Collectors.toCollection(HashSet::new));

        LambdaQueryWrapper<JobPosting> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(JobPosting::getStatus, JobPosting.Status.OPEN);
        if (intention != null && StringUtils.hasText(intention.getExpectedPosition())) {
            wrapper.like(JobPosting::getTitle, intention.getExpectedPosition());
        }
        if (intention != null) {
            JobPosting.WorkType workType = mapWorkType(intention.getWorkType());
            if (workType != null) {
                wrapper.eq(JobPosting::getWorkType, workType);
            }
        }
        if (!targetCities.isEmpty()) {
            wrapper.in(JobPosting::getLocation, targetCities);
        }
        wrapper.orderByDesc(JobPosting::getPublishedDate);
        wrapper.last("limit 8");

        List<JobPosting> jobPostings = jobPostingService.list(wrapper);
        if (jobPostings.isEmpty()) {
            jobPostings = jobPostingService.lambdaQuery()
                    .eq(JobPosting::getStatus, JobPosting.Status.OPEN)
                    .orderByDesc(JobPosting::getPublishedDate)
                    .last("limit 8")
                    .list();
        }

        List<StudentDashboardResponse.RecommendedJob> results = new ArrayList<>();
        for (JobPosting jobPosting : jobPostings) {
            StudentDashboardResponse.RecommendedJob recommendedJob = new StudentDashboardResponse.RecommendedJob();
            recommendedJob.setId(jobPosting.getId());
            recommendedJob.setTitle(jobPosting.getTitle());
            recommendedJob.setLocation(jobPosting.getLocation());
            recommendedJob.setSalaryRange(jobPosting.getSalaryRange());
            recommendedJob.setWorkType(jobPosting.getWorkType());
            boolean matchesIntention = false;
            if (intention != null) {
                if (StringUtils.hasText(intention.getExpectedPosition()) && StringUtils.hasText(jobPosting.getTitle()) &&
                        jobPosting.getTitle().toLowerCase().contains(intention.getExpectedPosition().toLowerCase())) {
                    matchesIntention = true;
                }
                if (!matchesIntention && intention.getWorkType() != null) {
                    JobPosting.WorkType workType = mapWorkType(intention.getWorkType());
                    if (workType != null && workType == jobPosting.getWorkType()) {
                        matchesIntention = true;
                    }
                }
                if (!matchesIntention && !targetCities.isEmpty() && StringUtils.hasText(jobPosting.getLocation()) &&
                        targetCities.contains(jobPosting.getLocation().trim())) {
                    matchesIntention = true;
                }
            }
            recommendedJob.setMatchesIntention(matchesIntention);
            results.add(recommendedJob);
        }
        return results;
    }

    private JobPosting.WorkType mapWorkType(EmploymentIntention.WorkType workType) {
        if (workType == null) {
            return null;
        }
        for (JobPosting.WorkType jobWorkType : JobPosting.WorkType.values()) {
            if (jobWorkType.name().equals(workType.name())) {
                return jobWorkType;
            }
        }
        return null;
    }
}
