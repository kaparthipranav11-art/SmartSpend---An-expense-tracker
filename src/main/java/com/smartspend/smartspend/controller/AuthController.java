package com.smartspend.smartspend.controller;

import com.smartspend.smartspend.entity.User;
import com.smartspend.smartspend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String fullName, 
                               @RequestParam String email, 
                               @RequestParam String password, 
                               Model model) {
        if (userRepository.findByEmail(email).isPresent()) {
            model.addAttribute("error", "Email is already registered!");
            return "register";
        }

        User newUser = new User();
        newUser.setFullName(fullName);
        newUser.setEmail(email);
        newUser.setPasswordHash(passwordEncoder.encode(password)); // Hash the password before saving!
        
        userRepository.save(newUser);

        return "redirect:/login?success";
    }
}