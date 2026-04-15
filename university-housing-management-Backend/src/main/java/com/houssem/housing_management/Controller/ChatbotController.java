package com.houssem.housing_management.Controller;



import com.houssem.housing_management.Services.GeminiChatbotService;
import com.houssem.housing_management.Services.HousingContextService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")

@RequiredArgsConstructor
public class ChatbotController {

    private final GeminiChatbotService geminiService;
    private final HousingContextService contextService;

    @PostMapping("/ask")
    public Map<String, String> askChatbot(@RequestBody Map<String, String> payload) {
        String userMessage = payload.get("message");

        // 1. Récupère le contexte (données réelles de la base)
        String contextData = contextService.buildContextForChatbot();

        // 2. Envoie la question et le contexte à Gemini
        String aiResponse = geminiService.askQuestion(userMessage, contextData);

        // 3. Retourne la réponse au frontend
        return Map.of("response", aiResponse);
    }
}