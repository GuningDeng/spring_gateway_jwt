package com.deng.auth_center.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity 
@Table(name = "sys_refresh_token") 
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
public class SysRefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sys_refresh_token_id")
    private Long id;

    @Column(name = "sys_refresh_token_user_id")
    private Long userId;
    @Column(name = "sys_refresh_token_jti")
    private String jti;
    @Column(name = "sys_refresh_token_status")
    private Byte status; // 0: active, 1: inactive
    @Column(name = "sys_refresh_token_issued_at")
    private Date issuedAt;
    @Column(name = "sys_refresh_token_expires_at")
    private Date expiresAt;
    @Column(name = "sys_refresh_token_revoked_at")
    private Date revokedAt;
    @Column(name = "sys_refresh_token_client_ip")
    private String clientIp;
    @Column(name = "sys_refresh_token_user_agent")
    private String userAgent;
    
}
