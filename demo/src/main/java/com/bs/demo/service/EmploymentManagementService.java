package com.bs.demo.service;

import com.bs.demo.domain.ApplicationStatus;
import com.bs.demo.domain.Employer;
import com.bs.demo.domain.EmploymentIntention;
import com.bs.demo.domain.Interview;
import com.bs.demo.domain.InterviewStatus;
import com.bs.demo.domain.JobApplication;
import com.bs.demo.domain.JobPosting;
import com.bs.demo.domain.JobStatus;
import com.bs.demo.domain.Resume;
import com.bs.demo.domain.Student;
import com.bs.demo.domain.Teacher;
import com.bs.demo.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class EmploymentManagementService {
    private final Map<Long, Student> students = new ConcurrentHashMap<>();
    private final Map<Long, Resume> resumes = new ConcurrentHashMap<>();
    private final Map<Long, Employer> employers = new ConcurrentHashMap<>();
    private final Map<Long, JobPosting> jobs = new ConcurrentHashMap<>();
    private final Map<Long, JobApplication> applications = new ConcurrentHashMap<>();
    private final Map<Long, Interview> interviews = new ConcurrentHashMap<>();
    private final Map<Long, Teacher> teachers = new ConcurrentHashMap<>();

    private final AtomicLong studentSequence = new AtomicLong(1000);
    private final AtomicLong resumeSequence = new AtomicLong(2000);
    private final AtomicLong employerSequence = new AtomicLong(3000);
    private final AtomicLong jobSequence = new AtomicLong(4000);
    private final AtomicLong applicationSequence = new AtomicLong(5000);
    private final AtomicLong interviewSequence = new AtomicLong(6000);
    private final AtomicLong teacherSequence = new AtomicLong(7000);

    public EmploymentManagementService() {
        seedData();
    }

    /* ==================== Student operations ==================== */

    public List<Student> listStudents() {
        return students.values().stream()
                .sorted((a, b) -> Long.compare(a.getId(), b.getId()))
                .collect(Collectors.toList());
    }

    public Student getStudent(Long id) {
        return Optional.ofNullable(students.get(id))
                .orElseThrow(() -> new ResourceNotFoundException("未找到ID为" + id + "的学生"));
    }

    public Student createStudent(Student payload) {
        Student student = Student.builder()
                .id(studentSequence.incrementAndGet())
                .name(payload.getName())
                .gender(payload.getGender())
                .age(payload.getAge())
                .major(payload.getMajor())
                .email(payload.getEmail())
                .phone(payload.getPhone())
                .biography(payload.getBiography())
                .educationHistory(new ArrayList<>(defaultList(payload.getEducationHistory())))
                .practiceExperience(new ArrayList<>(defaultList(payload.getPracticeExperience())))
                .awards(new ArrayList<>(defaultList(payload.getAwards())))
                .employmentIntention(payload.getEmploymentIntention())
                .build();
        students.put(student.getId(), student);
        return student;
    }

    public Student updateStudent(Long id, Student update) {
        Student existing = getStudent(id);
        if (update.getName() != null) {
            existing.setName(update.getName());
        }
        if (update.getGender() != null) {
            existing.setGender(update.getGender());
        }
        if (update.getAge() != null) {
            existing.setAge(update.getAge());
        }
        if (update.getMajor() != null) {
            existing.setMajor(update.getMajor());
        }
        if (update.getEmail() != null) {
            existing.setEmail(update.getEmail());
        }
        if (update.getPhone() != null) {
            existing.setPhone(update.getPhone());
        }
        if (update.getBiography() != null) {
            existing.setBiography(update.getBiography());
        }
        if (update.getEducationHistory() != null) {
            existing.setEducationHistory(new ArrayList<>(update.getEducationHistory()));
        }
        if (update.getPracticeExperience() != null) {
            existing.setPracticeExperience(new ArrayList<>(update.getPracticeExperience()));
        }
        if (update.getAwards() != null) {
            existing.setAwards(new ArrayList<>(update.getAwards()));
        }
        return existing;
    }

    public EmploymentIntention updateIntention(Long studentId, EmploymentIntention intention) {
        Student student = getStudent(studentId);
        EmploymentIntention merged = EmploymentIntention.builder()
                .expectedPosition(intention.getExpectedPosition())
                .salaryRange(intention.getSalaryRange())
                .preferredCities(new ArrayList<>(defaultList(intention.getPreferredCities())))
                .workType(intention.getWorkType())
                .notes(intention.getNotes())
                .build();
        student.setEmploymentIntention(merged);
        return merged;
    }

    public Resume createResume(Long studentId, Resume resumePayload) {
        Student student = getStudent(studentId);
        Resume resume = Resume.builder()
                .id(resumeSequence.incrementAndGet())
                .studentId(studentId)
                .title(resumePayload.getTitle())
                .summary(resumePayload.getSummary())
                .skills(new ArrayList<>(defaultList(resumePayload.getSkills())))
                .experiences(new ArrayList<>(defaultList(resumePayload.getExperiences())))
                .attachments(new ArrayList<>(defaultList(resumePayload.getAttachments())))
                .portfolioUrl(resumePayload.getPortfolioUrl())
                .build();
        resumes.put(resume.getId(), resume);
        student.getResumeIds().add(resume.getId());
        return resume;
    }

    public List<Resume> listResumes(Long studentId) {
        Student student = getStudent(studentId);
        return student.getResumeIds().stream()
                .map(resumes::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<JobApplication> listApplicationsByStudent(Long studentId) {
        Student student = getStudent(studentId);
        return student.getApplicationIds().stream()
                .map(applications::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /* ==================== Employer operations ==================== */

    public List<Employer> listEmployers() {
        return employers.values().stream()
                .sorted((a, b) -> Long.compare(a.getId(), b.getId()))
                .collect(Collectors.toList());
    }

    public Employer getEmployer(Long id) {
        return Optional.ofNullable(employers.get(id))
                .orElseThrow(() -> new ResourceNotFoundException("未找到ID为" + id + "的企业"));
    }

    public Employer createEmployer(Employer payload) {
        Employer employer = Employer.builder()
                .id(employerSequence.incrementAndGet())
                .name(payload.getName())
                .contactPerson(payload.getContactPerson())
                .contactEmail(payload.getContactEmail())
                .contactPhone(payload.getContactPhone())
                .description(payload.getDescription())
                .build();
        employers.put(employer.getId(), employer);
        return employer;
    }

    /* ==================== Job operations ==================== */

    public JobPosting createJob(Long employerId, JobPosting payload) {
        Employer employer = getEmployer(employerId);
        JobPosting job = JobPosting.builder()
                .id(jobSequence.incrementAndGet())
                .employerId(employerId)
                .title(payload.getTitle())
                .description(payload.getDescription())
                .salaryRange(payload.getSalaryRange())
                .location(payload.getLocation())
                .workType(payload.getWorkType())
                .requirements(new ArrayList<>(defaultList(payload.getRequirements())))
                .status(Optional.ofNullable(payload.getStatus()).orElse(JobStatus.OPEN))
                .publishedDate(Optional.ofNullable(payload.getPublishedDate()).orElse(LocalDate.now()))
                .build();
        jobs.put(job.getId(), job);
        employer.getJobIds().add(job.getId());
        return job;
    }

    public JobPosting getJob(Long id) {
        return Optional.ofNullable(jobs.get(id))
                .orElseThrow(() -> new ResourceNotFoundException("未找到ID为" + id + "的岗位"));
    }

    public List<JobPosting> listJobs(String keyword, String location, String workType) {
        return jobs.values().stream()
                .filter(job -> keyword == null || keyword.isBlank() || containsIgnoreCase(job.getTitle(), keyword) || containsIgnoreCase(job.getDescription(), keyword))
                .filter(job -> location == null || location.isBlank() || containsIgnoreCase(job.getLocation(), location))
                .filter(job -> workType == null || workType.isBlank() || containsIgnoreCase(job.getWorkType(), workType))
                .sorted((a, b) -> {
                    if (a.getPublishedDate() == null && b.getPublishedDate() == null) {
                        return 0;
                    }
                    if (a.getPublishedDate() == null) {
                        return 1;
                    }
                    if (b.getPublishedDate() == null) {
                        return -1;
                    }
                    return b.getPublishedDate().compareTo(a.getPublishedDate());
                })
                .collect(Collectors.toList());
    }

    public List<JobPosting> listJobsByEmployer(Long employerId) {
        Employer employer = getEmployer(employerId);
        return employer.getJobIds().stream()
                .map(jobs::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public JobApplication applyForJob(Long jobId, JobApplication payload) {
        JobPosting job = getJob(jobId);
        Student student = getStudent(payload.getStudentId());
        if (!student.getResumeIds().contains(payload.getResumeId())) {
            throw new IllegalArgumentException("简历编号与学生不匹配");
        }
        JobApplication application = JobApplication.builder()
                .id(applicationSequence.incrementAndGet())
                .jobId(jobId)
                .studentId(payload.getStudentId())
                .resumeId(payload.getResumeId())
                .coverLetter(payload.getCoverLetter())
                .status(Optional.ofNullable(payload.getStatus()).orElse(ApplicationStatus.SUBMITTED))
                .appliedAt(Optional.ofNullable(payload.getAppliedAt()).orElse(LocalDateTime.now()))
                .build();
        applications.put(application.getId(), application);
        job.getApplicationIds().add(application.getId());
        student.getApplicationIds().add(application.getId());
        return application;
    }

    public List<JobApplication> listApplicationsByJob(Long jobId) {
        JobPosting job = getJob(jobId);
        return job.getApplicationIds().stream()
                .map(applications::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public JobApplication updateApplicationStatus(Long applicationId, ApplicationStatus status) {
        JobApplication application = Optional.ofNullable(applications.get(applicationId))
                .orElseThrow(() -> new ResourceNotFoundException("未找到ID为" + applicationId + "的申请"));
        application.setStatus(status);
        return application;
    }

    /* ==================== Interview operations ==================== */

    public Interview scheduleInterview(Long jobId, Interview payload) {
        JobPosting job = getJob(jobId);
        JobApplication application = Optional.ofNullable(applications.get(payload.getApplicationId()))
                .orElseThrow(() -> new ResourceNotFoundException("未找到对应的求职申请"));
        if (!job.getApplicationIds().contains(application.getId())) {
            throw new IllegalArgumentException("该申请不属于当前岗位");
        }
        Interview interview = Interview.builder()
                .id(interviewSequence.incrementAndGet())
                .jobId(jobId)
                .applicationId(application.getId())
                .scheduledTime(Optional.ofNullable(payload.getScheduledTime()).orElse(LocalDateTime.now().plusDays(1)))
                .location(payload.getLocation())
                .meetingLink(payload.getMeetingLink())
                .status(Optional.ofNullable(payload.getStatus()).orElse(InterviewStatus.SCHEDULED))
                .feedback(payload.getFeedback())
                .build();
        interviews.put(interview.getId(), interview);
        job.getInterviewIds().add(interview.getId());
        return interview;
    }

    public List<Interview> listInterviewsForJob(Long jobId) {
        JobPosting job = getJob(jobId);
        return job.getInterviewIds().stream()
                .map(interviews::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Interview updateInterview(Long interviewId, InterviewStatus status, String feedback) {
        Interview interview = Optional.ofNullable(interviews.get(interviewId))
                .orElseThrow(() -> new ResourceNotFoundException("未找到ID为" + interviewId + "的面试"));
        if (status != null) {
            interview.setStatus(status);
        }
        if (feedback != null) {
            interview.setFeedback(feedback);
        }
        return interview;
    }

    /* ==================== Teacher operations ==================== */

    public List<Teacher> listTeachers() {
        return teachers.values().stream()
                .sorted((a, b) -> Long.compare(a.getId(), b.getId()))
                .collect(Collectors.toList());
    }

    public Teacher createTeacher(Teacher payload) {
        Teacher teacher = Teacher.builder()
                .id(teacherSequence.incrementAndGet())
                .name(payload.getName())
                .department(payload.getDepartment())
                .email(payload.getEmail())
                .phone(payload.getPhone())
                .guidanceNotes(new ConcurrentHashMap<>())
                .build();
        teachers.put(teacher.getId(), teacher);
        return teacher;
    }

    public Teacher addGuidance(Long teacherId, Long studentId, String note) {
        Teacher teacher = Optional.ofNullable(teachers.get(teacherId))
                .orElseThrow(() -> new ResourceNotFoundException("未找到ID为" + teacherId + "的教师"));
        getStudent(studentId); // ensure student exists
        teacher.getGuidanceNotes().put(studentId, note);
        return teacher;
    }

    /* ==================== Dashboard ==================== */

    public Map<String, Object> getDashboardSummary() {
        long openJobs = jobs.values().stream().filter(job -> job.getStatus() == JobStatus.OPEN).count();
        long scheduledInterviews = interviews.values().stream().filter(i -> i.getStatus() == InterviewStatus.SCHEDULED).count();
        long pendingApplications = applications.values().stream().filter(a -> a.getStatus() == ApplicationStatus.SUBMITTED || a.getStatus() == ApplicationStatus.REVIEWING).count();
        return Map.of(
                "students", students.size(),
                "employers", employers.size(),
                "teachers", teachers.size(),
                "openJobs", openJobs,
                "pendingApplications", pendingApplications,
                "scheduledInterviews", scheduledInterviews
        );
    }

    /* ==================== Helper methods ==================== */

    private boolean containsIgnoreCase(String source, String keyword) {
        return source != null && source.toLowerCase().contains(keyword.toLowerCase());
    }

    private List<String> defaultList(List<String> input) {
        return input == null ? Collections.emptyList() : input;
    }

    private void seedData() {
        Student alice = createStudent(Student.builder()
                .name("王小雨")
                .gender("女")
                .age(22)
                .major("计算机科学与技术")
                .email("alice@example.edu")
                .phone("13800000001")
                .biography("热衷于人工智能方向的应届毕业生")
                .educationHistory(List.of("2021-2025 XX大学 计算机科学与技术"))
                .practiceExperience(List.of("2024年于ABC科技担任算法实习生"))
                .awards(List.of("2023全国大学生数学建模竞赛二等奖"))
                .employmentIntention(EmploymentIntention.builder()
                        .expectedPosition("算法工程师")
                        .salaryRange("15k-20k")
                        .preferredCities(List.of("上海", "杭州"))
                        .workType("全职")
                        .notes("希望有完善的培养机制")
                        .build())
                .build());

        Student bob = createStudent(Student.builder()
                .name("李晨")
                .gender("男")
                .age(23)
                .major("信息管理与信息系统")
                .email("bob@example.edu")
                .phone("13800000002")
                .biography("具备良好沟通能力和项目管理经验")
                .educationHistory(List.of("2021-2025 XX大学 信息管理与信息系统"))
                .practiceExperience(List.of("2023年于XYZ公司产品运营实习"))
                .awards(List.of("校级优秀学生干部"))
                .employmentIntention(EmploymentIntention.builder()
                        .expectedPosition("产品经理")
                        .salaryRange("12k-18k")
                        .preferredCities(List.of("北京"))
                        .workType("全职")
                        .notes("希望加入互联网平台型企业")
                        .build())
                .build());

        Resume aliceResume = createResume(alice.getId(), Resume.builder()
                .title("算法工程师求职简历")
                .summary("熟悉机器学习与数据挖掘算法，有丰富竞赛经历")
                .skills(List.of("Python", "TensorFlow", "Pytorch", "SQL"))
                .experiences(List.of(
                        "ABC科技 - 算法实习生 (2024.06-2024.09)",
                        "高校科研项目 - 智能推荐系统"))
                .portfolioUrl("https://portfolio.example.com/alice")
                .build());

        Resume bobResume = createResume(bob.getId(), Resume.builder()
                .title("产品经理求职简历")
                .summary("精通需求分析与跨团队协作，熟练使用Axure")
                .skills(List.of("需求分析", "项目管理", "Axure"))
                .experiences(List.of(
                        "XYZ公司 - 产品实习生 (2023.05-2023.09)",
                        "校园社团活动策划负责人"))
                .build());

        Employer techCorp = createEmployer(Employer.builder()
                .name("星云科技有限公司")
                .contactPerson("张经理")
                .contactEmail("hr@nebula-tech.com")
                .contactPhone("021-12345678")
                .description("专注于云计算与AI解决方案的高科技企业")
                .build());

        Employer financeCorp = createEmployer(Employer.builder()
                .name("恒信金融集团")
                .contactPerson("王女士")
                .contactEmail("talent@hengxin-finance.com")
                .contactPhone("010-87654321")
                .description("大型综合性金融集团，提供多元化金融服务")
                .build());

        JobPosting algorithmJob = createJob(techCorp.getId(), JobPosting.builder()
                .title("算法工程师")
                .description("负责推荐系统相关算法的设计与优化")
                .salaryRange("18k-25k")
                .location("上海")
                .workType("全职")
                .requirements(List.of("熟悉机器学习算法", "掌握Python", "了解推荐系统原理"))
                .status(JobStatus.OPEN)
                .publishedDate(LocalDate.now().minusDays(3))
                .build());

        JobPosting productJob = createJob(financeCorp.getId(), JobPosting.builder()
                .title("产品经理")
                .description("负责互联网金融产品的需求分析与上线运营")
                .salaryRange("15k-22k")
                .location("北京")
                .workType("全职")
                .requirements(List.of("良好的沟通能力", "具备项目管理经验"))
                .status(JobStatus.OPEN)
                .publishedDate(LocalDate.now().minusDays(1))
                .build());

        JobApplication aliceApplication = applyForJob(algorithmJob.getId(), JobApplication.builder()
                .studentId(alice.getId())
                .resumeId(aliceResume.getId())
                .coverLetter("期待加入贵公司，与团队共同打造智能产品")
                .status(ApplicationStatus.REVIEWING)
                .appliedAt(LocalDateTime.now().minusDays(2))
                .build());

        applyForJob(productJob.getId(), JobApplication.builder()
                .studentId(bob.getId())
                .resumeId(bobResume.getId())
                .coverLetter("在产品规划与跨部门协作方面有丰富经验")
                .status(ApplicationStatus.SUBMITTED)
                .appliedAt(LocalDateTime.now().minusDays(1))
                .build());

        scheduleInterview(algorithmJob.getId(), Interview.builder()
                .applicationId(aliceApplication.getId())
                .scheduledTime(LocalDateTime.now().plusDays(1))
                .location("上海市浦东新区创新大厦301会议室")
                .status(InterviewStatus.SCHEDULED)
                .build());

        createTeacher(Teacher.builder()
                .name("赵老师")
                .department("计算机学院就业办")
                .email("zhao.teacher@example.edu")
                .phone("13600000003")
                .build());
    }
}
