package com.smartspend.smartspend.controller;

import com.smartspend.smartspend.entity.Goal;
import com.smartspend.smartspend.entity.User;
import com.smartspend.smartspend.repository.BankAccountRepository;
import com.smartspend.smartspend.repository.ExpenseRepository;
import com.smartspend.smartspend.repository.GoalRepository;
import com.smartspend.smartspend.repository.IncomeRepository;
import com.smartspend.smartspend.repository.NotificationRepository;
import com.smartspend.smartspend.repository.UserRepository;
import com.smartspend.smartspend.service.TransactionService;
import com.smartspend.smartspend.entity.Notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

@Controller
public class DashboardController {

    @Autowired private NotificationRepository notificationRepository;

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private TransactionService transactionService;

    private final UserRepository userRepository;
    private final BankAccountRepository bankAccountRepository;
    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;

    // Constructor
    public DashboardController(UserRepository userRepository, 
                               BankAccountRepository bankAccountRepository, 
                               ExpenseRepository expenseRepository,
                               IncomeRepository incomeRepository) {
        this.userRepository = userRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.expenseRepository = expenseRepository;
        this.incomeRepository = incomeRepository;
    }


@PostMapping("/goals/add")
public String addGoal(@RequestParam String goalName, 
                      @RequestParam BigDecimal targetAmount, 
                      @RequestParam BigDecimal currentAmount, 
                      Principal principal, 
                      RedirectAttributes redirectAttributes) { // Added RedirectAttributes
                      
    // THE SECURITY CHECK: Block negative numbers
    if (targetAmount.compareTo(BigDecimal.ZERO) <= 0 || currentAmount.compareTo(BigDecimal.ZERO) < 0) {
        redirectAttributes.addFlashAttribute("errorMessage", "Error: Amounts must be positive numbers.");
        return "redirect:/goals";
    }

    User user = userRepository.findByEmail(principal.getName()).orElseThrow();
    
    Goal newGoal = new Goal();
    newGoal.setGoalName(goalName);
    newGoal.setTargetAmount(targetAmount);
    newGoal.setCurrentAmount(currentAmount);
    newGoal.setUser(user);
    
    goalRepository.save(newGoal);
    
    redirectAttributes.addFlashAttribute("successMessage", "Goal created successfully!");
    return "redirect:/dashboard";
}

    @PostMapping("/user/reset-data")
    public String resetUserData(Principal principal, RedirectAttributes redirectAttributes) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        transactionService.resetUserData(user);
        redirectAttributes.addFlashAttribute("successMessage", "All financial data has been successfully reset!");
        return "redirect:/dashboard";
    }

  @GetMapping("/goals")
public String showGoalsPage(Model model, Principal principal) {
    User user = userRepository.findByEmail(principal.getName()).orElseThrow();
    
    // Safely fetch all goals and filter for the current user
    List<Goal> allGoals = goalRepository.findAll();
    List<Goal> userGoals = new ArrayList<>();
    for (Goal goal : allGoals) {
        if (goal.getUser().getUserId().equals(user.getUserId())) {
            userGoals.add(goal);
        }
    }
    
    model.addAttribute("goals", userGoals);
    return "goals"; 
}

    @GetMapping("/notifications")
public String showNotifications(Model model, Principal principal) {
    User user = userRepository.findByEmail(principal.getName()).orElseThrow();
    
    // Fetch all notifications for this user
    List<Notification> notifications = notificationRepository.findByUserOrderByCreatedAtDesc(user);
    
    // Mark them all as read since the user is now looking at them
    for (Notification notif : notifications) {
        if (!notif.isRead()) {
            notif.setRead(true);
            notificationRepository.save(notif);
        }
    }

    model.addAttribute("notifications", notifications);
    return "notifications";
}

    @GetMapping("/dashboard")
    public String showDashboard(Model model, Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        Long userId = user.getUserId();

        long unreadCount = notificationRepository.countByUserAndIsReadFalse(user);

        // Calculate the first and last day of the current month
        java.time.LocalDate startDate = java.time.LocalDate.now().withDayOfMonth(1);
        java.time.LocalDate endDate = java.time.LocalDate.now().withDayOfMonth(java.time.LocalDate.now().lengthOfMonth());

        // Fetch scoped totals
        BigDecimal totalBalance = bankAccountRepository.getTotalBalance(userId); // Keep all-time
        BigDecimal totalExpenses = expenseRepository.getTotalExpensesForMonth(userId, startDate, endDate);
        BigDecimal totalIncome = incomeRepository.getTotalIncomeForMonth(userId, startDate, endDate);
        
        // Calculate Active Goals
        long activeGoals = goalRepository.countByUser_UserId(userId);

        // Fetch scoped spending breakdown by category
        List<Object[]> categorySpending = expenseRepository.getExpensesByCategoryForMonth(userId, startDate, endDate);
        
        List<String> labels = new ArrayList<>();
        List<BigDecimal> data = new ArrayList<>();

        for (Object[] row : categorySpending) {
            labels.add((String) row[0]);             // Category Name
            data.add((BigDecimal) row[1]);           // Total Amount Spent
        }

        // Fallback if no expenses exist yet so the chart doesn't break
        if (labels.isEmpty()) {
            labels.add("No Expenses Yet");
            data.add(BigDecimal.ZERO);
        }

        // Add attributes to the model
        model.addAttribute("fullName", user.getFullName());
        model.addAttribute("totalIncome", totalIncome);
        model.addAttribute("totalExpenses", totalExpenses);
        model.addAttribute("availableBalance", totalBalance);
        
        // Use the calculated activeGoals variable here instead of 0
        model.addAttribute("activeGoalsCount", activeGoals); 
        model.addAttribute("unreadCount", unreadCount);

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