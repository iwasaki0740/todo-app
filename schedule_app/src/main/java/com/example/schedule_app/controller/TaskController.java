package com.example.schedule_app.controller;

import com.example.schedule_app.model.Schedule;
import com.example.schedule_app.repository.ScheduleRepository;
import com.example.schedule_app.sqlite.Database;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.sql.*;
import java.util.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private ScheduleRepository scheduleRepository;

    // グループ内のタスク一覧を取得
    @GetMapping("/group/{groupId}")
    public List<Schedule> getTasksByGroup(@PathVariable int groupId) {
        return scheduleRepository.findByGroupId(groupId);
    }

    // タスク詳細を取得
    @GetMapping("/{taskId}")
    public Schedule getTask(@PathVariable int taskId) {
        return scheduleRepository.findById(taskId);
    }

    // タスクを作成（グループ版）
    @PostMapping
    public Map<String, Object> createTask(
            @RequestBody Map<String, Object> request,
            HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        String username = (String) session.getAttribute("username");
        
        if (userId == null) {
            return Map.of("success", false, "message", "ログインが必要です");
        }

        String title = request.get("title").toString();
        String description = request.get("description") != null ? 
                request.get("description").toString() : "";
        Integer groupId = null;
        
        try {
            groupId = Integer.parseInt(request.get("groupId").toString());
        } catch (Exception e) {
            // groupIdがない場合はnull
        }

        if (title == null || title.trim().isEmpty()) {
            return Map.of("success", false, "message", "タスク名は必須です");
        }

        int taskId = scheduleRepository.createTask(title, description, groupId, userId, username);
        if (taskId > 0) {
            return Map.of("success", true, "message", "タスクを作成しました", "taskId", taskId);
        }

        return Map.of("success", false, "message", "タスク作成に失敗しました");
    }

    // ステータスを更新
    @PutMapping("/{taskId}/status")
    public Map<String, Object> updateStatus(
            @PathVariable int taskId,
            @RequestBody Map<String, String> request) {
        String status = request.get("status");

        if (status == null || !isValidStatus(status)) {
            return Map.of("success", false, "message", "ステータスが不正です");
        }

        if (scheduleRepository.updateStatus(taskId, status)) {
            return Map.of("success", true, "message", "ステータスを更新しました");
        }

        return Map.of("success", false, "message", "更新に失敗しました");
    }

    // ヘルプフラグを更新
    @PutMapping("/{taskId}/help-flag")
    public Map<String, Object> updateHelpFlag(
            @PathVariable int taskId,
            @RequestBody Map<String, Object> request) {
        Integer isHelpNeeded = null;
        
        try {
            isHelpNeeded = Integer.parseInt(request.get("isHelpNeeded").toString());
        } catch (Exception e) {
            return Map.of("success", false, "message", "リクエスト形式が不正です");
        }

        if (isHelpNeeded == null || (isHelpNeeded != 0 && isHelpNeeded != 1)) {
            return Map.of("success", false, "message", "値が不正です");
        }

        if (scheduleRepository.updateHelpNeeded(taskId, isHelpNeeded)) {
            return Map.of("success", true, "message", "ヘルプフラグを更新しました");
        }

        return Map.of("success", false, "message", "更新に失敗しました");
    }

    // 古いAPI互換性用：全タスク取得
    @GetMapping
    public List<Map<String, Object>> getAll() throws Exception {
        List<Map<String, Object>> list = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT id, title, date, user FROM tasks ORDER BY created_at DESC")) {

            while (rs.next()) {
                Map<String, Object> m = new HashMap<>();
                m.put("id", rs.getInt("id"));
                m.put("title", rs.getString("title"));
                m.put("date", rs.getString("date"));
                m.put("user", rs.getString("user"));
                list.add(m);
            }
        }

        return list;
    }

    // 古いAPI互換性用：タスク追加
    @PostMapping("/add")
    public String addTask(@RequestParam String title,
                          @RequestParam(required = false) String date,
                          HttpSession session) throws Exception {

        String user = (String) session.getAttribute("username");
        if (user == null) {
            user = "Unknown";
        }

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO tasks(title, date, user) VALUES(?, ?, ?)")) {

            ps.setString(1, title);
            ps.setString(2, date);
            ps.setString(3, user);
            ps.executeUpdate();
        }

        return "OK";
    }

    // 古いAPI互換性用：タスク削除
    @DeleteMapping("/{id}")
    public String delete(@PathVariable int id) throws Exception {
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "DELETE FROM tasks WHERE id=?")) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
        return "OK";
    }

    // ステータスチェック
    private boolean isValidStatus(String status) {
        return status.equals("TODO") || 
               status.equals("IN_PROGRESS") || 
               status.equals("DONE");
    }
}
