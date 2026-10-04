package com.deng.auth_center.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.deng.auth_center.entity.SysRole;

public interface SysRoleRepository extends JpaRepository<SysRole, Long> {
    
}
