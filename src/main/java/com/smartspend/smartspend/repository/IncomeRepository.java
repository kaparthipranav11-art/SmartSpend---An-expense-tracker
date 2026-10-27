package com.smartspend.smartspend.repository;

import com.smartspend.smartspend.entity.Income;
import com.smartspend.smartspend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {
    
    List<Income> findByUser_UserId(Long userId);

    @Transactional
    void deleteByUser(User user);

    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM Income i WHERE i.user.userId = :userId")
    BigDecimal getTotalIncome(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM Income i WHERE i.user.userId = :userId AND i.incomeDate >= :startDate AND i.incomeDate <= :endDate")
    BigDecimal getTotalIncomeForMonth(
        @Param("userId") Long userId, 
        @Param("startDate") LocalDate startDate, 
        @Param("endDate") LocalDate endDate
    );
}