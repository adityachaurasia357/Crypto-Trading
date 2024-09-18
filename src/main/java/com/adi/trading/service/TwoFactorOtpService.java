package com.adi.trading.service;

import com.adi.trading.model.TwoFactorOtp;
import com.adi.trading.model.User;

public interface TwoFactorOtpService {
    
    TwoFactorOtp createTwoFactorOtp(User user, String otp, String jwt);
    TwoFactorOtp findByUser(Long userId);
    TwoFactorOtp findById(String id);
    boolean verifyTwoFactorOtp(TwoFactorOtp twoFactorOtp, String otp);
    void deleteTwoFactorOtp(TwoFactorOtp twoFactorOtp);
}
