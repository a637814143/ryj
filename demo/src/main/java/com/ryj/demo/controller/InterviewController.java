package com.ryj.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ryj.demo.common.ApiResponse;
import com.ryj.demo.dto.InterviewRequest;
import com.ryj.demo.entity.Interview;
import com.ryj.demo.service.InterviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping
    public ApiResponse<Interview> create(@Valid @RequestBody InterviewRequest request) {
        Interview interview = new Interview();
        apply(interview, request);
        interviewService.save(interview);
        return ApiResponse.success(interview);
    }

    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Long id, @Valid @RequestBody InterviewRequest request) {
        Interview interview = interviewService.getById(id);
        if (interview == null) {
            throw new IllegalArgumentException("面试记录不存在");
        }
        apply(interview, request);
        return ApiResponse.success(interviewService.updateById(interview));
    }

    @GetMapping
    public ApiResponse<Page<Interview>> list(@RequestParam(defaultValue = "1") long page,
                                             @RequestParam(defaultValue = "10") long size,
                                             @RequestParam(required = false) Long jobId,
                                             @RequestParam(required = false) Long applicationId,
                                             @RequestParam(required = false) Interview.Status status) {
        LambdaQueryWrapper<Interview> wrapper = new LambdaQueryWrapper<>();
        if (jobId != null) {
            wrapper.eq(Interview::getJobId, jobId);
        }
        if (applicationId != null) {
            wrapper.eq(Interview::getApplicationId, applicationId);
        }
        if (status != null) {
            wrapper.eq(Interview::getStatus, status);
        }
        Page<Interview> result = interviewService.page(new Page<>(page, size), wrapper);
        return ApiResponse.success(result);
    }

    @GetMapping("/{id}")
    public ApiResponse<Interview> detail(@PathVariable Long id) {
        return ApiResponse.success(interviewService.getById(id));
    }

    private void apply(Interview interview, InterviewRequest request) {
        interview.setJobId(request.getJobId());
        interview.setApplicationId(request.getApplicationId());
        interview.setScheduledTime(request.getScheduledTime());
        interview.setLocation(request.getLocation());
        interview.setMeetingLink(request.getMeetingLink());
        interview.setStatus(request.getStatus());
        interview.setFeedback(request.getFeedback());
    }
}
