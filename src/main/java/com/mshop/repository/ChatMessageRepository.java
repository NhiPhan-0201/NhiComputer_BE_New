package com.mshop.repository;

import com.mshop.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findBySession_SessionIdOrderBySentAtAsc(Long sessionId);
}