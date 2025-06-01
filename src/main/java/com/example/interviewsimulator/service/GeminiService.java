package com.example.interviewsimulator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    public String askGemini(String userInput) {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> request = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", userInput)))
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        String fullUrl = apiUrl + "?key=" + apiKey;
        ResponseEntity<String> response = restTemplate.postForEntity(fullUrl, entity, String.class);

        // Extract answer text
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            return root
                    .path("candidates").get(0)
                    .path("content")
                    .path("parts").get(0)
                    .path("text")
                    .asText();
        } catch (Exception e) {
            return "Error parsing Gemini response.";
        }
    }
}
