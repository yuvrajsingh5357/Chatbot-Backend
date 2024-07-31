package com.vision.chatbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vision.chatbot.entities.Ai;

@Repository
public interface AiRepository extends JpaRepository<Ai,Integer>{

    
    
}
