package com.deng.auth_center.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.deng.auth_center.entity.SysRefreshToken;

public interface SysRefreshTokenRepository extends JpaRepository<SysRefreshToken, Long> {
    
}
