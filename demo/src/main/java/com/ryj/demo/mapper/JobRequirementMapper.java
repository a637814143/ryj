package com.ryj.demo.mapper;

import com.ryj.demo.entity.JobRequirement;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface JobRequirementMapper {

    @Select("SELECT job_id, requirement FROM job_requirement WHERE job_id = #{jobId}")
    List<JobRequirement> findByJobId(Long jobId);

    @Insert("INSERT INTO job_requirement (job_id, requirement) VALUES (#{jobId}, #{requirement})")
    void insertRequirement(JobRequirement requirement);

    @Delete("DELETE FROM job_requirement WHERE job_id = #{jobId}")
    void deleteByJobId(Long jobId);
}
