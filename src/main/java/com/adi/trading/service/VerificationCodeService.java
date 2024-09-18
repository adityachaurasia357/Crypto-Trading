package com.adi.trading.service;

import com.adi.trading.domain.VerificationType;
import com.adi.trading.model.User;
import com.adi.trading.model.VerificationCode;

public interface VerificationCodeService {
    VerificationCode sendVerificationCode(User user, VerificationType verificationType);
    VerificationCode getVerificationCodeById(Long id) throws Exception;
    VerificationCode getVerificationCodeByUser(Long userId);
    void deleteVerificationCode(VerificationCode verificationCode);
}
