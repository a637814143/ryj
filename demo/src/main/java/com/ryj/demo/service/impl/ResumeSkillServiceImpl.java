package com.ryj.demo.service.impl;

import com.ryj.demo.entity.ResumeSkill;
import com.ryj.demo.mapper.ResumeSkillMapper;
import com.ryj.demo.service.ResumeSkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResumeSkillServiceImpl implements ResumeSkillService {

    private final ResumeSkillMapper resumeSkillMapper;

    @Override
    public List<ResumeSkill> findByResumeId(Long resumeId) {
        return resumeSkillMapper.findByResumeId(resumeId);
    }

    @Override
    @Transactional
    public void replaceSkills(Long resumeId, List<ResumeSkill> skills) {
        resumeSkillMapper.deleteByResumeId(resumeId);
        if (skills == null || skills.isEmpty()) {
            return;
        }
        for (ResumeSkill skill : skills) {
            skill.setResumeId(resumeId);
            resumeSkillMapper.insertSkill(skill);
        }
    }

    @Override
    public int countByResumeId(Long resumeId) {
        return resumeSkillMapper.countByResumeId(resumeId);
    }
}
