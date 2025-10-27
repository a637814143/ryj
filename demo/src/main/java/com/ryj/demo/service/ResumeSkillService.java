package com.ryj.demo.service;

import com.ryj.demo.entity.ResumeSkill;

import java.util.List;

public interface ResumeSkillService {
    List<ResumeSkill> findByResumeId(Long resumeId);

    void replaceSkills(Long resumeId, List<ResumeSkill> skills);

    int countByResumeId(Long resumeId);
}
