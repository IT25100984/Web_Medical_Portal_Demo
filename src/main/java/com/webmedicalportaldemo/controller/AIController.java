package com.webmedicalportaldemo.controller;

import com.webmedicalportaldemo.service.MedicalAIService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final MedicalAIService aiService;

    @Autowired
    public AIController(MedicalAIService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chatWithAssistant(
            @RequestBody Map<String, String> request,
            HttpSession session) {

        String userMessage = request.getOrDefault("message", "").trim();
        if (userMessage.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Message cannot be empty"));
        }

        // Session ID naturally separates guests, logged-in users, and different browsers/tabs
        String conversationId = session.getId();

        String aiResponse = aiService.generateResponse(userMessage, conversationId);
        return ResponseEntity.ok(Map.of("response", aiResponse));
    }
}