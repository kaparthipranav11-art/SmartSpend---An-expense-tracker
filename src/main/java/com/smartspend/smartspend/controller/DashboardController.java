package com.smartspend.smartspend.controller;

import com.smartspend.smartspend.entity.User;
import com.smartspend.smartspend.repository.BankAccountRepository;
import com.smartspend.smartspend.repository.ExpenseRepository;
import com.smartspend.smartspend.repository.IncomeRepository;
import com.smartspend.smartspend.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

@Controller
public class DashboardController {

    private final UserRepository userRepository;
    private final BankAccountRepository bankAccountRepository;
    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository; // Added IncomeRepository

    // Updated constructor to include IncomeRepository
    public DashboardController(UserRepository userRepository, 
                               BankAccountRepository bankAccountRepository, 
                               ExpenseRepository expenseRepository,
                               IncomeRepository incomeRepository) {
        this.userRepository = userRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.expenseRepository = expenseRepository;
        this.incomeRepository = incomeRepository;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model, Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        Long userId = user.getUserId();

        BigDecimal totalBalance = bankAccountRepository.getTotalBalance(userId);
        BigDecimal totalExpenses = expenseRepository.getTotalExpenses(userId);
        BigDecimal totalIncome = incomeRepository.getTotalIncome(userId);

        // Fetch real spending breakdown by category
        List<Object[]> categorySpending = expenseRepository.getExpensesByCategory(userId);
        
        List<String> labels = new java.util.ArrayList<>();
        List<BigDecimal> data = new java.util.ArrayList<>();

        for (Object[] row : categorySpending) {
            labels.add((String) row[0]);             // Category Name
            data.add((BigDecimal) row[1]);           // Total Amount Spent
        }

        // Fallback if no expenses exist yet so the chart doesn't break
        if (labels.isEmpty()) {
            labels.add("No Expenses Yet");
            data.add(BigDecimal.ZERO);
        }

        model.addAttribute("fullName", user.getFullName());
        model.addAttribute("totalIncome", totalIncome);
        model.addAttribute("totalExpenses", totalExpenses);
        model.addAttribute("availableBalance", totalBalance);
        model.addAttribute("activeGoalsCount", 0);
        model.addAttribute("unreadCount", 0);

        // Pass real data to Chart.js
        model.addAttribute("chartLabels", labels);
        model.addAttribute("chartData", data);

        model.addAttribute("insights", Arrays.asList(
            "Your expenses are being tracked accurately.",
            "Monitor your category spending regularly to stay on budget."
        ));

        return "dashboard";
    }
}