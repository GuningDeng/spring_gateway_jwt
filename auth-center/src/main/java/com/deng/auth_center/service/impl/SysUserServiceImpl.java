package com.deng.auth_center.service.impl;

import org.springframework.stereotype.Service;

import com.deng.auth_center.entity.SysUser;
import com.deng.auth_center.repository.SysUserRepository;
import com.deng.auth_center.service.itf.SysUserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j 
@Service 
public class SysUserServiceImpl implements SysUserService {
    private final SysUserRepository sysUserRepository;

    public SysUserServiceImpl(SysUserRepository sysUserRepository) {
        this.sysUserRepository = sysUserRepository;
    }

    @Override
    public SysUser findSysUserById(Long id) {
        log.info("Fetch sysUser by ID: {}", id);
        if (id == null || id < 1) {
            log.error("Invalid sysUser ID: {}", id);
            return null;
        }
        try {
            Boolean exists = sysUserRepository.existsById(id);
            if (!exists) {
                log.error("SysUser not found with ID: {}", id);
                return null;
            }
            SysUser sysUser = sysUserRepository.findById(id).orElseThrow(() -> new RuntimeException("SysUser not found."));
            return sysUser;
        } catch (Exception e) {
            log.error("SysUser not found with ID: {}", id);
            return null;
        }
    }

    @Override
    public Boolean isExistById(Long id) {
        log.info("Exist sysUser by ID: {}", id);
        if (id == null || id < 1) {
            log.error("Invalid sysUser ID: {}", id);
            return false;
        }
        try {
            return sysUserRepository.existsById(id);
        } catch (Exception e) {
            log.error("SysUser not found with ID: {}", id);
            return false;
        }
    }

    @Override
    public SysUser findSysUserByUsername(String username) {
        log.info("Fetch sysUser by username: {}", username);
        if (username == null || username.isEmpty()) {
            log.error("Invalid username: {}", username);
            return null;
        }
        try {
            SysUser sysUser = sysUserRepository.findAll().stream()
                .filter(sys -> sys.getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("SysUser not found - uwsername: " + username));
            
            return sysUser;

        } catch (Exception e) {
            log.error("Error fechting sysUser - username: {}", username);
            return null;
        }
    }
}
