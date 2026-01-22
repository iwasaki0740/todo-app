package com.example.schedule_app.model;

public class Schedule {
    private int id;
    private String user;
    private String title;
    private String date;
    private String description;
    private int groupId;
    private int creatorId;
    private String creatorUsername;
    private String status;
    private int isHelpNeeded;
    private String createdAt;
    private String updatedAt;

    public Schedule() {}
    
    public Schedule(int id, String user, String title, String date) {
        this.id = id;
        this.user = user;
        this.title = title;
        this.date = date;
    }

    // Basic Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    // Extended Getters and Setters
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getGroupId() { return groupId; }
    public void setGroupId(int groupId) { this.groupId = groupId; }

    public int getCreatorId() { return creatorId; }
    public void setCreatorId(int creatorId) { this.creatorId = creatorId; }

    public String getCreatorUsername() { return creatorUsername; }
    public void setCreatorUsername(String creatorUsername) { this.creatorUsername = creatorUsername; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getIsHelpNeeded() { return isHelpNeeded; }
    public void setIsHelpNeeded(int isHelpNeeded) { this.isHelpNeeded = isHelpNeeded; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
