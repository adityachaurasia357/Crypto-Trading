package com.adi.trading.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adi.trading.model.User;

public interface UserRepository extends JpaRepository<User,Long> {

    User findByEmail(String email);
    
}
