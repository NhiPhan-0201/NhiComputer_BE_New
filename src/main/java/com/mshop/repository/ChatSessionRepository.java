package com.mshop.repository;

import com.mshop.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {
    List<ChatSession> findByStatus(String status);
    List<ChatSession> findByUser_UserId(Long userId);
}