package com.vision.chatbot.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
// import org.springframework.web.context.request.RequestContextHolder;
// import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vision.chatbot.entities.Ai;
import com.vision.chatbot.repository.AiRepository;

// import jakarta.servlet.http.HttpSession;

@Service
public class ChatbotServiceImpl implements ChatbotService {

    // @Value("${gemini.api.key}")
    private String apiKey = "AIzaSyAlhH-lZ2TI8p4FbHEthGx9B8D2qTp1uH8";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AiRepository aiRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // @Autowired
    // private HttpSession session;

    private final String apiUrlTemplate = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=%s";

    // private HttpSession getSession() {
    // ServletRequestAttributes attr = (ServletRequestAttributes)
    // RequestContextHolder.currentRequestAttributes();
    // return attr.getRequest().getSession(true);
    // }

    @Override
    public String generateResponse(String prompt) {

        List<Ai> allAis = aiRepository.findAll();
        StringBuilder chatHistory = new StringBuilder();
        for (Ai tempAi : allAis) {
            chatHistory.append("User : " + tempAi.getPrompt() + ",");
            chatHistory.append("Bot : " + tempAi.getResponse() + "\n");
        }

        chatHistory.append("Prompt: "+prompt);
        System.out.println("CHat History: \n"+chatHistory);
        

        // System.out.println("chatfrom session: " + chatHistory);
        // if (chatHistory == null) {
        // chatHistory = new ArrayList<>();
        // session.setAttribute("chatHistory", chatHistory);
        // }

        // Adding the new prompt to the chat history
        // chatHistory.add( prompt);

        // Format the API URL with the actual API key
        String apiUrl = String.format(apiUrlTemplate, apiKey);

        // Updating the prompt with the new chat history
        // prompt = String.join("\n", chatHistory);
        // System.out.println("hellow djflds");
        // System.out.println(chatHistory);

        // Set up HTTP headers with the content type set to application/json
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        // Create the Json stucture for the request body
        ObjectNode contentNode = objectMapper.createObjectNode();
        ObjectNode partNode = objectMapper.createObjectNode();
        partNode.put("text", chatHistory.toString());
        contentNode.set("parts", objectMapper.createArrayNode().add(partNode));
        contentNode.put("role", "user");
        ObjectNode requestBodyNode = objectMapper.createObjectNode();
        requestBodyNode.set("contents", objectMapper.createArrayNode().add(contentNode));

        // convert the JSON object to a JSON string
        String requestBody;
        try {
            requestBody = objectMapper.writeValueAsString(requestBodyNode);
            // System.out.println(requestBody);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert request body to JSON", e);
        }

        // Create an HTTPEntity with the request body and headers
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        // System.out.println(requestEntity);

        ResponseEntity<String> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity,
                String.class);
        // System.out.println(responseEntity);

        String aiResponse = responseEntity.getBody();
        // System.out.println(aiResponse);

        try {
            JsonNode jsonNode = objectMapper.readTree(aiResponse);
            // System.out.println(jsonNode);

            String responseTest = jsonNode.get("candidates").get(0).get("content").get("parts").get(0).get("text")
                    .asText();

            // Add the AI response to the chat history
            // chatHistory.add("AI: " + responseTest);

            // Save the updated chat history
            // session.setAttribute("chatHistory", chatHistory);
            Ai ai = new Ai();
            ai.setPrompt(prompt);
            ai.setResponse(responseTest);
            aiRepository.save(ai);

            System.out.println(responseTest);

            return responseTest;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}