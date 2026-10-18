package com.smartspend.smartspend.repository;

import com.smartspend.smartspend.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {
    long countByUser_UserId(Long userId); // This will count active goals for the dashboard
}
