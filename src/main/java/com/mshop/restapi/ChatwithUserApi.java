package com.mshop.restapi;

import com.mshop.entity.ChatMessage;
import com.mshop.entity.ChatSession;
import com.mshop.entity.User;
import com.mshop.repository.ChatMessageRepository;
import com.mshop.repository.ChatSessionRepository;
import com.mshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatwithUserApi {

    @Autowired
    private ChatSessionRepository chatSessionRepo;

    @Autowired
    private ChatMessageRepository chatMessageRepo;

    @Autowired
    private UserRepository userRepo;

    // Tạo phiên chat mới (user bắt đầu chat)
    @PostMapping("/start")
    public ChatSession startChat(@RequestParam Long userId) {
        User user = userRepo.findById(userId).orElse(null);
        if (user == null) throw new RuntimeException("User not found");
        ChatSession session = new ChatSession();
        session.setUser(user);
        session.setStartTime(new Date());
        session.setStatus("active");
        return chatSessionRepo.save(session);
    }

    // Lấy tất cả phiên chat (cho admin quản lý)
    @GetMapping("/sessions")
    public List<ChatSession> getAllSessions() {
        return chatSessionRepo.findAll();
    }

    // Lấy phiên chat của 1 user
    @GetMapping("/user/{userId}/sessions")
    public List<ChatSession> getUserSessions(@PathVariable Long userId) {
        return chatSessionRepo.findByUser_UserId(userId);
    }

    // Admin join vào một session
    @PostMapping("/session/{sessionId}/join")
    public ChatSession joinSession(@PathVariable Long sessionId, @RequestParam Long adminId) {
        ChatSession session = chatSessionRepo.findById(sessionId).orElse(null);
        User admin = userRepo.findById(adminId).orElse(null);
        if (session == null || admin == null) throw new RuntimeException("Not found");
        session.setAdmin(admin);
        return chatSessionRepo.save(session);
    }

    // Gửi tin nhắn trong phiên chat
    @PostMapping("/session/{sessionId}/send")
    public ChatMessage sendMessage(
            @PathVariable Long sessionId,
            @RequestParam Long senderId,
            @RequestBody String message
    ) {
        ChatSession session = chatSessionRepo.findById(sessionId).orElse(null);
        User sender = userRepo.findById(senderId).orElse(null);
        if (session == null || sender == null) throw new RuntimeException("Not found");
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSession(session);
        chatMessage.setSender(sender);
        chatMessage.setMessage(message);
        chatMessage.setSentAt(new Date());
        chatMessage.setIsRead(false);
        return chatMessageRepo.save(chatMessage);
    }

    // Lấy lịch sử tin nhắn của 1 phiên chat
    @GetMapping("/session/{sessionId}/messages")
    public List<ChatMessage> getSessionMessages(@PathVariable Long sessionId) {
        return chatMessageRepo.findBySession_SessionIdOrderBySentAtAsc(sessionId);
    }

    // Đóng phiên chat
    @PostMapping("/session/{sessionId}/close")
    public ChatSession closeSession(@PathVariable Long sessionId) {
        ChatSession session = chatSessionRepo.findById(sessionId).orElse(null);
        if (session == null) throw new RuntimeException("Session not found");
        session.setStatus("closed");
        session.setEndTime(new Date());
        return chatSessionRepo.save(session);
    }
}