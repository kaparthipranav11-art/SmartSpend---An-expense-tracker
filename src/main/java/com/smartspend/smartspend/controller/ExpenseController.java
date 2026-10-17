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
import java.util.List;

@Controller
public class ExpenseController {

    private final ExpenseRepository expenseRepository;
    private final BankAccountRepository bankAccountRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public ExpenseController(ExpenseRepository expenseRepository, BankAccountRepository bankAccountRepository, 
                             CategoryRepository categoryRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/expenses")
    public String viewExpenses(Model model, Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        
        model.addAttribute("expenses", expenseRepository.findByUser_UserId(user.getUserId()));
        model.addAttribute("accounts", bankAccountRepository.findByUser_UserId(user.getUserId()));
        model.addAttribute("categories", categoryRepository.findAll());
        
        return "expenses"; // Points to expenses.html
    }

    @PostMapping("/expenses/add")
    public String addExpense(@RequestParam Long accountId, 
                             @RequestParam Integer categoryId,
                             @RequestParam BigDecimal amount, 
                             @RequestParam String description,
                             @RequestParam PaymentMethod paymentMethod, 
                             Principal principal,
                             org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) { // <-- Added here
        
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        BankAccount account = bankAccountRepository.findById(accountId).orElseThrow();
        Category category = categoryRepository.findById(categoryId).orElseThrow();

        // Check if the account has enough money
        if (account.getBalance().compareTo(amount) < 0) {
            // Safely pass the error message
            redirectAttributes.addFlashAttribute("error", "Insufficient funds! Your balance is only ₹" + account.getBalance());
            return "redirect:/expenses";
        }

        // 1. Create and save the expense
        Expense expense = new Expense();
        expense.setUser(user);
        expense.setAccount(account);
        expense.setCategory(category);
        expense.setAmount(amount);
        expense.setDescription(description);
        expense.setPaymentMethod(paymentMethod);
        expense.setExpenseDate(LocalDate.now());
        expenseRepository.save(expense);

        // 2. Deduct the amount from the bank account balance
        account.setBalance(account.getBalance().subtract(amount));
        bankAccountRepository.save(account);

        return "redirect:/expenses?success";
    }
}