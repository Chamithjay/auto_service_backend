package com.EAD.autoservice_backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class RecentActivityResponse {
    // Getters and Setters
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

}