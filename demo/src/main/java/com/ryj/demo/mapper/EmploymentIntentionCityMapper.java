package com.ryj.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ryj.demo.entity.EmploymentIntentionCity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EmploymentIntentionCityMapper extends BaseMapper<EmploymentIntentionCity> {
    
    @Select("SELECT * FROM employment_intention_city WHERE intention_id = #{intentionId}")
    List<EmploymentIntentionCity> findByIntentionId(@Param("intentionId") Long intentionId);
    
    @Insert("INSERT INTO employment_intention_city (intention_id, city) VALUES (#{intentionId}, #{city})")
    int insertCity(@Param("intentionId") Long intentionId, @Param("city") String city);
    
    @Delete("DELETE FROM employment_intention_city WHERE intention_id = #{intentionId}")
    int deleteByIntentionId(@Param("intentionId") Long intentionId);
}
