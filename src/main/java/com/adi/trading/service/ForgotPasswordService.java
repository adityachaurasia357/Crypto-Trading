package com.adi.trading.service;

import com.adi.trading.domain.VerificationType;
import com.adi.trading.model.ForgotPasswordToken;
import com.adi.trading.model.User;

public interface ForgotPasswordService {
    
    ForgotPasswordToken createToken(User user, String id, String otp, VerificationType verificationType, String sendTo);
    ForgotPasswordToken findById(String id);
    ForgotPasswordToken findByUser(Long userId);
    void deleteToken(ForgotPasswordToken token);
}
