package com.greenpulsespring.greenpulsespring.controller;

import com.greenpulsespring.greenpulsespring.service.OllamaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private OllamaService ollamaService;

    @PostMapping
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> body) {
        String userMessage = body.get("message");
        String reply = ollamaService.getChatResponse(userMessage);
        return ResponseEntity.ok(Map.of("reply", reply));
    }
}
