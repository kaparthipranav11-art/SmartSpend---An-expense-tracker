package com.smartspend.smartspend.service;

import com.smartspend.smartspend.entity.User;
import com.smartspend.smartspend.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Transactional
    public void resetUserData(User user) {
        expenseRepository.deleteByUser(user);
    }
}
