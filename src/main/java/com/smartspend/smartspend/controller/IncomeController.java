package com.smartspend.smartspend.controller;

import com.smartspend.smartspend.entity.Income;
import com.smartspend.smartspend.entity.User;
import com.smartspend.smartspend.repository.IncomeRepository;
import com.smartspend.smartspend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;

@Controller
public class IncomeController {

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/incomes/add")
    public String addIncome(
            @RequestParam(required = false) BigDecimal amount,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate incomeDate,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        // Session Safety Check
        if (principal == null) {
            return "redirect:/login";
        }

        // Amount Validation Check
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Income amount must be greater than zero.");
            return "redirect:/dashboard";
        }

        User user = userRepository.findByEmail(principal.getName()).orElse(null);
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Session expired. Please log in again.");
            return "redirect:/login";
        }

        Income income = new Income();
        income.setAmount(amount);
        income.setSource(source != null && !source.isBlank() ? source : "General Income");
        income.setDescription(description);
        income.setIncomeDate(incomeDate != null ? incomeDate : LocalDate.now());
        income.setUser(user);

        incomeRepository.save(income);

        redirectAttributes.addFlashAttribute("successMessage", "Income added successfully!");
        return "redirect:/dashboard";
    }
}