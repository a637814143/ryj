package com.ryj.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ryj.demo.common.ApiResponse;
import com.ryj.demo.dto.StudentProfileReviewDecision;
import com.ryj.demo.entity.StudentProfile;
import com.ryj.demo.entity.StudentProfileUpdateRequest;
import com.ryj.demo.service.StudentProfileService;
import com.ryj.demo.service.StudentProfileUpdateRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/admin/student-profiles")
@RequiredArgsConstructor
public class StudentProfileApprovalController {

    private final StudentProfileService studentProfileService;
    private final StudentProfileUpdateRequestService updateRequestService;

    @GetMapping("/requests")
    public ApiResponse<List<StudentProfileUpdateRequest>> listRequests(@RequestParam(value = "status", required = false) String status) {
        LambdaQueryWrapper<StudentProfileUpdateRequest> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isBlank()) {
            wrapper.eq(StudentProfileUpdateRequest::getStatus, status.toUpperCase());
        }
        wrapper.orderByDesc(StudentProfileUpdateRequest::getCreatedAt);
        return ApiResponse.success(updateRequestService.list(wrapper));
    }

    @GetMapping("/requests/{id}")
    public ApiResponse<StudentProfileUpdateRequest> getRequest(@PathVariable Long id) {
        StudentProfileUpdateRequest request = updateRequestService.getById(id);
        if (request == null) {
            return ApiResponse.failure(404, "未找到档案更新申请");
        }
        return ApiResponse.success(request);
    }

    @PutMapping("/requests/{id}/approve")
    public ApiResponse<Boolean> approve(@PathVariable Long id, @RequestBody(required = false) StudentProfileReviewDecision decision) {
        StudentProfileUpdateRequest request = updateRequestService.getById(id);
        if (request == null) {
            return ApiResponse.failure(404, "未找到档案更新申请");
        }
        if (!Objects.equals("PENDING", request.getStatus())) {
            return ApiResponse.failure(400, "仅能审核待处理的申请");
        }

        StudentProfile profile = studentProfileService.getById(request.getStudentId());
        if (profile == null) {
            profile = new StudentProfile();
            profile.setId(request.getStudentId());
        }
        profile.setGender(request.getGender());
        profile.setAge(request.getAge());
        profile.setMajor(request.getMajor());
        profile.setBiography(request.getBiography());
        profile.setGraduationYear(request.getGraduationYear());
        studentProfileService.saveOrUpdate(profile);

        applyDecision(request, decision);
        request.setStatus("APPROVED");
        request.setReviewedAt(LocalDateTime.now());

        return ApiResponse.success(updateRequestService.updateById(request));
    }

    @PutMapping("/requests/{id}/reject")
    public ApiResponse<Boolean> reject(@PathVariable Long id, @RequestBody StudentProfileReviewDecision decision) {
        StudentProfileUpdateRequest request = updateRequestService.getById(id);
        if (request == null) {
            return ApiResponse.failure(404, "未找到档案更新申请");
        }
        if (!Objects.equals("PENDING", request.getStatus())) {
            return ApiResponse.failure(400, "仅能审核待处理的申请");
        }

        applyDecision(request, decision);
        request.setStatus("REJECTED");
        request.setReviewedAt(LocalDateTime.now());

        return ApiResponse.success(updateRequestService.updateById(request));
    }

    @DeleteMapping("/requests/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.success(updateRequestService.removeById(id));
    }

    private void applyDecision(StudentProfileUpdateRequest request, StudentProfileReviewDecision decision) {
        if (decision == null) {
            return;
        }
        request.setReviewerId(decision.getReviewerId());
        request.setReviewComment(decision.getReviewComment());
    }
}
