package com.example.schedule_app.sqlite;

import java.sql.*;

public class Database {

    private static final String URL = "jdbc:sqlite:schedule.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void init() {
        try (Connection conn = getConnection();
             Statement st = conn.createStatement()) {

            // ユーザーテーブル
            st.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
                );
            """);

            // グループテーブル
            st.execute("""
                CREATE TABLE IF NOT EXISTS groups (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    description TEXT,
                    owner_id INTEGER NOT NULL,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (owner_id) REFERENCES users(id)
                );
            """);

            // グループメンバーテーブル
            st.execute("""
                CREATE TABLE IF NOT EXISTS group_members (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    group_id INTEGER NOT NULL,
                    user_id INTEGER NOT NULL,
                    joined_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE,
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                    UNIQUE(group_id, user_id)
                );
            """);

            // 新しいタスクテーブル（拡張版）
            st.execute("""
                CREATE TABLE IF NOT EXISTS tasks (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    description TEXT,
                    date TEXT,
                    group_id INTEGER,
                    creator_id INTEGER,
                    status TEXT DEFAULT 'TODO',
                    is_help_needed INTEGER DEFAULT 0,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    user TEXT,
                    FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE,
                    FOREIGN KEY (creator_id) REFERENCES users(id)
                );
            """);

            // メッセージテーブル（ヘルプ関連）
            st.execute("""
                CREATE TABLE IF NOT EXISTS help_messages (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    task_id INTEGER NOT NULL,
                    sender_id INTEGER NOT NULL,
                    message TEXT NOT NULL,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
                    FOREIGN KEY (sender_id) REFERENCES users(id)
                );
            """);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}