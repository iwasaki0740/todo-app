package com.example.schedule_app;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

@Component
public class DatabaseInit implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {

        // SQLiteファイルのパス
        String url = "jdbc:sqlite:schedule.db";

        // テーブル作成SQL
        String createSchedulesTable = """
                CREATE TABLE IF NOT EXISTS schedules (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user TEXT NOT NULL,
                    title TEXT NOT NULL,
                    date TEXT NOT NULL
                );
                """;

        String createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL
                );
                """;

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {

            stmt.execute(createSchedulesTable);
            stmt.execute(createUsersTable);
            System.out.println("★ データベーステーブル作成完了");
        }
    }
}