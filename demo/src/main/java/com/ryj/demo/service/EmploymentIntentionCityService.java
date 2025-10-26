package com.ryj.demo.service;

import com.ryj.demo.entity.EmploymentIntentionCity;

import java.util.List;

public interface EmploymentIntentionCityService {
    List<EmploymentIntentionCity> findByIntentionId(Long intentionId);

    void replaceCities(Long intentionId, List<String> cities);
}
