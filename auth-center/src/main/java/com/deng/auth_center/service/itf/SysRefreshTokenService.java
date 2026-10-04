package com.deng.auth_center.service.itf;

import com.deng.auth_center.entity.SysRefreshToken;

public interface SysRefreshTokenService {
    public SysRefreshToken saveSysRefreshToken(SysRefreshToken token);
    public Boolean existsByJti(String jti);
    public void updateStatus(String jti, Byte status);
    public SysRefreshToken findSysRefreshTokenByJti(String jti);
}
