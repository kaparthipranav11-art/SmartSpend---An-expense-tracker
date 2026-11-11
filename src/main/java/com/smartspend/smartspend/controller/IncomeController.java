
package com.smartspend.smartspend.controller;

import com.smartspend.smartspend.entity.Income;
import com.smartspend.smartspend.entity.User;
import com.smartspend.smartspend.entity.BankAccount;
import com.smartspend.smartspend.repository.IncomeRepository;
import com.smartspend.smartspend.repository.UserRepository;
import com.smartspend.smartspend.repository.BankAccountRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
public class IncomeController {

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @GetMapping("/incomes")
    public String showIncomes(Model model, Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        User user = userRepository.findByEmail(principal.getName())
                .orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("incomes",
                incomeRepository.findByUser_UserId(user.getUserId()));

        model.addAttribute("accounts",
                bankAccountRepository.findByUser_UserId(user.getUserId()));

        return "incomes";
    }

@PostMapping("/incomes/add")
@Transactional
public String addIncome(
        @RequestParam BigDecimal amount,
        @RequestParam(required = false) String source,
        @RequestParam(required = false) String description,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate incomeDate,
        @RequestParam Long accountId,
        Principal principal,
        RedirectAttributes redirectAttributes) {

    try {
        if (principal == null) {
            return "redirect:/login";
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Income must be greater than zero.");
            return "redirect:/incomes";
        }

        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow();

        BankAccount account = bankAccountRepository
                .findByAccountIdAndUser_UserId(accountId, user.getUserId())
                .orElse(null);

        if (account == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Invalid bank account.");
            return "redirect:/incomes";
        }

        Income income = new Income();
        income.setAmount(amount);
        income.setSource(
                source != null && !source.isBlank()
                        ? source : "General Income");
        income.setDescription(
    description != null && !description.isBlank()
        ? description
        : "General Income"
);
        income.setIncomeDate(
                incomeDate != null ? incomeDate : LocalDate.now());
        income.setUser(user);
        income.setAccount(account);

        incomeRepository.save(income);

        BigDecimal balance = account.getBalance() != null
                ? account.getBalance() : BigDecimal.ZERO;

        account.setBalance(balance.add(amount));
        bankAccountRepository.save(account);

        redirectAttributes.addFlashAttribute(
                "successMessage", "Income added successfully!");

        return "redirect:/incomes";

    } catch (Exception e) {
        e.printStackTrace();

        redirectAttributes.addFlashAttribute(
                "errorMessage", "Failed to add income: " + e.getMessage());

        return "redirect:/incomes";
    }
}

}
