package com.vision.chatbot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.vision.chatbot.service.ChatbotService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;
    
    @PostMapping("/generate-response")
    public String postMethodName(@RequestParam String prompt) {
 
        String response = chatbotService.generateResponse(prompt);  
        
        return (response != null) ? response  : "no response";
    }

}
