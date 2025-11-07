package com.EAD.autoservice_backend.dto;
import lombok.Data;

@Data
public class ChatResponse {
    private String response;
    private Object data;
    private String status;
}