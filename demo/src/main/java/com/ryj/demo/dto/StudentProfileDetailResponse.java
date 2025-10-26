package com.ryj.demo.dto;

import com.ryj.demo.entity.StudentProfile;
import com.ryj.demo.entity.StudentProfileUpdateRequest;
import lombok.Data;

import java.util.List;

@Data
public class StudentProfileDetailResponse {
    private StudentProfile profile;
    private StudentProfileUpdateRequest pendingRequest;
    private List<StudentProfileUpdateRequest> history;
}
