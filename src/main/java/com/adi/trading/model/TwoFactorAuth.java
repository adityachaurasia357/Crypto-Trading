package com.adi.trading.model;

import com.adi.trading.domain.VerificationType;

import lombok.Data;

@Data
public class TwoFactorAuth {

    private boolean isEnabled=false;

    private VerificationType sendTo;
}
