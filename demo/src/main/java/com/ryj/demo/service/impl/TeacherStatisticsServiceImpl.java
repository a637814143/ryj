package com.ryj.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ryj.demo.dto.EmploymentStatisticsDto;
import com.ryj.demo.entity.*;
import com.ryj.demo.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherStatisticsServiceImpl implements TeacherStatisticsService {

    private final TeacherGuidanceService guidanceService;
    private final StudentProfileService studentProfileService;
    private final JobApplicationService jobApplicationService;
    private final InterviewService interviewService;
    private final JobPostingService jobPostingService;
    private final EmployerService employerService;
    private final SysUserService sysUserService;

    @Override
    public EmploymentStatisticsDto getEmploymentStatistics(Long teacherId, String scope) {
        EmploymentStatisticsDto stats = new EmploymentStatisticsDto();

        // 获取学生ID列表
        Set<Long> studentIds = getStudentIds(teacherId, scope);

        if (studentIds.isEmpty()) {
            // 返回空统计数据
            stats.setOverview(createEmptyOverview());
            stats.setIndustryDistribution(new ArrayList<>());
            stats.setLocationDistribution(new ArrayList<>());
            stats.setSalaryDistribution(createEmptySalaryDistribution());
            stats.setMajorEmploymentRates(new ArrayList<>());
            stats.setMonthlyTrends(new ArrayList<>());
            return stats;
        }

        // 1. 总览统计
        stats.setOverview(calculateOverviewStats(studentIds));

        // 2. 行业分布
        stats.setIndustryDistribution(calculateIndustryDistribution(studentIds));

        // 3. 地区分布
        stats.setLocationDistribution(calculateLocationDistribution(studentIds));

        // 4. 薪资分布
        stats.setSalaryDistribution(calculateSalaryDistribution(studentIds));

        // 5. 专业就业率
        stats.setMajorEmploymentRates(calculateMajorEmploymentRates(studentIds));

        // 6. 月度趋势
        stats.setMonthlyTrends(calculateMonthlyTrends(studentIds));

        return stats;
    }

    /**
     * 获取学生ID列表
     */
    private Set<Long> getStudentIds(Long teacherId, String scope) {
        if ("all".equalsIgnoreCase(scope)) {
            // 获取所有学生
            List<SysUser> allStudents = sysUserService.list(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getRole, SysUser.Role.STUDENT)
            );
            return allStudents.stream().map(SysUser::getId).collect(Collectors.toSet());
        } else {
            // 仅获取结对学生
            List<TeacherGuidance> guidances = guidanceService.list(
                new LambdaQueryWrapper<TeacherGuidance>().eq(TeacherGuidance::getTeacherId, teacherId)
            );
            return guidances.stream().map(TeacherGuidance::getStudentId).collect(Collectors.toSet());
        }
    }

    /**
     * 计算总览统计
     */
    private EmploymentStatisticsDto.OverviewStats calculateOverviewStats(Set<Long> studentIds) {
        EmploymentStatisticsDto.OverviewStats overview = new EmploymentStatisticsDto.OverviewStats();
        
        overview.setTotalStudents(studentIds.size());

        // 统计已就业学生（获得Offer的学生）
        long employedCount = jobApplicationService.count(
            new LambdaQueryWrapper<JobApplication>()
                .in(JobApplication::getStudentId, studentIds)
                .eq(JobApplication::getStatus, JobApplication.Status.OFFERED)
        );
        
        Set<Long> employedStudentIds = jobApplicationService.list(
            new LambdaQueryWrapper<JobApplication>()
                .in(JobApplication::getStudentId, studentIds)
                .eq(JobApplication::getStatus, JobApplication.Status.OFFERED)
        ).stream().map(JobApplication::getStudentId).collect(Collectors.toSet());
        
        overview.setEmployedStudents(employedStudentIds.size());

        // 统计求职中学生
        Set<Long> activeStudentIds = jobApplicationService.list(
            new LambdaQueryWrapper<JobApplication>()
                .in(JobApplication::getStudentId, studentIds)
                .in(JobApplication::getStatus, Arrays.asList(
                    JobApplication.Status.SUBMITTED,
                    JobApplication.Status.REVIEWING,
                    JobApplication.Status.INTERVIEW
                ))
        ).stream().map(JobApplication::getStudentId).collect(Collectors.toSet());
        
        overview.setActivelyApplying(activeStudentIds.size());

        // 统计未开始求职学生
        Set<Long> hasApplicationIds = jobApplicationService.list(
            new LambdaQueryWrapper<JobApplication>().in(JobApplication::getStudentId, studentIds)
        ).stream().map(JobApplication::getStudentId).collect(Collectors.toSet());
        
        overview.setNotStarted(studentIds.size() - hasApplicationIds.size());

        // 计算就业率
        if (studentIds.size() > 0) {
            overview.setEmploymentRate((double) employedStudentIds.size() / studentIds.size() * 100);
        } else {
            overview.setEmploymentRate(0.0);
        }

        // 统计申请、面试、Offer数
        overview.setTotalApplications((int) jobApplicationService.count(
            new LambdaQueryWrapper<JobApplication>().in(JobApplication::getStudentId, studentIds)
        ));

        List<Long> applicationIds = jobApplicationService.list(
            new LambdaQueryWrapper<JobApplication>().in(JobApplication::getStudentId, studentIds)
        ).stream().map(JobApplication::getId).collect(Collectors.toList());

        if (!applicationIds.isEmpty()) {
            overview.setTotalInterviews((int) interviewService.count(
                new LambdaQueryWrapper<Interview>().in(Interview::getApplicationId, applicationIds)
            ));
        } else {
            overview.setTotalInterviews(0);
        }

        overview.setOffersReceived((int) jobApplicationService.count(
            new LambdaQueryWrapper<JobApplication>()
                .in(JobApplication::getStudentId, studentIds)
                .eq(JobApplication::getStatus, JobApplication.Status.OFFERED)
        ));

        return overview;
    }

    /**
     * 计算行业分布（按公司统计）
     */
    private List<EmploymentStatisticsDto.IndustryDistribution> calculateIndustryDistribution(Set<Long> studentIds) {
        // 获取所有已就业学生的申请
        List<JobApplication> employedApps = jobApplicationService.list(
            new LambdaQueryWrapper<JobApplication>()
                .in(JobApplication::getStudentId, studentIds)
                .eq(JobApplication::getStatus, JobApplication.Status.OFFERED)
        );

        // 统计公司分布（作为行业分布的替代）
        Map<String, Integer> companyMap = new HashMap<>();
        int total = 0;

        for (JobApplication app : employedApps) {
            if (app.getJobId() == null) continue;
            JobPosting job = jobPostingService.getById(app.getJobId());
            if (job == null || job.getEmployerId() == null) continue;
            
            Employer employer = employerService.getById(job.getEmployerId());
            if (employer == null || employer.getCompanyName() == null) continue;

            String company = employer.getCompanyName();
            companyMap.put(company, companyMap.getOrDefault(company, 0) + 1);
            total++;
        }

        // 转换为结果列表
        final int finalTotal = total;
        return companyMap.entrySet().stream()
            .map(entry -> {
                EmploymentStatisticsDto.IndustryDistribution dist = new EmploymentStatisticsDto.IndustryDistribution();
                dist.setIndustry(entry.getKey());
                dist.setCount(entry.getValue());
                dist.setPercentage(finalTotal > 0 ? (double) entry.getValue() / finalTotal * 100 : 0);
                return dist;
            })
            .sorted((a, b) -> Integer.compare(b.getCount(), a.getCount()))
            .limit(10)  // 只显示前10个公司
            .collect(Collectors.toList());
    }

    /**
     * 计算地区分布
     */
    private List<EmploymentStatisticsDto.LocationDistribution> calculateLocationDistribution(Set<Long> studentIds) {
        // 获取所有已就业学生的申请
        List<JobApplication> employedApps = jobApplicationService.list(
            new LambdaQueryWrapper<JobApplication>()
                .in(JobApplication::getStudentId, studentIds)
                .eq(JobApplication::getStatus, JobApplication.Status.OFFERED)
        );

        // 统计地区分布
        Map<String, Integer> locationMap = new HashMap<>();
        int total = 0;

        for (JobApplication app : employedApps) {
            if (app.getJobId() == null) continue;
            JobPosting job = jobPostingService.getById(app.getJobId());
            if (job == null || job.getLocation() == null) continue;

            String location = job.getLocation();
            locationMap.put(location, locationMap.getOrDefault(location, 0) + 1);
            total++;
        }

        // 转换为结果列表
        final int finalTotal = total;
        return locationMap.entrySet().stream()
            .map(entry -> {
                EmploymentStatisticsDto.LocationDistribution dist = new EmploymentStatisticsDto.LocationDistribution();
                dist.setLocation(entry.getKey());
                dist.setCount(entry.getValue());
                dist.setPercentage(finalTotal > 0 ? (double) entry.getValue() / finalTotal * 100 : 0);
                return dist;
            })
            .sorted((a, b) -> Integer.compare(b.getCount(), a.getCount()))
            .collect(Collectors.toList());
    }

    /**
     * 计算薪资分布（基于薪资范围字符串解析）
     */
    private EmploymentStatisticsDto.SalaryDistribution calculateSalaryDistribution(Set<Long> studentIds) {
        EmploymentStatisticsDto.SalaryDistribution salaryDist = new EmploymentStatisticsDto.SalaryDistribution();
        
        // 获取所有已就业学生的职位信息
        List<JobApplication> employedApps = jobApplicationService.list(
            new LambdaQueryWrapper<JobApplication>()
                .in(JobApplication::getStudentId, studentIds)
                .eq(JobApplication::getStatus, JobApplication.Status.OFFERED)
        );

        List<Integer> salaries = new ArrayList<>();
        int below5k = 0, range5to8k = 0, range8to12k = 0, range12to20k = 0, above20k = 0;

        for (JobApplication app : employedApps) {
            if (app.getJobId() == null) continue;
            JobPosting job = jobPostingService.getById(app.getJobId());
            if (job == null || job.getSalaryRange() == null) continue;

            // 尝试从薪资范围字符串中解析薪资（简单处理）
            Integer avgSalary = parseSalaryRange(job.getSalaryRange());
            if (avgSalary == null) continue;

            salaries.add(avgSalary);

            if (avgSalary < 5000) below5k++;
            else if (avgSalary < 8000) range5to8k++;
            else if (avgSalary < 12000) range8to12k++;
            else if (avgSalary < 20000) range12to20k++;
            else above20k++;
        }

        salaryDist.setBelow5k(below5k);
        salaryDist.setRange5to8k(range5to8k);
        salaryDist.setRange8to12k(range8to12k);
        salaryDist.setRange12to20k(range12to20k);
        salaryDist.setAbove20k(above20k);

        // 计算平均薪资
        if (!salaries.isEmpty()) {
            double avg = salaries.stream().mapToInt(Integer::intValue).average().orElse(0);
            salaryDist.setAverageSalary(avg);

            // 计算中位数
            Collections.sort(salaries);
            int median = salaries.get(salaries.size() / 2);
            salaryDist.setMedianSalary((double) median);
        } else {
            salaryDist.setAverageSalary(0.0);
            salaryDist.setMedianSalary(0.0);
        }

        return salaryDist;
    }

    /**
     * 解析薪资范围字符串（如 "8k-12k", "10000-15000"）
     * 返回平均值
     */
    private Integer parseSalaryRange(String salaryRange) {
        if (salaryRange == null || salaryRange.trim().isEmpty()) {
            return null;
        }

        try {
            // 移除空格
            String range = salaryRange.trim().toLowerCase();
            
            // 处理 "8k-12k" 格式
            if (range.contains("k")) {
                range = range.replace("k", "");
                String[] parts = range.split("-");
                if (parts.length == 2) {
                    int min = (int) (Double.parseDouble(parts[0].trim()) * 1000);
                    int max = (int) (Double.parseDouble(parts[1].trim()) * 1000);
                    return (min + max) / 2;
                } else if (parts.length == 1) {
                    return (int) (Double.parseDouble(parts[0].trim()) * 1000);
                }
            }
            
            // 处理 "8000-12000" 格式
            if (range.contains("-")) {
                String[] parts = range.split("-");
                if (parts.length == 2) {
                    int min = Integer.parseInt(parts[0].trim());
                    int max = Integer.parseInt(parts[1].trim());
                    return (min + max) / 2;
                }
            }
            
            // 尝试直接解析数字
            return Integer.parseInt(range);
        } catch (Exception e) {
            // 解析失败返回 null
            return null;
        }
    }

    /**
     * 计算专业就业率
     */
    private List<EmploymentStatisticsDto.MajorEmploymentRate> calculateMajorEmploymentRates(Set<Long> studentIds) {
        // 按专业分组学生
        Map<String, Set<Long>> majorStudentsMap = new HashMap<>();
        Map<String, Set<Long>> majorEmployedMap = new HashMap<>();

        for (Long studentId : studentIds) {
            StudentProfile profile = studentProfileService.getById(studentId);
            String major = profile != null && profile.getMajor() != null ? profile.getMajor() : "未知专业";
            
            majorStudentsMap.computeIfAbsent(major, k -> new HashSet<>()).add(studentId);

            // 检查是否已就业
            long offersCount = jobApplicationService.count(
                new LambdaQueryWrapper<JobApplication>()
                    .eq(JobApplication::getStudentId, studentId)
                    .eq(JobApplication::getStatus, JobApplication.Status.OFFERED)
            );

            if (offersCount > 0) {
                majorEmployedMap.computeIfAbsent(major, k -> new HashSet<>()).add(studentId);
            }
        }

        // 计算每个专业的就业率
        return majorStudentsMap.entrySet().stream()
            .map(entry -> {
                String major = entry.getKey();
                int totalStudents = entry.getValue().size();
                int employedStudents = majorEmployedMap.getOrDefault(major, new HashSet<>()).size();
                
                EmploymentStatisticsDto.MajorEmploymentRate rate = new EmploymentStatisticsDto.MajorEmploymentRate();
                rate.setMajor(major);
                rate.setTotalStudents(totalStudents);
                rate.setEmployedStudents(employedStudents);
                rate.setEmploymentRate(totalStudents > 0 ? (double) employedStudents / totalStudents * 100 : 0);
                return rate;
            })
            .sorted((a, b) -> Double.compare(b.getEmploymentRate(), a.getEmploymentRate()))
            .collect(Collectors.toList());
    }

    /**
     * 计算月度趋势
     */
    private List<EmploymentStatisticsDto.MonthlyTrend> calculateMonthlyTrends(Set<Long> studentIds) {
        // 获取所有申请
        List<JobApplication> allApps = jobApplicationService.list(
            new LambdaQueryWrapper<JobApplication>().in(JobApplication::getStudentId, studentIds)
        );

        // 按月份分组
        Map<String, EmploymentStatisticsDto.MonthlyTrend> trendsMap = new TreeMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        for (JobApplication app : allApps) {
            if (app.getAppliedAt() == null) continue;
            
            String month = app.getAppliedAt().format(formatter);
            EmploymentStatisticsDto.MonthlyTrend trend = trendsMap.computeIfAbsent(month, k -> {
                EmploymentStatisticsDto.MonthlyTrend t = new EmploymentStatisticsDto.MonthlyTrend();
                t.setMonth(k);
                t.setApplications(0);
                t.setInterviews(0);
                t.setOffers(0);
                return t;
            });

            trend.setApplications(trend.getApplications() + 1);

            // 统计面试数
            long interviewCount = interviewService.count(
                new LambdaQueryWrapper<Interview>().eq(Interview::getApplicationId, app.getId())
            );
            if (interviewCount > 0) {
                trend.setInterviews(trend.getInterviews() + 1);
            }

            // 统计Offer数
            if (app.getStatus() == JobApplication.Status.OFFERED) {
                trend.setOffers(trend.getOffers() + 1);
            }
        }

        return new ArrayList<>(trendsMap.values());
    }

    /**
     * 创建空的总览数据
     */
    private EmploymentStatisticsDto.OverviewStats createEmptyOverview() {
        EmploymentStatisticsDto.OverviewStats overview = new EmploymentStatisticsDto.OverviewStats();
        overview.setTotalStudents(0);
        overview.setEmployedStudents(0);
        overview.setActivelyApplying(0);
        overview.setNotStarted(0);
        overview.setEmploymentRate(0.0);
        overview.setTotalApplications(0);
        overview.setTotalInterviews(0);
        overview.setOffersReceived(0);
        return overview;
    }

    /**
     * 创建空的薪资分布数据
     */
    private EmploymentStatisticsDto.SalaryDistribution createEmptySalaryDistribution() {
        EmploymentStatisticsDto.SalaryDistribution salaryDist = new EmploymentStatisticsDto.SalaryDistribution();
        salaryDist.setBelow5k(0);
        salaryDist.setRange5to8k(0);
        salaryDist.setRange8to12k(0);
        salaryDist.setRange12to20k(0);
        salaryDist.setAbove20k(0);
        salaryDist.setAverageSalary(0.0);
        salaryDist.setMedianSalary(0.0);
        return salaryDist;
    }
}

