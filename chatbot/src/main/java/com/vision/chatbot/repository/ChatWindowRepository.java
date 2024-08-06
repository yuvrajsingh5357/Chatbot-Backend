package com.vision.chatbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.vision.chatbot.entities.ChatWindow;

public interface ChatWindowRepository extends JpaRepository<ChatWindow, Integer> {
}
