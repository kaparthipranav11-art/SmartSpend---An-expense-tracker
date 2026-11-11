
package com.smartspend.smartspend.service;

import com.smartspend.smartspend.entity.User;
import com.smartspend.smartspend.repository.ExpenseRepository;
import com.smartspend.smartspend.repository.GoalRepository;
import com.smartspend.smartspend.repository.IncomeRepository;
import com.smartspend.smartspend.repository.BudgetRepository;
import com.smartspend.smartspend.repository.BankAccountRepository;

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

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Transactional
    public void resetUserData(User user) {

        // Delete expenses and incomes first
        expenseRepository.deleteByUser(user);
        incomeRepository.deleteByUser(user);

        // Delete budgets and goals
        budgetRepository.deleteByUser(user);
        goalRepository.deleteByUser(user);

        // Delete bank accounts after dependent records
        bankAccountRepository.deleteByUser(user);
    }
}
