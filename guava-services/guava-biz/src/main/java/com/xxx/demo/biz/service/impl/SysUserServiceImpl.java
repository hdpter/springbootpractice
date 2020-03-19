package com.xxx.demo.biz.service.impl;

import com.xxx.demo.biz.configurer.core.AbstractService;
import com.xxx.demo.biz.service.SysUserService;
import com.xxx.demo.dal.mapper.SysUserMapper;
import com.xxx.demo.dal.model.SysUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Transactional
public class SysUserServiceImpl extends AbstractService<SysUser> implements SysUserService {
    @Resource
    private SysUserMapper sysUserMapper;

}
