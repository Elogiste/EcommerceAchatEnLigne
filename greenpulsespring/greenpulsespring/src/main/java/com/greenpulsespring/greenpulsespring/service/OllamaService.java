package com.greenpulsespring.greenpulsespring.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import java.util.List;
import java.util.Map;

@Service
public class OllamaService {

    private final WebClient webClient;

    @Value("${ollama.model}")
    private String model;

    public OllamaService(@Value("${ollama.api.url}") String apiUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .build();
    }

    public String getChatResponse(String userMessage) {
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(Map.of("role", "user", "content", userMessage)),
                "stream", false // important si tu veux éviter le NDJSON et obtenir une réponse unique
        );

        try {
            Map response = webClient.post()
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            Map<String, Object> message = (Map<String, Object>) response.get("message");
            return message != null ? message.get("content").toString() : "Aucune réponse reçue.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors de la communication avec Ollama.";
        }
    }
}
