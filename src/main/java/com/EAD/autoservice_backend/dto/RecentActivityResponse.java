package com.EAD.autoservice_backend.dto;

import java.time.LocalDateTime;

public class RecentActivityResponse {
    private String activityType; // "APPOINTMENT", "SERVICE", "LEAVE", "REGISTRATION"
    private String description;
    private String userName;
    private LocalDateTime timestamp;
    private String status; // For different colored badges

    public RecentActivityResponse() {}

    public RecentActivityResponse(String activityType, String description, String userName,
                                  LocalDateTime timestamp, String status) {
        this.activityType = activityType;
        this.description = description;
        this.userName = userName;
        this.timestamp = timestamp;
        this.status = status;
    }

    // Getters and Setters
    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}