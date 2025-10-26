package com.ryj.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ryj.demo.entity.SystemNotification;
import com.ryj.demo.mapper.SystemNotificationMapper;
import com.ryj.demo.service.SystemNotificationService;
import org.springframework.stereotype.Service;

@Service
public class SystemNotificationServiceImpl extends ServiceImpl<SystemNotificationMapper, SystemNotification> implements SystemNotificationService {
}
