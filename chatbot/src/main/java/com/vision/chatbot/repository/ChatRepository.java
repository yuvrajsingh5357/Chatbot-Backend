package com.vision.chatbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.vision.chatbot.entities.Chat;

public interface ChatRepository extends JpaRepository<Chat, Integer> {
    
}
