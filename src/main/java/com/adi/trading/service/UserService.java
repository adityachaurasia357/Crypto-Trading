package com.adi.trading.service;

import com.adi.trading.domain.VerificationType;
import com.adi.trading.model.User;

public interface UserService {
    
    public User findUserByJwt(String jwt) throws Exception;
    public User findUserByEmail(String email) throws Exception;
    public User findUserById(Long userId) throws Exception;
    public User enableTwoFactorAuthentication(VerificationType verificationType, String sentTo, User user);
    User updatePasswrd(User user, String newPassword);
}
