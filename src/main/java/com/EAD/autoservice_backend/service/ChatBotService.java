package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.dto.ChatRequest;
import com.EAD.autoservice_backend.dto.ChatResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class ChatBotService {

    private final RestTemplate restTemplate;

    @Value("${chatbot.api.url:http://localhost:8000}")
    private String chatbotApiUrl;

    public ChatBotService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Send message to FastAPI chatbot and get response
     *
     * @param chatRequest the chat request containing user message
     * @return ChatResponse with bot reply
     */
    public ChatResponse sendMessage(ChatRequest chatRequest) {
        try {
            // Log the request
            log.info("Sending message to chatbot: {}", chatRequest.getMessage());

            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Create HTTP entity with request body and headers
            HttpEntity<ChatRequest> requestEntity = new HttpEntity<>(chatRequest, headers);

            // Build the FastAPI URL
            String fastApiEndpoint = chatbotApiUrl + "/api/chat/";
            log.info("Calling FastAPI at: {}", fastApiEndpoint);

            // Make the POST request to FastAPI
            ResponseEntity<ChatResponse> responseEntity = restTemplate.exchange(
                    fastApiEndpoint,
                    HttpMethod.POST,
                    requestEntity,
                    ChatResponse.class
            );

            // Check if response is successful
            if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null) {
                ChatResponse response = responseEntity.getBody();
                response.setStatus("success");
                log.info("Received response from chatbot: {}", response.getResponse());
                return response;
            } else {
                log.error("Empty response from chatbot");
                return createErrorResponse("No response received from chatbot");
            }

        } catch (HttpClientErrorException e) {
            // Handle 4xx errors (client errors)
            log.error("Client error while calling chatbot API: {} - {}", e.getStatusCode(), e.getMessage());
            return createErrorResponse("Invalid request to chatbot: " + e.getMessage());

        } catch (HttpServerErrorException e) {
            // Handle 5xx errors (server errors)
            log.error("Server error while calling chatbot API: {} - {}", e.getStatusCode(), e.getMessage());
            return createErrorResponse("Chatbot service is temporarily unavailable");

        } catch (Exception e) {
            // Handle any other exceptions
            log.error("Unexpected error while calling chatbot API", e);
            return createErrorResponse("Failed to communicate with chatbot: " + e.getMessage());
        }
    }

    /**
     * Create an error response
     *
     * @param errorMessage the error message
     * @return ChatResponse with error details
     */
    private ChatResponse createErrorResponse(String errorMessage) {
        ChatResponse errorResponse = new ChatResponse();
        errorResponse.setResponse(errorMessage);
        errorResponse.setStatus("error");
        errorResponse.setData(null);
        return errorResponse;
    }

    /**
     * Check if chatbot service is available
     *
     * @return true if chatbot is reachable, false otherwise
     */
    public boolean isChatbotAvailable() {
        try {
            String healthEndpoint = chatbotApiUrl + "/health";
            ResponseEntity<String> response = restTemplate.getForEntity(healthEndpoint, String.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            log.error("Chatbot health check failed", e);
            return false;
        }
    }
}