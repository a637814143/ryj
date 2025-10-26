package com.ryj.demo.controller;

import com.ryj.demo.common.ApiResponse;
import com.ryj.demo.dto.StudentProfileDetailResponse;
import com.ryj.demo.dto.StudentProfileRequest;
import com.ryj.demo.entity.StudentProfile;
import com.ryj.demo.entity.StudentProfileUpdateRequest;
import com.ryj.demo.service.StudentProfileService;
import com.ryj.demo.service.StudentProfileUpdateRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentProfileController {

    private final StudentProfileService studentProfileService;
    private final StudentProfileUpdateRequestService updateRequestService;

    @GetMapping("/profiles/{studentId}")
    public ApiResponse<StudentProfileDetailResponse> getProfile(@PathVariable Long studentId) {
        StudentProfile profile = studentProfileService.getById(studentId);

        List<StudentProfileUpdateRequest> requests = updateRequestService.list(
                new LambdaQueryWrapper<StudentProfileUpdateRequest>()
                        .eq(StudentProfileUpdateRequest::getStudentId, studentId)
                        .orderByDesc(StudentProfileUpdateRequest::getCreatedAt)
        );

        StudentProfileDetailResponse response = new StudentProfileDetailResponse();
        response.setProfile(profile);
        response.setHistory(requests);
        response.setPendingRequest(requests.stream()
                .filter(req -> Objects.equals("PENDING", req.getStatus()))
                .findFirst()
                .orElse(null));
        return ApiResponse.success(response);
    }

    @PostMapping("/profiles/requests")
    public ApiResponse<StudentProfileUpdateRequest> submitProfileUpdate(@Valid @RequestBody StudentProfileRequest request) {
        StudentProfileUpdateRequest existingPending = updateRequestService.getOne(
                new LambdaQueryWrapper<StudentProfileUpdateRequest>()
                        .eq(StudentProfileUpdateRequest::getStudentId, request.getId())
                        .eq(StudentProfileUpdateRequest::getStatus, "PENDING")
        );
        if (existingPending != null) {
            return ApiResponse.failure(400, "已有待审核的档案更新申请，请先等待审核或修改现有申请");
        }

        StudentProfileUpdateRequest updateRequest = mapToRequestEntity(request);
        updateRequest.setStatus("PENDING");
        updateRequestService.save(updateRequest);
        return ApiResponse.success("档案更新申请已提交，等待管理员审核", updateRequest);
    }

    @PutMapping("/profiles/requests/{requestId}")
    public ApiResponse<Boolean> updatePendingRequest(@PathVariable Long requestId,
                                                     @Valid @RequestBody StudentProfileRequest request) {
        StudentProfileUpdateRequest existing = updateRequestService.getById(requestId);
        if (existing == null) {
            return ApiResponse.failure(404, "未找到档案更新申请");
        }
        if (!Objects.equals(existing.getStudentId(), request.getId())) {
            return ApiResponse.failure(400, "学生ID与申请记录不匹配");
        }
        if (!Objects.equals("PENDING", existing.getStatus())) {
            return ApiResponse.failure(400, "仅能修改待审核的档案更新申请");
        }

        applyRequestChanges(existing, request);
        return ApiResponse.success(updateRequestService.updateById(existing));
    }

    @DeleteMapping("/profiles/requests/{requestId}")
    public ApiResponse<Boolean> deletePendingRequest(@PathVariable Long requestId,
                                                     @RequestParam("studentId") Long studentId) {
        StudentProfileUpdateRequest existing = updateRequestService.getById(requestId);
        if (existing == null) {
            return ApiResponse.failure(404, "未找到档案更新申请");
        }
        if (!Objects.equals(existing.getStudentId(), studentId)) {
            return ApiResponse.failure(400, "学生ID与申请记录不匹配");
        }
        if (!Objects.equals("PENDING", existing.getStatus())) {
            return ApiResponse.failure(400, "仅能撤回待审核的档案更新申请");
        }
        return ApiResponse.success(updateRequestService.removeById(requestId));
    }

    private StudentProfileUpdateRequest mapToRequestEntity(StudentProfileRequest request) {
        StudentProfileUpdateRequest updateRequest = new StudentProfileUpdateRequest();
        updateRequest.setStudentId(request.getId());
        applyRequestChanges(updateRequest, request);
        return updateRequest;
    }

    private void applyRequestChanges(StudentProfileUpdateRequest target, StudentProfileRequest source) {
        target.setGender(source.getGender());
        target.setAge(source.getAge());
        target.setMajor(source.getMajor());
        target.setBiography(source.getBiography());
        target.setGraduationYear(source.getGraduationYear());
    }
}
