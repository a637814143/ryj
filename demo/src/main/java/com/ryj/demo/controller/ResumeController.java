package com.ryj.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ryj.demo.common.ApiResponse;
import com.ryj.demo.dto.ResumeRequest;
import com.ryj.demo.entity.Resume;
import com.ryj.demo.entity.ResumeExperience;
import com.ryj.demo.entity.ResumeSkill;
import com.ryj.demo.service.ResumeExperienceService;
import com.ryj.demo.service.ResumeService;
import com.ryj.demo.service.ResumeSkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;
    private final ResumeExperienceService resumeExperienceService;
    private final ResumeSkillService resumeSkillService;

    @PostMapping
    @Transactional
    public ApiResponse<Map<String, Object>> create(@Valid @RequestBody ResumeRequest request) {
        Resume resume = new Resume();
        resume.setStudentId(request.getStudentId());
        resume.setTitle(request.getTitle());
        resume.setSummary(request.getSummary());
        resume.setPortfolioUrl(request.getPortfolioUrl());
        resumeService.save(resume);
        Long resumeId = resume.getId();
        saveDetails(resumeId, request);
        return getResumeDetailResponse(resumeId);
    }

    @PutMapping("/{id}")
    @Transactional
    public ApiResponse<Map<String, Object>> update(@PathVariable Long id, @Valid @RequestBody ResumeRequest request) {
        Resume resume = resumeService.getById(id);
        if (resume == null) {
            throw new IllegalArgumentException("简历不存在");
        }
        resume.setTitle(request.getTitle());
        resume.setSummary(request.getSummary());
        resume.setPortfolioUrl(request.getPortfolioUrl());
        resumeService.updateById(resume);
        saveDetails(id, request);
        return getResumeDetailResponse(id);
    }

    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> detail(@PathVariable Long id) {
        return getResumeDetailResponse(id);
    }

    @GetMapping("/student/{studentId}")
    public ApiResponse<List<Resume>> listByStudent(@PathVariable Long studentId) {
        List<Resume> resumes = resumeService.list(new LambdaQueryWrapper<Resume>().eq(Resume::getStudentId, studentId));
        return ApiResponse.success(resumes);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        resumeExperienceService.remove(new LambdaQueryWrapper<ResumeExperience>().eq(ResumeExperience::getResumeId, id));
        resumeSkillService.replaceSkills(id, List.of());
        return ApiResponse.success(resumeService.removeById(id));
    }

    private void saveDetails(Long resumeId, ResumeRequest request) {
        resumeExperienceService.remove(new LambdaQueryWrapper<ResumeExperience>().eq(ResumeExperience::getResumeId, resumeId));
        if (request.getExperiences() != null) {
            for (ResumeRequest.ResumeExperienceRequest expRequest : request.getExperiences()) {
                ResumeExperience experience = new ResumeExperience();
                experience.setResumeId(resumeId);
                experience.setTitle(expRequest.getTitle());
                experience.setOrganization(expRequest.getOrganization());
                experience.setStartDate(expRequest.getStartDate());
                experience.setEndDate(expRequest.getEndDate());
                experience.setDescription(expRequest.getDescription());
                resumeExperienceService.save(experience);
            }
        }
        if (request.getSkills() != null) {
            List<ResumeSkill> skills = request.getSkills().stream().map(skillRequest -> {
                ResumeSkill skill = new ResumeSkill();
                skill.setSkill(skillRequest.getSkill());
                skill.setProficiency(skillRequest.getProficiency());
                return skill;
            }).toList();
            resumeSkillService.replaceSkills(resumeId, skills);
        } else {
            resumeSkillService.replaceSkills(resumeId, List.of());
        }
    }

    private ApiResponse<Map<String, Object>> getResumeDetailResponse(Long resumeId) {
        Resume resume = resumeService.getById(resumeId);
        if (resume == null) {
            throw new IllegalArgumentException("简历不存在");
        }
        Map<String, Object> result = new HashMap<>();
        result.put("resume", resume);
        result.put("experiences", resumeExperienceService.list(new LambdaQueryWrapper<ResumeExperience>().eq(ResumeExperience::getResumeId, resumeId)));
        result.put("skills", resumeSkillService.findByResumeId(resumeId));
        return ApiResponse.success(result);
    }
}
