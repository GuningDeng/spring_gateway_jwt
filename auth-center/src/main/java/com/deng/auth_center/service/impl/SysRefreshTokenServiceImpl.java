package com.deng.auth_center.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.deng.auth_center.entity.SysRefreshToken;
import com.deng.auth_center.repository.SysRefreshTokenRepository;
import com.deng.auth_center.service.itf.SysRefreshTokenService;

import lombok.extern.slf4j.Slf4j;

@Slf4j 
@Service 
public class SysRefreshTokenServiceImpl implements SysRefreshTokenService {
    private final SysRefreshTokenRepository sysRefreshTokenRepository;

    public SysRefreshTokenServiceImpl(SysRefreshTokenRepository sysRefreshTokenRepository) {
        this.sysRefreshTokenRepository = sysRefreshTokenRepository;
    }

    @Override
    public SysRefreshToken saveSysRefreshToken(SysRefreshToken token) {
        log.info("Save sysRefreshToken");
        if (token == null) {
            log.error("RefreshToken is empty");
            return null;
        }
        try {
            return sysRefreshTokenRepository.save(token);
        } catch (Exception e) {
            log.error("Error saving refreshToken.", e.getMessage());
            return null;
        }
    }

    @Override
    public Boolean existsByJti(String jti) {
        log.info("Check if refresh token exists - jti: {}", jti);
        if (jti == null || jti.isEmpty()) {
            log.error("Invalid refresh token: {}", jti);
            return false;
        }
        try {
            Optional<SysRefreshToken> token = sysRefreshTokenRepository.findAll().stream()
                .filter(sys -> sys.getJti().equals(jti))
                .findFirst();
            
            return token.isPresent();

        } catch (Exception e) {
            log.error("Error checking refresh token: {}", jti, e.getMessage());
            return false;
        }
    }

    @Override
    public void updateStatus(String jti, Byte status) {
        log.info("Update sysRefreshToken");
        if (jti == null || jti.isEmpty()) {
            log.error("Invalid refresh token - jti: {}", jti);
            
        }
        if (status == null || status != 0 || status != 1) {
            log.error("Invalid refresh token - status: {}", status);
            
        }

        SysRefreshToken existedToken = sysRefreshTokenRepository.findAll().stream()
            .filter(sys -> sys.getJti().equals(jti))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("SysRefreschToken does not found"));
        
        SysRefreshToken updatedToken = new SysRefreshToken();
        updatedToken.setId(existedToken.getId());
        updatedToken.setUserId(existedToken.getUserId());
        updatedToken.setJti(existedToken.getJti());
        updatedToken.setStatus(status);
        updatedToken.setIssuedAt(existedToken.getIssuedAt());
        updatedToken.setExpiresAt(existedToken.getExpiresAt());
        updatedToken.setRevokedAt(existedToken.getRevokedAt());
        updatedToken.setClientIp(existedToken.getClientIp());
        updatedToken.setUserAgent(existedToken.getUserAgent());

        sysRefreshTokenRepository.save(updatedToken);
    }

    @Override
    public SysRefreshToken findSysRefreshTokenByJti(String jti) {
        log.info("Fetching sysRefreshToken - jti: {}", jti);
        if (jti == null || jti.isEmpty()) {
            log.error("Invalid refresh token - jti: {}", jti);
            return null;
        }

        try {
            if (!existsByJti(jti)) {
                log.error("Refresh token does not exists- jti: {}", jti);
                return null;
            }
            SysRefreshToken sysRefreshToken = sysRefreshTokenRepository.findAll().stream()
                .filter(sys -> sys.getJti().equals(jti))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Refresch token not found"));

            return sysRefreshToken;
            
        } catch (Exception e) {
            log.error("Error feching refresh token - jti: {}", jti, e.getMessage());
            return null;
        }
    }

    
}
