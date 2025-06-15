package com.mshop.service;

import com.mshop.entity.Product;

import com.mshop.entity.ChatResponse;
import com.mshop.repository.ProductResository;
import com.mshop.repository.ChatResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ChatService {

    @Autowired
    private ProductResository productRepository;

    @Autowired
    private ChatResponseRepository chatbotResponseRepository;

    @Autowired
    private GeminiChatService geminiChatService;

    public String getBestResponse(String userInput) {
        // 1. Tìm sản phẩm theo tên trong câu hỏi
        List<Product> products = productRepository.findByNameContainingIgnoreCase(userInput);
        if (!products.isEmpty()) {
            Product p = products.get(0);
            return String.format("Sản phẩm %s hiện có giá %,.0f VND. Mô tả: %s", p.getName(), p.getPrice(), p.getDescription());
        }

        // 2. Tìm câu trả lời mẫu (FAQ)
        List<ChatResponse> responses = chatbotResponseRepository.findMatchingResponses(userInput);
        if (!responses.isEmpty()) {
            String botAnswer = responses.get(0).getResponse();

            // 3. Lọc tên sản phẩm từ câu trả lời của bot (ví dụ: lấy tên sau từ "Laptop Gaming Acer Nitro 5...")
            String productName = extractProductName(botAnswer);
            if (productName != null && !productName.isEmpty()) {
                List<Product> found = productRepository.findByNameContainingIgnoreCase(productName);
                if (!found.isEmpty()) {
                    Product p = found.get(0);
                    return String.format("Sản phẩm %s hiện có giá %,.0f VND. Mô tả: %s", p.getName(), p.getPrice(), p.getDescription());
                }
            }
            // Nếu không tìm thấy sản phẩm, trả về câu trả lời mẫu
            return botAnswer;
        }

        // 4. Nếu không có, gọi Gemini
        return geminiChatService.chatWithGemini(userInput);
    }

    // Hàm tách tên sản phẩm từ câu trả lời mẫu (ví dụ đơn giản, bạn có thể cải tiến)
    private String extractProductName(String botAnswer) {
        // Ví dụ: lấy tên sau từ "Laptop Gaming Acer Nitro 5: 23,791,500 VND"
        Pattern pattern = Pattern.compile("([A-Za-z0-9\\s]+)[:\\-]");
        Matcher matcher = pattern.matcher(botAnswer);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }
}