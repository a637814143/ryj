package com.ryj.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ryj.demo.entity.Employer;
import com.ryj.demo.mapper.EmployerMapper;
import com.ryj.demo.service.EmployerService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmployerServiceImpl extends ServiceImpl<EmployerMapper, Employer> implements EmployerService {

    @Override
    public Optional<Employer> findByUserId(Long userId) {
        if (userId == null) {
            return Optional.empty();
        }
        Employer employer = getOne(new LambdaQueryWrapper<Employer>()
                .eq(Employer::getUserId, userId));
        return Optional.ofNullable(employer);
    }
}
