package com.vision.chatbot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;



@Configuration
public class ChatbotConfig {

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
    

    // @Bean
    // public HttpSession getHttpSession() {
    //     return new HttpSession();
    // }


}
