package com.zirofam.interview.repository;

import com.zirofam.interview.domain.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface WalletRepository extends JpaRepository<WalletEntity, String> {

    @Modifying
    @Query("update WalletEntity w set w.balance = w.balance + :amount where w.user = :user")
    void incrementWallet(String user, BigDecimal amount);

    @Modifying
    @Query("update WalletEntity w set w.balance = w.balance - :amount where w.user = :user")
    void decrementWallet(String user, BigDecimal amount);

    WalletEntity findByUser(String user);
}
