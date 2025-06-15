package com.mshop.restapi;

import com.mshop.entity.MailInfo;
import com.mshop.service.SendMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/mail")
@CrossOrigin("*")
public class SendMailController {
    @Autowired
    SendMailService sendMailService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> sendMail(@RequestBody MailInfo mail) {
        Map<String, Object> response = new HashMap<>();
        try {
            sendMailService.send(mail);
            response.put("success", true);
            response.put("message", "Gửi mail thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Gửi mail thất bại: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}