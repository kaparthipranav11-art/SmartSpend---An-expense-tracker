package com.smartspend.smartspend.service;

import com.smartspend.smartspend.entity.User;
import com.smartspend.smartspend.repository.ExpenseRepository;
import com.smartspend.smartspend.repository.GoalRepository;
import com.smartspend.smartspend.repository.IncomeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private GoalRepository goalRepository;

    @Transactional
    public void resetUserData(User user) {
        // Delete all expenses
        expenseRepository.deleteByUser(user);

        // Delete all incomes
        incomeRepository.deleteByUser(user);

        // Delete all goals
        goalRepository.deleteByUser(user);
    }
}