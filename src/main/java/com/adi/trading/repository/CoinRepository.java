package com.adi.trading.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adi.trading.model.Coin;

public interface CoinRepository extends JpaRepository<Coin,String> {
    
}
