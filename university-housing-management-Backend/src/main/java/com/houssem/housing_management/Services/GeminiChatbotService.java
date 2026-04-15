package com.houssem.housing_management.Services;


import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeminiChatbotService {

    private final Dotenv dotenv;
    private String apiKey;

    @PostConstruct
    public void init() {
        this.apiKey = dotenv.get("GEMINI_API_KEY");

        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("GEMINI_API_KEY is not set in .env file");
        }
    }

    public String askQuestion(String userQuestion, String contextData) {

        String systemPrompt = """
            Tu es l'assistant virtuel du service de logement universitaire.
            Ton rôle est d'aider les étudiants à trouver le logement qui leur correspond le mieux.
            Sois empathique, rassurant et donne des conseils personnalisés.

            Voici les données actuelles des résidences :

            %s

            Si l'information n'existe pas, dis-le poliment et propose de contacter le service du logement.
            """.formatted(contextData);

        String fullPrompt = systemPrompt + "\n\nQuestion : " + userQuestion;

        List<String> models = List.of(
                "gemini-2.5-flash",
                "gemini-2.0-flash",
                "gemini-2.5-pro"
        );

        for (String model : models) {
            try {
                return callGemini(model, fullPrompt);
            } catch (Exception e) {
                System.out.println("Model failed: " + model + " → " + e.getMessage());
            }
        }

        return "Le service IA est temporairement indisponible. Veuillez réessayer plus tard.";
    }

    private String callGemini(String model, String prompt) {

        String url = "https://generativelanguage.googleapis.com/v1/models/"
                + model
                + ":generateContent?key="
                + apiKey;

        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> text = new HashMap<>();
        text.put("text", prompt);

        Map<String, Object> parts = new HashMap<>();
        parts.put("parts", List.of(text));

        Map<String, Object> body = new HashMap<>();
        body.put("contents", List.of(parts));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(url, request, Map.class);

        List candidates = (List) response.getBody().get("candidates");
        Map first = (Map) candidates.get(0);
        Map content = (Map) first.get("content");
        List partsList = (List) content.get("parts");
        Map textMap = (Map) partsList.get(0);

        return textMap.get("text").toString();
    }
}