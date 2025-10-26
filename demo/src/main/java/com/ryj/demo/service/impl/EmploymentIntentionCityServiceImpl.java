package com.ryj.demo.service.impl;

import com.ryj.demo.entity.EmploymentIntentionCity;
import com.ryj.demo.mapper.EmploymentIntentionCityMapper;
import com.ryj.demo.service.EmploymentIntentionCityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmploymentIntentionCityServiceImpl implements EmploymentIntentionCityService {

    private final EmploymentIntentionCityMapper cityMapper;

    @Override
    public List<EmploymentIntentionCity> findByIntentionId(Long intentionId) {
        return cityMapper.findByIntentionId(intentionId);
    }

    @Override
    @Transactional
    public void replaceCities(Long intentionId, List<String> cities) {
        cityMapper.deleteByIntentionId(intentionId);
        if (cities == null || cities.isEmpty()) {
            return;
        }
        for (String city : cities) {
            cityMapper.insertCity(intentionId, city);
        }
    }
}
