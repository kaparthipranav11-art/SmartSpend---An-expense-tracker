package com.smartspend.smartspend.repository;

import com.smartspend.smartspend.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import com.smartspend.smartspend.entity.User;


@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByUser_UserId(Long userId);

    void deleteByUser(User user);

}
