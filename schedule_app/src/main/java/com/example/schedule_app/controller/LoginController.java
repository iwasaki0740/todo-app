package com.example.schedule_app.controller;

import com.example.schedule_app.model.User;
import com.example.schedule_app.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session) {

        // ユーザーをDBから検索
        User user = userRepository.findByUsername(username);
        
        if(user != null && user.getPassword().equals(password)) {
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("user", user.getUsername());  // 互換性のため
            return "redirect:/schedule.html";
        }

        return "redirect:/auth.html?error";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/auth.html";
    }
}

