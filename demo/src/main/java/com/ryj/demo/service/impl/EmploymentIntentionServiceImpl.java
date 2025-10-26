package com.ryj.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ryj.demo.dto.EmploymentIntentionRequest;
import com.ryj.demo.dto.EmploymentIntentionResponse;
import com.ryj.demo.entity.EmploymentIntention;
import com.ryj.demo.entity.EmploymentIntentionCity;
import com.ryj.demo.mapper.EmploymentIntentionCityMapper;
import com.ryj.demo.mapper.EmploymentIntentionMapper;
import com.ryj.demo.service.EmploymentIntentionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmploymentIntentionServiceImpl extends ServiceImpl<EmploymentIntentionMapper, EmploymentIntention>
        implements EmploymentIntentionService {

    private final EmploymentIntentionCityMapper cityMapper;

    public EmploymentIntentionServiceImpl(EmploymentIntentionCityMapper cityMapper) {
        this.cityMapper = cityMapper;
    }

    @Override
    @Transactional
    public EmploymentIntentionResponse saveOrUpdateIntention(Long studentId, EmploymentIntentionRequest request) {
        // 查找现有的就业意向
        LambdaQueryWrapper<EmploymentIntention> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmploymentIntention::getStudentId, studentId);
        EmploymentIntention existing = getOne(wrapper);

        EmploymentIntention intention;
        if (existing != null) {
            // 更新现有记录
            intention = existing;
            intention.setExpectedPosition(request.getExpectedPosition());
            intention.setSalaryRange(request.getSalaryRange());
            intention.setWorkType(request.getWorkType());
            intention.setNotes(request.getNotes());
            updateById(intention);
            
            // 删除旧的城市记录
            cityMapper.deleteByIntentionId(intention.getId());
        } else {
            // 创建新记录
            intention = new EmploymentIntention();
            intention.setStudentId(studentId);
            intention.setExpectedPosition(request.getExpectedPosition());
            intention.setSalaryRange(request.getSalaryRange());
            intention.setWorkType(request.getWorkType());
            intention.setNotes(request.getNotes());
            save(intention);
        }

        // 保存城市列表
        if (request.getCities() != null && !request.getCities().isEmpty()) {
            for (String city : request.getCities()) {
                EmploymentIntentionCity intentionCity = new EmploymentIntentionCity();
                intentionCity.setIntentionId(intention.getId());
                intentionCity.setCity(city);
                cityMapper.insert(intentionCity);
            }
        }

        return EmploymentIntentionResponse.from(intention, request.getCities());
    }

    @Override
    public EmploymentIntentionResponse getIntentionByStudentId(Long studentId) {
        // 查找就业意向
        LambdaQueryWrapper<EmploymentIntention> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmploymentIntention::getStudentId, studentId);
        EmploymentIntention intention = getOne(wrapper);

        if (intention == null) {
            return null;
        }

        // 查找意向城市
        LambdaQueryWrapper<EmploymentIntentionCity> cityWrapper = new LambdaQueryWrapper<>();
        cityWrapper.eq(EmploymentIntentionCity::getIntentionId, intention.getId());
        List<EmploymentIntentionCity> intentionCities = cityMapper.selectList(cityWrapper);
        
        List<String> cities = intentionCities.stream()
                .map(EmploymentIntentionCity::getCity)
                .collect(Collectors.toList());

        return EmploymentIntentionResponse.from(intention, cities);
    }

    @Override
    @Transactional
    public boolean deleteIntentionByStudentId(Long studentId) {
        // 查找就业意向
        LambdaQueryWrapper<EmploymentIntention> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmploymentIntention::getStudentId, studentId);
        EmploymentIntention intention = getOne(wrapper);

        if (intention == null) {
            return false;
        }

        // 删除城市记录
        cityMapper.deleteByIntentionId(intention.getId());
        
        // 删除就业意向
        return removeById(intention.getId());
    }
}
