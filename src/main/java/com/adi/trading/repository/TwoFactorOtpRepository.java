package com.adi.trading.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adi.trading.model.TwoFactorOtp;

public interface TwoFactorOtpRepository extends JpaRepository<TwoFactorOtp,String>{
    TwoFactorOtp findByUserId(Long userId);
}
