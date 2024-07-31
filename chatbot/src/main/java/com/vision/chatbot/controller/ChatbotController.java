package com.vision.chatbot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.vision.chatbot.service.ChatbotService;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;

    // @CrossOrigin(origins = "http://127.0.0.1:5500")
    @CrossOrigin(origins = { "http://localhost:5173", "http://127.0.0.1:5500" })
    @PostMapping("/generate-response")
    public String postMethodName(@RequestParam String prompt) {
        System.out.println("Helloo from controller");
        // String prompt = "hello";

        String response = chatbotService.generateResponse(prompt);
        
        return (response != null) ? response  : "no response";
    }

}
