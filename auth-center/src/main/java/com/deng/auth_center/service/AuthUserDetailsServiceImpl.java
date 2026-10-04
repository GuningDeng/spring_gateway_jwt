package com.deng.auth_center.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.deng.auth_center.entity.SysRole;
import com.deng.auth_center.entity.SysUser;
import com.deng.auth_center.repository.SysUserRepository;

@Service 
public class AuthUserDetailsServiceImpl implements UserDetailsService {
    private final SysUserRepository sysUserRepository;

    public AuthUserDetailsServiceImpl(SysUserRepository sysUserRepository) {
        this.sysUserRepository = sysUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = sysUserRepository.findAll().stream()
            .filter(s -> s.getUsername().equals(username))
            .findFirst().orElseThrow(() -> new RuntimeException("Sysuser with not found"));

        if (sysUser == null) {
            throw new UsernameNotFoundException("SysUser is not existed: " + username);
        }
        if (sysUser.getStatus() != null && sysUser.getStatus() == 1) {
            throw new UsernameNotFoundException("SysUser is inactive: " + username);
        }
        List<GrantedAuthority> authorities = (sysUser.getRoles() == null ? List.<SysRole>of() : sysUser.getRoles())
            .stream()
            .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getCode()))
            .collect(Collectors.toList());
        
        return new User(sysUser.getUsername(), sysUser.getPassword(), authorities);
    }

    
    
}
