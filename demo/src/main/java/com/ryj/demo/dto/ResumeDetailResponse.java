package com.ryj.demo.dto;

import com.ryj.demo.entity.Resume;
import com.ryj.demo.entity.ResumeExperience;
import com.ryj.demo.entity.ResumeSkill;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ResumeDetailResponse {
    private Resume resume;
    private List<ResumeExperience> experiences;
    private List<ResumeSkill> skills;
}
