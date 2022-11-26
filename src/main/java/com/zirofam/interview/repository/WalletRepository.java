package com.zirofam.interview.repository;

import com.zirofam.interview.domain.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface WalletRepository extends JpaRepository<WalletEntity, String> {

    @Modifying(clearAutomatically = true)
    @Query("update WalletEntity w set w.balance = w.balance + :amount where w.user =:user")
    void incrementWallet(@Param("user") String user, @Param("amount") BigDecimal amount);

    @Modifying(clearAutomatically = true)
    @Query("update WalletEntity w set w.balance = w.balance - :amount where w.user =:user")
    void decrementWallet(@Param("user") String user, @Param("amount") BigDecimal amount);

    WalletEntity findByUser(String user);
}
