package com.mshop.restapi;

import com.mshop.entity.Feedback;
import com.mshop.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:4201"})
@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;

    @PostMapping
    public Feedback createFeedback(@RequestParam Long userId, @RequestParam String content) {
        return feedbackService.createFeedback(userId, content);
    }

    @GetMapping
    public List<Feedback> getAllFeedback() {
        return feedbackService.getAllFeedback();
    }

    @PutMapping("/{id}/status")
    public Feedback updateStatus(@PathVariable("id") Long feedbackId, @RequestParam Boolean status) {
        return feedbackService.updateStatus(feedbackId, status);
    }
    @GetMapping("/user/{userId}")
    public List<Feedback> getFeedbackByUser(@PathVariable Long userId) {
        return feedbackService.getFeedbackByUser(userId);
    }

    // API xóa góp ý (chỉ cho phép user xóa góp ý của mình)
    @DeleteMapping("/{id}")
    public void deleteFeedback(@PathVariable("id") Long feedbackId, @RequestParam Long userId) {
        feedbackService.deleteFeedbackOfUser(feedbackId, userId);
    }
}