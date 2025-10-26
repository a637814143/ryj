package com.ryj.demo.user.service;

import com.ryj.demo.user.entity.SysUser;
import java.util.Optional;

public interface SysUserService {

    Optional<SysUser> findByUsername(String username);

    SysUser save(SysUser user);
}

