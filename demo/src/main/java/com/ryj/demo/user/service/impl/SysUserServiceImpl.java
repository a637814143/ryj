package com.ryj.demo.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ryj.demo.user.entity.SysUser;
import com.ryj.demo.user.mapper.SysUserMapper;
import com.ryj.demo.user.service.SysUserService;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl implements SysUserService {

    private final SysUserMapper sysUserMapper;

    public SysUserServiceImpl(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    public Optional<SysUser> findByUsername(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username).last("limit 1");
        SysUser sysUser = sysUserMapper.selectOne(wrapper);
        return Optional.ofNullable(sysUser);
    }

    @Override
    public SysUser save(SysUser user) {
        if (user.getId() == null) {
            sysUserMapper.insert(user);
        } else {
            sysUserMapper.updateById(user);
        }
        return user;
    }
}

