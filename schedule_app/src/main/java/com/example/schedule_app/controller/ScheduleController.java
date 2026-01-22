package com.example.schedule_app.controller;

import com.example.schedule_app.model.Schedule;
import com.example.schedule_app.repository.ScheduleRepository;

import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ScheduleController {

    private final ScheduleRepository repo;

    public ScheduleController(ScheduleRepository repo) {
        this.repo = repo;
    }

    @PostMapping("/schedules")
    public Map<String, Object> addSchedule(@RequestBody Schedule schedule, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        String user = (String) session.getAttribute("username");
        if (user == null) {
            user = (String) session.getAttribute("user");
        }
        
        if(user == null) {
            response.put("success", false);
            response.put("message", "ログインしてください");
            return response;
        }
        
        schedule.setUser(user);
        repo.save(schedule);
        
        response.put("success", true);
        response.put("message", "スケジュールを追加しました");
        return response;
    }

    @GetMapping("/schedules")
    public List<Schedule> getSchedules(
            @RequestParam(defaultValue = "desc") String sort,
            HttpSession session) {
        String user = (String) session.getAttribute("username");
        if (user == null) {
            user = (String) session.getAttribute("user");
        }
        
        final String finalUser = user;  // finalに変換
        if(finalUser == null) return List.of();
        
        List<Schedule> schedules = repo.findAll().stream()
                .filter(s -> s.getUser() != null && s.getUser().equals(finalUser))
                .collect(Collectors.toList());
        
        // ソート
        if ("asc".equals(sort)) {
            schedules.sort(Comparator.comparing(Schedule::getDate));
        } else {
            schedules.sort(Comparator.comparing(Schedule::getDate).reversed());
        }
        
        return schedules;
    }

    @DeleteMapping("/schedules/{id}")
    public Map<String, Object> deleteSchedule(@PathVariable int id, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        String user = (String) session.getAttribute("username");
        if (user == null) {
            user = (String) session.getAttribute("user");
        }
        
        if(user == null) {
            response.put("success", false);
            response.put("message", "ログインしてください");
            return response;
        }
        
        repo.deleteById(id);
        
        response.put("success", true);
        response.put("message", "削除完了");
        return response;
    }

    @PutMapping("/schedules/{id}")
    public Map<String, Object> updateSchedule(
            @PathVariable int id,
            @RequestBody Schedule schedule,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        String user = (String) session.getAttribute("username");
        if (user == null) {
            user = (String) session.getAttribute("user");
        }
        
        if(user == null) {
            response.put("success", false);
            response.put("message", "ログインしてください");
            return response;
        }
        
        schedule.setId(id);
        schedule.setUser(user);
        repo.update(schedule);
        
        response.put("success", true);
        response.put("message", "更新完了");
        return response;
    }

    @DeleteMapping("/schedules")
    public Map<String, Object> deleteAll(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        String user = (String) session.getAttribute("username");
        if (user == null) {
            user = (String) session.getAttribute("user");
        }
        
        if(user == null) {
            response.put("success", false);
            response.put("message", "ログインしてください");
            return response;
        }
        
        repo.deleteAll();
        
        response.put("success", true);
        response.put("message", "全削除完了");
        return response;
    }
}
