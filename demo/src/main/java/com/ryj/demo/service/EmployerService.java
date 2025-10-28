package com.ryj.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ryj.demo.entity.Employer;

import java.util.Optional;

public interface EmployerService extends IService<Employer> {
    Optional<Employer> findByUserId(Long userId);
}
