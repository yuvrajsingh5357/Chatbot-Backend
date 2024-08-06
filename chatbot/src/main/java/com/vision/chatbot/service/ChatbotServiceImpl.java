package com.vision.chatbot.service;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vision.chatbot.entities.Chat;
import com.vision.chatbot.entities.ChatWindow;
import com.vision.chatbot.repository.ChatRepository;
import com.vision.chatbot.repository.ChatWindowRepository;

@Service
public class ChatbotServiceImpl implements ChatbotService {

    private String apiKey = "AIzaSyAlhH-lZ2TI8p4FbHEthGx9B8D2qTp1uH8";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private ChatWindowRepository chatWindowRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private final String apiUrlTemplate = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=%s";

/**
 * Generates a response based on the given prompt by sending a request to an API.
 * Retrieves chat history, creates a request entity, sends a POST request to the API, and processes the API response.
 *
 * @param prompt the prompt for generating the response
 * @return the response generated based on the prompt
 */
@Override
public String generateResponse(String prompt) {
    String chatHistory = getChatHistory(prompt);
    String apiUrl = String.format(apiUrlTemplate, apiKey);
    HttpEntity<String> requestEntity = createRequestEntity(chatHistory);

    ResponseEntity<String> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, String.class);

    return processApiResponse(prompt, responseEntity.getBody());
}

/**
 * Retrieves the chat history from all chat windows and appends the given prompt.
 *
 * @param prompt The prompt to be appended at the end of the chat history.
 * @return A string representing the entire chat history including the given prompt.
 */
private String getChatHistory(String prompt) {
    List<ChatWindow> allChatWindows = chatWindowRepository.findAll();
    StringBuilder chatHistory = new StringBuilder();
    for (ChatWindow tempChatWindow : allChatWindows) {
        chatHistory.append("User : ").append(tempChatWindow.getPrompt()).append(",");
        chatHistory.append("Bot : ").append(tempChatWindow.getResponse()).append("\n");
    }
    chatHistory.append("Prompt: ").append(prompt);
    System.out.println("Chat History: \n" + chatHistory);
    return chatHistory.toString();
}

/**
 * Creates an HttpEntity with a JSON request body and appropriate headers.
 *
 * @param chatHistory The chat history to be included in the request body.
 * @return An HttpEntity containing the JSON request body and headers.
 * @throws RuntimeException if there is an error converting the request body to JSON.
 */
private HttpEntity<String> createRequestEntity(String chatHistory) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", "application/json");

    ObjectNode contentNode = objectMapper.createObjectNode();
    ObjectNode partNode = objectMapper.createObjectNode();
    partNode.put("text", chatHistory);
    contentNode.set("parts", objectMapper.createArrayNode().add(partNode));
    contentNode.put("role", "user");

    ObjectNode requestBodyNode = objectMapper.createObjectNode();
    requestBodyNode.set("contents", objectMapper.createArrayNode().add(contentNode));

    String requestBody;
    try {
        requestBody = objectMapper.writeValueAsString(requestBodyNode);
    } catch (Exception e) {
        throw new RuntimeException("Failed to convert request body to JSON", e);
    }

    return new HttpEntity<>(requestBody, headers);
}

/**
 * Processes the API response by extracting the relevant text and saving the chat history.
 *
 * @param prompt The prompt that was sent to the API.
 * @param apiResponse The response received from the API in JSON format.
 * @return The extracted text from the API response, or null if an exception occurs.
 */
private String processApiResponse(String prompt, String apiResponse) {
    try {
        JsonNode jsonNode = objectMapper.readTree(apiResponse);
        String responseText = jsonNode.get("candidates").get(0).get("content").get("parts").get(0).get("text").asText();

        saveChatHistory(prompt, responseText);

        System.out.println(responseText);

        return responseText;
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}

/**
 * Saves the chat history by creating a new ChatWindow entry and associating it with a Chat.
 *
 * @param prompt       The prompt text from the chat.
 * @param responseText The response text from the chat.
 */
private void saveChatHistory(String prompt, String responseText) {
    // Retrieve the Chat with ID 1 or create a new one if it doesn't exist
    Chat chat = chatRepository.findById(1).orElse(new Chat());  // Assuming there's always a Chat with ID 1
    
    // Create a new ChatWindow and set its properties
    ChatWindow chatWindow = new ChatWindow();
    chatWindow.setPrompt(prompt);
    chatWindow.setResponse(responseText);
    chatWindow.setTimestamp(new Timestamp(System.currentTimeMillis()));
    chatWindow.setChat(chat);
    
    // Save the new ChatWindow to the repository
    chatWindowRepository.save(chatWindow);

    // If the Chat is new (ID is 0), add the ChatWindow to it and save the Chat
    if (chat.getId() == 0) {
        chat.getChatWindows().add(chatWindow);
        chatRepository.save(chat);
    }
}
}
