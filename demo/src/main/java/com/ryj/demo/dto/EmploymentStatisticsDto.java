package com.ryj.demo.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 就业统计数据传输对象
 */
@Data
public class EmploymentStatisticsDto {

    /**
     * 总览统计
     */
    private OverviewStats overview;

    /**
     * 按行业分布
     */
    private List<IndustryDistribution> industryDistribution;

    /**
     * 按地区分布
     */
    private List<LocationDistribution> locationDistribution;

    /**
     * 薪资水平分布
     */
    private SalaryDistribution salaryDistribution;

    /**
     * 按专业就业率
     */
    private List<MajorEmploymentRate> majorEmploymentRates;

    /**
     * 就业趋势（按月）
     */
    private List<MonthlyTrend> monthlyTrends;

    @Data
    public static class OverviewStats {
        private int totalStudents;           // 总学生数
        private int employedStudents;        // 已就业学生数
        private int activelyApplying;        // 求职中学生数
        private int notStarted;              // 未开始求职学生数
        private double employmentRate;       // 就业率
        private int totalApplications;       // 总申请数
        private int totalInterviews;         // 总面试数
        private int offersReceived;          // 获得Offer数
    }

    @Data
    public static class IndustryDistribution {
        private String industry;             // 行业名称
        private int count;                   // 人数
        private double percentage;           // 百分比
    }

    @Data
    public static class LocationDistribution {
        private String location;             // 地区
        private int count;                   // 人数
        private double percentage;           // 百分比
    }

    @Data
    public static class SalaryDistribution {
        private int below5k;                 // 5k以下
        private int range5to8k;              // 5-8k
        private int range8to12k;             // 8-12k
        private int range12to20k;            // 12-20k
        private int above20k;                // 20k以上
        private Double averageSalary;        // 平均薪资
        private Double medianSalary;         // 中位数薪资
    }

    @Data
    public static class MajorEmploymentRate {
        private String major;                // 专业
        private int totalStudents;           // 总学生数
        private int employedStudents;        // 已就业学生数
        private double employmentRate;       // 就业率
    }

    @Data
    public static class MonthlyTrend {
        private String month;                // 月份 (yyyy-MM)
        private int applications;            // 申请数
        private int interviews;              // 面试数
        private int offers;                  // Offer数
    }
}

