package com.smartspend.smartspend.repository;

import com.smartspend.smartspend.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(b.balance), 0) FROM BankAccount b WHERE b.user.userId = :userId")
    java.math.BigDecimal getTotalBalance(@org.springframework.data.repository.query.Param("userId") Long userId);
    
    List<BankAccount> findByUser_UserId(Long userId);
}
