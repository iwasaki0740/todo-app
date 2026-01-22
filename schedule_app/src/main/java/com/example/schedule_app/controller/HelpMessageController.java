package com.example.schedule_app.controller;

import com.example.schedule_app.model.HelpMessage;
import com.example.schedule_app.repository.HelpMessageRepository;
import com.example.schedule_app.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.*;

@RestController
@RequestMapping("/api/help-messages")
public class HelpMessageController {

    @Autowired
    private HelpMessageRepository helpMessageRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    // メッセージ一覧を取得
    @GetMapping("/task/{taskId}")
    public List<HelpMessage> getMessages(@PathVariable int taskId) {
        return helpMessageRepository.findByTaskId(taskId);
    }

    // メッセージを送信
    @PostMapping
    public Map<String, Object> sendMessage(
            @RequestBody Map<String, Object> request,
            HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return Map.of("success", false, "message", "ログインが必要です");
        }

        Integer taskId = null;
        String message = null;

        try {
            taskId = Integer.parseInt(request.get("taskId").toString());
            message = request.get("message").toString();
        } catch (Exception e) {
            return Map.of("success", false, "message", "リクエスト形式が不正です");
        }

        if (message == null || message.trim().isEmpty()) {
            return Map.of("success", false, "message", "メッセージは必須です");
        }

        // タスクが存在するかチェック
        if (scheduleRepository.findById(taskId) == null) {
            return Map.of("success", false, "message", "タスクが見つかりません");
        }

        // メッセージを送信
        if (helpMessageRepository.sendMessage(taskId, userId, message)) {
            return Map.of("success", true, "message", "メッセージを送信しました");
        }

        return Map.of("success", false, "message", "メッセージ送信に失敗しました");
    }

    // メッセージを削除
    @DeleteMapping("/{messageId}")
    public Map<String, Object> deleteMessage(
            @PathVariable int messageId,
            HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return Map.of("success", false, "message", "ログインが必要です");
        }

        if (helpMessageRepository.deleteMessage(messageId)) {
            return Map.of("success", true, "message", "メッセージを削除しました");
        }

        return Map.of("success", false, "message", "削除に失敗しました");
    }
}
