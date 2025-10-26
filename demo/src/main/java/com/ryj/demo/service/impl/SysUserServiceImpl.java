package com.ryj.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ryj.demo.entity.SysUser;
import com.ryj.demo.mapper.SysUserMapper;
import com.ryj.demo.service.SysUserService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    @Override
    public Optional<SysUser> findByUsername(String username) {
        return Optional.ofNullable(getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username)));
    }
}
