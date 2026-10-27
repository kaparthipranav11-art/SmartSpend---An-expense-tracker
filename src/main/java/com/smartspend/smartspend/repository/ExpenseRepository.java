package com.smartspend.smartspend.repository;

import com.smartspend.smartspend.entity.Expense;
import com.smartspend.smartspend.entity.User; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user.userId = :userId AND e.category.categoryId = :categoryId")
    java.math.BigDecimal getSpentByCategory(@org.springframework.data.repository.query.Param("userId") Long userId, @org.springframework.data.repository.query.Param("categoryId") Integer categoryId);

    @org.springframework.data.jpa.repository.Query("SELECT e.category.categoryName, SUM(e.amount) FROM Expense e WHERE e.user.userId = :userId GROUP BY e.category.categoryName")
    java.util.List<Object[]> getExpensesByCategory(@org.springframework.data.repository.query.Param("userId") Long userId);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user.userId = :userId")
    java.math.BigDecimal getTotalExpenses(@org.springframework.data.repository.query.Param("userId") Long userId);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user.userId = :userId AND e.expenseDate >= :startDate AND e.expenseDate <= :endDate")
    java.math.BigDecimal getTotalExpensesForMonth(
        @org.springframework.data.repository.query.Param("userId") Long userId, 
        @org.springframework.data.repository.query.Param("startDate") java.time.LocalDate startDate, 
        @org.springframework.data.repository.query.Param("endDate") java.time.LocalDate endDate
    );

    @org.springframework.data.jpa.repository.Query("SELECT e.category.categoryName, SUM(e.amount) FROM Expense e WHERE e.user.userId = :userId AND e.expenseDate >= :startDate AND e.expenseDate <= :endDate GROUP BY e.category.categoryName")
    java.util.List<Object[]> getExpensesByCategoryForMonth(
        @org.springframework.data.repository.query.Param("userId") Long userId, 
        @org.springframework.data.repository.query.Param("startDate") java.time.LocalDate startDate, 
        @org.springframework.data.repository.query.Param("endDate") java.time.LocalDate endDate
    );
    
    List<Expense> findByUser_UserId(Long userId);

    @Transactional
    void deleteByUser(User user);
}