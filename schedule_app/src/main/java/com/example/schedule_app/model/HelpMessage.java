package com.example.schedule_app.model;

public class HelpMessage {
    private int id;
    private int taskId;
    private int senderId;
    private String senderUsername;
    private String message;
    private String createdAt;

    public HelpMessage() {}

    public HelpMessage(int taskId, int senderId, String message) {
        this.taskId = taskId;
        this.senderId = senderId;
        this.message = message;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTaskId() { return taskId; }
    public void setTaskId(int taskId) { this.taskId = taskId; }

    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }

    public String getSenderUsername() { return senderUsername; }
    public void setSenderUsername(String senderUsername) { this.senderUsername = senderUsername; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
