package com.ryj.demo.mapper;

import com.ryj.demo.entity.ResumeSkill;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ResumeSkillMapper {

    @Select("SELECT resume_id, skill, proficiency FROM resume_skill WHERE resume_id = #{resumeId}")
    List<ResumeSkill> findByResumeId(Long resumeId);

    @Insert("INSERT INTO resume_skill (resume_id, skill, proficiency) VALUES (#{resumeId}, #{skill}, #{proficiency})")
    void insertSkill(ResumeSkill skill);

    @Delete("DELETE FROM resume_skill WHERE resume_id = #{resumeId}")
    void deleteByResumeId(Long resumeId);

    @Select("SELECT COUNT(1) FROM resume_skill WHERE resume_id = #{resumeId}")
    int countByResumeId(Long resumeId);
}
