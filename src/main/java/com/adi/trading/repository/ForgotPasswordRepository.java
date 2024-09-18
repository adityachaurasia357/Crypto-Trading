package com.adi.trading.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adi.trading.model.ForgotPasswordToken;

public interface ForgotPasswordRepository extends JpaRepository<ForgotPasswordToken, String> {
    ForgotPasswordToken findByUserId(Long userId);
}
