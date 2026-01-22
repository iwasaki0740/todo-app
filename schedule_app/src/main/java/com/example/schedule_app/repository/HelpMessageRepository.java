package com.example.schedule_app.repository;

import com.example.schedule_app.model.HelpMessage;
import com.example.schedule_app.sqlite.Database;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.util.*;

@Repository
public class HelpMessageRepository {

    // メッセージを送信
    public boolean sendMessage(int taskId, int senderId, String message) {
        String sql = "INSERT INTO help_messages(task_id, sender_id, message) VALUES(?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, taskId);
            pstmt.setInt(2, senderId);
            pstmt.setString(3, message);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // タスクのメッセージ一覧を取得
    public List<HelpMessage> findByTaskId(int taskId) {
        String sql = """
            SELECT hm.id, hm.task_id, hm.sender_id, u.username, hm.message, hm.created_at
            FROM help_messages hm
            JOIN users u ON hm.sender_id = u.id
            WHERE hm.task_id = ?
            ORDER BY hm.created_at ASC
            """;
        List<HelpMessage> messages = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, taskId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    HelpMessage msg = new HelpMessage();
                    msg.setId(rs.getInt("id"));
                    msg.setTaskId(rs.getInt("task_id"));
                    msg.setSenderId(rs.getInt("sender_id"));
                    msg.setSenderUsername(rs.getString("username"));
                    msg.setMessage(rs.getString("message"));
                    msg.setCreatedAt(rs.getString("created_at"));
                    messages.add(msg);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    // メッセージを削除
    public boolean deleteMessage(int messageId) {
        String sql = "DELETE FROM help_messages WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, messageId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // メッセージ数を取得
    public int getMessageCount(int taskId) {
        String sql = "SELECT COUNT(*) as count FROM help_messages WHERE task_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, taskId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
