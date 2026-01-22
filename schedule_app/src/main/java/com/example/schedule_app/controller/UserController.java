package com.example.schedule_app.controller;

import com.example.schedule_app.model.User;
import com.example.schedule_app.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserRepository userRepo;

    public UserController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    // --------------------------
    // ユーザー登録 API
    // --------------------------
    @PostMapping("/user/register")
    public Map<String, Object> register(@RequestBody Map<String, String> form) {
        Map<String, Object> response = new HashMap<>();
        
        String username = form.get("username");
        String password = form.get("password");

        if (username == null || username.isEmpty()) {
            response.put("success", false);
            response.put("message", "ユーザー名を入力してください");
            return response;
        }

        if (password == null || password.isEmpty()) {
            response.put("success", false);
            response.put("message", "パスワードを入力してください");
            return response;
        }

        // ユーザー名の重複チェック
        if (userRepo.findByUsername(username) != null) {
            response.put("success", false);
            response.put("message", "このユーザー名は既に使用されています");
            return response;
        }

        // ユーザーを保存（パスワードは本来ハッシュ化すべき）
        User user = new User();
        user.setUsername(username);
        user.setPassword(password); // 本番環境ではBCryptなどでハッシュ化する
        userRepo.save(user);

        response.put("success", true);
        response.put("message", "ユーザー登録完了");
        return response;
    }

    // --------------------------
    // ログイン API
    // --------------------------
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> form, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        String username = form.get("username");
        String password = form.get("password");

        User user = userRepo.findByUsername(username);

        if (user == null) {
            response.put("success", false);
            response.put("message", "ユーザー名またはパスワードが正しくありません");
            return response;
        }

        if (!user.getPassword().equals(password)) {
            response.put("success", false);
            response.put("message", "ユーザー名またはパスワードが正しくありません");
            return response;
        }

        // セッション属性を統一
        session.setAttribute("userId", user.getId());
        session.setAttribute("username", username);
        session.setAttribute("user", username);
        
        System.out.println("★ ログイン成功: " + username);
        System.out.println("★ セッションID: " + session.getId());

        response.put("success", true);
        response.put("message", "ログイン成功");
        return response;
    }

    // --------------------------
    // ログアウト API
    // --------------------------
    @PostMapping("/logout")
    public Map<String, Object> logout(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        session.invalidate();
        response.put("success", true);
        response.put("message", "ログアウトしました");
        return response;
    }

    // --------------------------
    // 現在のユーザー取得 API
    // --------------------------
    @GetMapping("/current-user")
    public Map<String, Object> currentUser(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        Integer userId = (Integer) session.getAttribute("userId");
        String user = (String) session.getAttribute("username");
        if (user == null) {
            user = (String) session.getAttribute("user");
        }
        
        System.out.println("★ セッションID: " + session.getId());
        System.out.println("★ セッションのユーザー: " + user);
        
        if (userId != null && user != null) {
            response.put("loggedIn", true);
            response.put("userId", userId);
            response.put("username", user);
        } else {
            response.put("loggedIn", false);
        }
        
        return response;
    }
}