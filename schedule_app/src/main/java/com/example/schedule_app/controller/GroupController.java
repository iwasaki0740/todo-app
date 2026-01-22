package com.example.schedule_app.controller;

import com.example.schedule_app.model.Group;
import com.example.schedule_app.repository.GroupRepository;
import com.example.schedule_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.*;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    @Autowired
    private GroupRepository groupRepository;
    
    @Autowired
    private UserRepository userRepository;

    // グループ一覧を取得
    @GetMapping
    public List<Group> getUserGroups(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return new ArrayList<>();
        }
        return groupRepository.findByUserId(userId);
    }

    // グループを作成
    @PostMapping
    public Map<String, Object> createGroup(
            @RequestBody Map<String, String> request,
            HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return Map.of("success", false, "message", "ログインが必要です");
        }

        String name = request.get("name");
        String description = request.get("description");

        if (name == null || name.trim().isEmpty()) {
            return Map.of("success", false, "message", "グループ名は必須です");
        }

        int groupId = groupRepository.createGroup(name, description != null ? description : "", userId);
        if (groupId > 0) {
            // グループ作成者を自動的にメンバーに追加
            groupRepository.addMember(groupId, userId);
            return Map.of("success", true, "message", "グループを作成しました", "groupId", groupId);
        }

        return Map.of("success", false, "message", "グループ作成に失敗しました");
    }

    // グループ詳細を取得
    @GetMapping("/{groupId}")
    public Group getGroup(@PathVariable int groupId) {
        return groupRepository.findById(groupId);
    }

    // グループにメンバーを追加
    @PostMapping("/{groupId}/members")
    public Map<String, Object> addMember(
            @PathVariable int groupId,
            @RequestBody Map<String, String> request) {
        String username = request.get("username");

        if (username == null || username.trim().isEmpty()) {
            return Map.of("success", false, "message", "ユーザー名は必須です");
        }

        // ユーザーを取得
        Integer userId = userRepository.findIdByUsername(username);
        if (userId == null) {
            return Map.of("success", false, "message", "ユーザーが見つかりません");
        }

        // メンバーを追加
        if (groupRepository.addMember(groupId, userId)) {
            return Map.of("success", true, "message", "メンバーを追加しました");
        }

        return Map.of("success", false, "message", "メンバー追加に失敗しました");
    }

    // グループメンバー一覧を取得
    @GetMapping("/{groupId}/members")
    public List<Map<String, Object>> getMembers(@PathVariable int groupId) {
        return groupRepository.getGroupMembers(groupId);
    }

    // グループを削除
    @DeleteMapping("/{groupId}")
    public Map<String, Object> deleteGroup(@PathVariable int groupId, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return Map.of("success", false, "message", "ログインが必要です");
        }

        Group group = groupRepository.findById(groupId);
        if (group == null) {
            return Map.of("success", false, "message", "グループが見つかりません");
        }

        // グループオーナーのみ削除可能
        if (group.getOwnerId() != userId) {
            return Map.of("success", false, "message", "削除権限がありません");
        }

        if (groupRepository.deleteGroup(groupId)) {
            return Map.of("success", true, "message", "グループを削除しました");
        }

        return Map.of("success", false, "message", "削除に失敗しました");
    }
}
