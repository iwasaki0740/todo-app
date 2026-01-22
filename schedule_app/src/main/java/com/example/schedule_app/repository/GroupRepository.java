package com.example.schedule_app.repository;

import com.example.schedule_app.model.Group;
import com.example.schedule_app.sqlite.Database;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.util.*;

@Repository
public class GroupRepository {

    // グループを作成
    public int createGroup(String name, String description, int ownerId) {
        String sql = "INSERT INTO groups(name, description, owner_id) VALUES(?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name);
            pstmt.setString(2, description);
            pstmt.setInt(3, ownerId);
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

    // グループIDで取得
    public Group findById(int groupId) {
        String sql = "SELECT id, name, description, owner_id, created_at FROM groups WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Group group = new Group();
                    group.setId(rs.getInt("id"));
                    group.setName(rs.getString("name"));
                    group.setDescription(rs.getString("description"));
                    group.setOwnerId(rs.getInt("owner_id"));
                    group.setCreatedAt(rs.getString("created_at"));
                    
                    // メンバー数を取得
                    group.setMemberCount(getMemberCount(groupId));
                    
                    return group;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ユーザーが属するグループ一覧を取得
    public List<Group> findByUserId(int userId) {
        String sql = """
            SELECT g.id, g.name, g.description, g.owner_id, g.created_at
            FROM groups g
            JOIN group_members gm ON g.id = gm.group_id
            WHERE gm.user_id = ?
            ORDER BY g.created_at DESC
            """;
        List<Group> groups = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Group group = new Group();
                    group.setId(rs.getInt("id"));
                    group.setName(rs.getString("name"));
                    group.setDescription(rs.getString("description"));
                    group.setOwnerId(rs.getInt("owner_id"));
                    group.setCreatedAt(rs.getString("created_at"));
                    group.setMemberCount(getMemberCount(group.getId()));
                    groups.add(group);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groups;
    }

    // グループにメンバーを追加
    public boolean addMember(int groupId, int userId) {
        String sql = "INSERT INTO group_members(group_id, user_id) VALUES(?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // グループメンバー一覧を取得
    public List<Map<String, Object>> getGroupMembers(int groupId) {
        String sql = """
            SELECT u.id, u.username
            FROM users u
            JOIN group_members gm ON u.id = gm.user_id
            WHERE gm.group_id = ?
            ORDER BY u.username
            """;
        List<Map<String, Object>> members = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> member = new HashMap<>();
                    member.put("id", rs.getInt("id"));
                    member.put("username", rs.getString("username"));
                    members.add(member);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }

    // グループを削除
    public boolean deleteGroup(int groupId) {
        String sql = "DELETE FROM groups WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // メンバー数を取得
    private int getMemberCount(int groupId) {
        String sql = "SELECT COUNT(*) as count FROM group_members WHERE group_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);
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

    // ユーザーIDでグループメンバーを検索
    public boolean isMember(int groupId, int userId) {
        String sql = "SELECT COUNT(*) as count FROM group_members WHERE group_id = ? AND user_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);
            pstmt.setInt(2, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
