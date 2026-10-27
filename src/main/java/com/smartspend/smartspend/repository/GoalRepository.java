package com.smartspend.smartspend.repository;

import com.smartspend.smartspend.entity.Goal;
import com.smartspend.smartspend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {
    
    List<Goal> findByUser_UserId(Long userId);
    
    long countByUser_UserId(Long userId);

    @Transactional
    void deleteByUser(User user);
}