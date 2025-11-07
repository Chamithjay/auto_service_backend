package com.EAD.autoservice_backend.controller;

import com.EAD.autoservice_backend.dto.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/chatbot")
@CrossOrigin(origins = "*")
public class ChatBotController {

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        try {
            // Forward to FastAPI
            String url = "http://localhost:8000/api/chat/";
            return restTemplate.postForObject(url, request, ChatResponse.class);
        } catch (Exception e) {
            ChatResponse error = new ChatResponse();
            error.setResponse("Error: " + e.getMessage());
            error.setStatus("error");
            return error;
        }
    }
}