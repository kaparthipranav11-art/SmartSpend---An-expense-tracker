package com.smartspend.smartspend.controller;

import com.smartspend.smartspend.entity.*;
import com.smartspend.smartspend.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class BudgetController {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository; // 1. Declare the repository

    // 2. Inject it in the constructor
    public BudgetController(BudgetRepository budgetRepository, 
                            CategoryRepository categoryRepository, 
                            UserRepository userRepository,
                            ExpenseRepository expenseRepository) {
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.expenseRepository = expenseRepository;
    }

    @GetMapping("/budgets")
    public String viewBudgets(Model model, Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        Long userId = user.getUserId();
        
        List<Budget> budgets = budgetRepository.findByUser_UserId(userId);
        
        Map<Long, BigDecimal> spentMap = new HashMap<>();
        Map<Long, Integer> percentageMap = new HashMap<>();

        for (Budget b : budgets) {
            // 3. Call it on the lowercase instance variable (expenseRepository)
            BigDecimal spent = expenseRepository.getSpentByCategory(userId, b.getCategory().getCategoryId());
            spentMap.put(b.getBudgetId(), spent);

            if (b.getMonthlyLimit().compareTo(BigDecimal.ZERO) > 0) {
                int pct = spent.multiply(new BigDecimal(100)).divide(b.getMonthlyLimit(), RoundingMode.HALF_UP).intValue();
                percentageMap.put(b.getBudgetId(), pct);
            } else {
                percentageMap.put(b.getBudgetId(), 0);
            }
        }

        model.addAttribute("budgets", budgets);
        model.addAttribute("spentMap", spentMap);
        model.addAttribute("percentageMap", percentageMap);
        model.addAttribute("categories", categoryRepository.findAll());
        
        return "budgets";
    }

    @PostMapping("/budgets/add")
    public String addBudget(@RequestParam Integer categoryId, 
                            @RequestParam BigDecimal monthlyLimit, 
                            Principal principal) {
        
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        Category category = categoryRepository.findById(categoryId).orElseThrow();

        Budget budget = new Budget();
        budget.setUser(user);
        budget.setCategory(category);
        budget.setMonthlyLimit(monthlyLimit);

        budgetRepository.save(budget);

        return "redirect:/budgets?success";
    }
}