package com.smartspend.smartspend.controller;

import com.smartspend.smartspend.entity.*;
import com.smartspend.smartspend.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;

@Controller
public class IncomeController {

    private final IncomeRepository incomeRepository;
    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;

    public IncomeController(IncomeRepository incomeRepository, BankAccountRepository bankAccountRepository, UserRepository userRepository) {
        this.incomeRepository = incomeRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/incomes")
    public String viewIncomes(Model model, Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        
        model.addAttribute("incomes", incomeRepository.findByUser_UserId(user.getUserId()));
        model.addAttribute("accounts", bankAccountRepository.findByUser_UserId(user.getUserId()));
        
        return "incomes"; 
    }

   @PostMapping("/incomes/add") // Or /income/add depending on your form action
public String addIncome(@RequestParam BigDecimal amount,
                        @RequestParam(required = false) String source,
                        @RequestParam(required = false) String description,
                        @RequestParam(required = false) String incomeDate,
                        Principal principal,
                        RedirectAttributes redirectAttributes) {

    // 1. Validation check for positive amount
    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
        redirectAttributes.addFlashAttribute("errorMessage", "Income amount must be greater than zero.");
        return "redirect:/incomes"; // Or /dashboard
    }

    User user = userRepository.findByEmail(principal.getName()).orElseThrow();

    Income income = new Income();
    income.setAmount(amount);
    income.setSource(source != null && !source.isBlank() ? source : "General Income");
    income.setDescription(description);
    income.setUser(user);

    // 2. Safe Date Fallback: Use submitted date or default to today if null
    if (incomeDate != null && !incomeDate.isBlank()) {
        income.setIncomeDate(LocalDate.parse(incomeDate));
    } else {
        income.setIncomeDate(LocalDate.now());
    }

    incomeRepository.save(income);

    redirectAttributes.addFlashAttribute("successMessage", "Income added successfully!");
    return "redirect:/dashboard";
}
}
