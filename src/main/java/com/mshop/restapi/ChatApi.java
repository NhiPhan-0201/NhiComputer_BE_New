package com.mshop.restapi;

import com.mshop.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/v1/chatbot")
public class ChatApi {

    @Autowired
    private ChatService chatService;

    @GetMapping("/{keyword}")
    public ResponseEntity<?> getResponse(@PathVariable String keyword) {
        String response = chatService.getBestResponse(keyword);
        return ResponseEntity.ok().body(new java.util.HashMap<String, String>() {{
            put("response", response);
        }});
    }
}