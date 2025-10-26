package com.ryj.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ryj.demo.entity.SysUser;

import java.util.Optional;

public interface SysUserService extends IService<SysUser> {
    Optional<SysUser> findByUsername(String username);
}
