package com.deng.auth_center.service.itf;

import com.deng.auth_center.entity.SysUser;

public interface SysUserService {
    public SysUser findSysUserById(Long id);
    public Boolean isExistById(Long id);
    public SysUser findSysUserByUsername(String username);
}
