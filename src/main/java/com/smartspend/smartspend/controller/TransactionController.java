package com.smartspend.smartspend.controller;

import com.smartspend.smartspend.dto.TransactionDto;
import com.smartspend.smartspend.entity.Expense;
import com.smartspend.smartspend.entity.Income;
import com.smartspend.smartspend.entity.User;
import com.smartspend.smartspend.repository.ExpenseRepository;
import com.smartspend.smartspend.repository.IncomeRepository;
import com.smartspend.smartspend.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
public class TransactionController {

    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;
    private final UserRepository userRepository;

    public TransactionController(ExpenseRepository expenseRepository, 
                                 IncomeRepository incomeRepository, 
                                 UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.incomeRepository = incomeRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/history")
    public String viewTransactionHistory(Model model, Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        Long userId = user.getUserId();

        List<Expense> expenses = expenseRepository.findByUser_UserId(userId);
        List<Income> incomes = incomeRepository.findByUser_UserId(userId);

        List<TransactionDto> transactions = new ArrayList<>();

        // Map expenses
        for (Expense e : expenses) {
            transactions.add(new TransactionDto(
                e.getExpenseDate(),
                e.getDescription(),
                e.getCategory() != null ? e.getCategory().getCategoryName() : "Expense",
                e.getAccount().getAccountName(),
                e.getAmount(),
                "EXPENSE"
            ));
        }

        // Map incomes
        for (Income i : incomes) {
            transactions.add(new TransactionDto(
                i.getIncomeDate(),
                i.getSource(),
                "Income Deposit",
                i.getAccount().getAccountName(),
                i.getAmount(),
                "INCOME"
            ));
        }

        // Sort chronologically: latest date first
        transactions.sort(Comparator.comparing(TransactionDto::getDate).reversed());

        model.addAttribute("transactions", transactions);
        return "history";
    }
}