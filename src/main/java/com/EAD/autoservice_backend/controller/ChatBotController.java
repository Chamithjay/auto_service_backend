package com.EAD.autoservice_backend.controller;

import com.EAD.autoservice_backend.dto.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

/**
 * REST controller for chatbot integration.
 * Forwards chat requests to FastAPI chatbot service.
 */
@RestController
@RequestMapping("/api/chatbot")
@CrossOrigin(origins = "*")
public class ChatBotController {

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Processes a chat message by forwarding it to the FastAPI chatbot service.
     *
     * @param request the chat request containing the user's message
     * @return ChatResponse containing the bot's reply or error message
     */
    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        try {
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