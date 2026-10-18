package com.smartspend.smartspend.repository;

import com.smartspend.smartspend.entity.Expense;
import com.smartspend.smartspend.entity.User; // Fixes the 'User' error
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Modifying

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user.userId = :userId AND e.category.categoryId = :categoryId")
java.math.BigDecimal getSpentByCategory(@org.springframework.data.repository.query.Param("userId") Long userId, @org.springframework.data.repository.query.Param("categoryId") Integer categoryId);

    @org.springframework.data.jpa.repository.Query("SELECT e.category.categoryName, SUM(e.amount) FROM Expense e WHERE e.user.userId = :userId GROUP BY e.category.categoryName")
    java.util.List<Object[]> getExpensesByCategory(@org.springframework.data.repository.query.Param("userId") Long userId);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user.userId = :userId")
    java.math.BigDecimal getTotalExpenses(@org.springframework.data.repository.query.Param("userId") Long userId);
    List<Expense> findByUser_UserId(Long userId);

    @Transactional
    void deleteByUser(User user);
}