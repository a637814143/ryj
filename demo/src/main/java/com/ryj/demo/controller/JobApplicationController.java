package com.ryj.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ryj.demo.common.ApiResponse;
import com.ryj.demo.dto.JobApplicationRequest;
import com.ryj.demo.entity.JobApplication;
import com.ryj.demo.service.JobApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/job-applications")
@RequiredArgsConstructor
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    @PostMapping
    public ApiResponse<JobApplication> create(@Valid @RequestBody JobApplicationRequest request) {
        JobApplication application = new JobApplication();
        application.setJobId(request.getJobId());
        application.setStudentId(request.getStudentId());
        application.setResumeId(request.getResumeId());
        application.setStatus(request.getStatus() != null ? request.getStatus() : JobApplication.Status.SUBMITTED);
        application.setCoverLetter(request.getCoverLetter());
        jobApplicationService.save(application);
        return ApiResponse.success(application);
    }

    @PutMapping("/{id}")
    public ApiResponse<Boolean> updateStatus(@PathVariable Long id, @RequestBody JobApplicationRequest request) {
        JobApplication application = jobApplicationService.getById(id);
        if (application == null) {
            throw new IllegalArgumentException("申请不存在");
        }
        if (request.getStatus() != null) {
            application.setStatus(request.getStatus());
        }
        application.setCoverLetter(request.getCoverLetter());
        return ApiResponse.success(jobApplicationService.updateById(application));
    }

    @GetMapping
    public ApiResponse<Page<JobApplication>> list(@RequestParam(defaultValue = "1") long page,
                                                  @RequestParam(defaultValue = "10") long size,
                                                  @RequestParam(required = false) Long jobId,
                                                  @RequestParam(required = false) Long studentId) {
        LambdaQueryWrapper<JobApplication> wrapper = new LambdaQueryWrapper<>();
        if (jobId != null) {
            wrapper.eq(JobApplication::getJobId, jobId);
        }
        if (studentId != null) {
            wrapper.eq(JobApplication::getStudentId, studentId);
        }
        Page<JobApplication> result = jobApplicationService.page(new Page<>(page, size), wrapper);
        return ApiResponse.success(result);
    }

    @GetMapping("/{id}")
    public ApiResponse<JobApplication> detail(@PathVariable Long id) {
        return ApiResponse.success(jobApplicationService.getById(id));
    }
}
