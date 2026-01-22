package com.example.schedule_app.repository;

import com.example.schedule_app.model.Schedule;
import com.example.schedule_app.sqlite.Database;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ScheduleRepository {

    // タスクを作成（グループ機能対応）
    public int createTask(String title, String description, Integer groupId, Integer creatorId, String user) {
        String sql = "INSERT INTO tasks(title, description, group_id, creator_id, status, user) VALUES(?, ?, ?, ?, 'TODO', ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, title);
            pstmt.setString(2, description);
            if (groupId != null) {
                pstmt.setInt(3, groupId);
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }
            if (creatorId != null) {
                pstmt.setInt(4, creatorId);
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }
            pstmt.setString(5, user);
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // グループ内のタスク一覧を取得
    public List<Schedule> findByGroupId(int groupId) {
        String sql = """
            SELECT t.id, t.title, t.description, t.group_id, t.creator_id, 
                   u.username, t.status, t.is_help_needed, t.created_at, t.updated_at, t.user
            FROM tasks t
            LEFT JOIN users u ON t.creator_id = u.id
            WHERE t.group_id = ?
            ORDER BY t.created_at DESC
            """;
        List<Schedule> tasks = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Schedule task = new Schedule();
                    task.setId(rs.getInt("id"));
                    task.setTitle(rs.getString("title"));
                    task.setDescription(rs.getString("description"));
                    task.setGroupId(rs.getInt("group_id"));
                    task.setCreatorId(rs.getInt("creator_id"));
                    task.setCreatorUsername(rs.getString("username"));
                    task.setStatus(rs.getString("status"));
                    task.setIsHelpNeeded(rs.getInt("is_help_needed"));
                    task.setCreatedAt(rs.getString("created_at"));
                    task.setUpdatedAt(rs.getString("updated_at"));
                    task.setUser(rs.getString("user"));
                    tasks.add(task);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    // タスクIDで取得
    public Schedule findById(int taskId) {
        String sql = """
            SELECT t.id, t.title, t.description, t.group_id, t.creator_id, 
                   u.username, t.status, t.is_help_needed, t.created_at, t.updated_at, t.user
            FROM tasks t
            LEFT JOIN users u ON t.creator_id = u.id
            WHERE t.id = ?
            """;
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, taskId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Schedule task = new Schedule();
                    task.setId(rs.getInt("id"));
                    task.setTitle(rs.getString("title"));
                    task.setDescription(rs.getString("description"));
                    task.setGroupId(rs.getInt("group_id"));
                    task.setCreatorId(rs.getInt("creator_id"));
                    task.setCreatorUsername(rs.getString("username"));
                    task.setStatus(rs.getString("status"));
                    task.setIsHelpNeeded(rs.getInt("is_help_needed"));
                    task.setCreatedAt(rs.getString("created_at"));
                    task.setUpdatedAt(rs.getString("updated_at"));
                    task.setUser(rs.getString("user"));
                    return task;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ステータスを更新
    public boolean updateStatus(int taskId, String status) {
        String sql = "UPDATE tasks SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, taskId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ヘルプフラグを更新
    public boolean updateHelpNeeded(int taskId, int isHelpNeeded) {
        String sql = "UPDATE tasks SET is_help_needed = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, isHelpNeeded);
            pstmt.setInt(2, taskId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 古いAPI用：全タスク取得
    public List<Schedule> findAll() {
        List<Schedule> list = new ArrayList<>();
        String sql = """
            SELECT t.id, t.title, t.date, t.user, t.description, t.status, t.is_help_needed, t.created_at
            FROM tasks t
            ORDER BY t.created_at DESC
            """;
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Schedule task = new Schedule();
                task.setId(rs.getInt("id"));
                task.setTitle(rs.getString("title"));
                task.setDate(rs.getString("date"));
                task.setUser(rs.getString("user"));
                task.setDescription(rs.getString("description"));
                task.setStatus(rs.getString("status"));
                task.setIsHelpNeeded(rs.getInt("is_help_needed"));
                task.setCreatedAt(rs.getString("created_at"));
                list.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 古いAPI用：保存
    public void save(Schedule schedule) {
        String sql = "INSERT INTO tasks (user, title, date) VALUES (?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, schedule.getUser());
            pstmt.setString(2, schedule.getTitle());
            pstmt.setString(3, schedule.getDate());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 古いAPI用：削除
    public void deleteById(int id) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 古いAPI用：更新
    public void update(Schedule schedule) {
        String sql = "UPDATE tasks SET title = ?, date = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, schedule.getTitle());
            pstmt.setString(2, schedule.getDate());
            pstmt.setInt(3, schedule.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 古いAPI用：全削除
    public void deleteAll() {
        String sql = "DELETE FROM tasks";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
