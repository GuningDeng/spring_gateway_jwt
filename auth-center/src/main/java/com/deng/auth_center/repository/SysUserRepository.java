package com.deng.auth_center.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.deng.auth_center.entity.SysUser;

public interface SysUserRepository extends JpaRepository<SysUser, Long> {
    
}
