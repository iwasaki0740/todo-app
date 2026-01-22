package com.example.schedule_app;

import com.example.schedule_app.sqlite.Database;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ScheduleAppApplication {
    public static void main(String[] args) {
        // データベースを初期化
        Database.init();
        
        SpringApplication.run(ScheduleAppApplication.class, args);
    }
}