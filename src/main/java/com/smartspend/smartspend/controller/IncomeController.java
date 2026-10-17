package com.smartspend.smartspend.controller;

import com.smartspend.smartspend.entity.*;
import com.smartspend.smartspend.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @PostMapping("/incomes/add")
    public String addIncome(@RequestParam Long accountId, 
                            @RequestParam BigDecimal amount, 
                            @RequestParam String source,
                            Principal principal) {
        
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        BankAccount account = bankAccountRepository.findById(accountId).orElseThrow();

        // 1. Save the income record
        Income income = new Income();
        income.setUser(user);
        income.setAccount(account);
        income.setAmount(amount);
        income.setSource(source);
        income.setIncomeDate(LocalDate.now());
        incomeRepository.save(income);

        // 2. Add the amount to the bank account balance
        account.setBalance(account.getBalance().add(amount));
        bankAccountRepository.save(account);

        return "redirect:/incomes?success";
    }
}
