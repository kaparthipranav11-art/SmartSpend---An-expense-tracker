
package com.smartspend.smartspend.repository;

import com.smartspend.smartspend.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import com.smartspend.smartspend.entity.User;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

    @Query("SELECT COALESCE(SUM(b.balance), 0) FROM BankAccount b WHERE b.user.userId = :userId")
    BigDecimal getTotalBalance(@Param("userId") Long userId);

    List<BankAccount> findByUser_UserId(Long userId);

    Optional<BankAccount> findByAccountIdAndUser_UserId(
            Long accountId, Long userId);

            void deleteByUser(User user);
}
