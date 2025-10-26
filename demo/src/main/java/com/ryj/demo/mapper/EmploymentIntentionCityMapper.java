package com.ryj.demo.mapper;

import com.ryj.demo.entity.EmploymentIntentionCity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface EmploymentIntentionCityMapper {

    @Select("SELECT intention_id, city FROM employment_intention_city WHERE intention_id = #{intentionId}")
    List<EmploymentIntentionCity> findByIntentionId(Long intentionId);

    @Insert("INSERT INTO employment_intention_city (intention_id, city) VALUES (#{intentionId}, #{city})")
    void insertCity(@Param("intentionId") Long intentionId, @Param("city") String city);

    @Delete("DELETE FROM employment_intention_city WHERE intention_id = #{intentionId}")
    void deleteByIntentionId(Long intentionId);
}
