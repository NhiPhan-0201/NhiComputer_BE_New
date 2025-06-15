package com.mshop.service;

import com.mshop.entity.Feedback;
import com.mshop.entity.User;
import com.mshop.repository.FeedbackRepository;
import com.mshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FeedbackServiceImpl implements FeedbackService {
    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Feedback createFeedback(Long userId, String content) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) throw new RuntimeException("User not found");
        Feedback feedback = new Feedback();
        feedback.setUser(userOpt.get());
        feedback.setContent(content);
        return feedbackRepository.save(feedback);
    }

    @Override
    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAll();
    }

    @Override
    public Feedback updateStatus(Long feedbackId, Boolean status) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));
        feedback.setStatus(status);
        return feedbackRepository.save(feedback);
    }
    @Override
    public List<Feedback> getFeedbackByUser(Long userId) {
        return feedbackRepository.findByUser_UserId(userId);
    }

    @Override
    public void deleteFeedbackOfUser(Long feedbackId, Long userId) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));
        if (!feedback.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("Không thể xóa góp ý không thuộc về bạn!");
        }
        feedbackRepository.deleteById(feedbackId);
    }
}
