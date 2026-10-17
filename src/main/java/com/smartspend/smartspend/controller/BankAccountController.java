package com.smartspend.smartspend.controller;

import com.smartspend.smartspend.entity.AccountType;
import com.smartspend.smartspend.entity.BankAccount;
import com.smartspend.smartspend.entity.User;
import com.smartspend.smartspend.repository.BankAccountRepository;
import com.smartspend.smartspend.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Controller
public class BankAccountController {

    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;

    public BankAccountController(BankAccountRepository bankAccountRepository, UserRepository userRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/accounts")
    public String viewAccounts(Model model, Principal principal) {
        // 1. Get the currently logged-in user
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        
        // 2. Fetch only this user's accounts
        List<BankAccount> accounts = bankAccountRepository.findByUser_UserId(user.getUserId());
        
        model.addAttribute("accounts", accounts);
        return "accounts"; // Points to accounts.html
    }

    @PostMapping("/accounts/add")
    public String addAccount(@RequestParam String accountName, 
                             @RequestParam AccountType accountType, 
                             @RequestParam BigDecimal initialBalance, 
                             Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();

        BankAccount account = new BankAccount();
        account.setUser(user);
        account.setAccountName(accountName);
        account.setAccountType(accountType);
        account.setBalance(initialBalance);
        
        // Generate a safe, fake simulated account number
        account.setAccountNumber("SB" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        bankAccountRepository.save(account);

        return "redirect:/accounts?success";
    }
}