package com.mshop.service;

import com.mshop.entity.Feedback;

import java.util.List;

public interface FeedbackService {
    Feedback createFeedback(Long userId, String content);
    List<Feedback> getAllFeedback();
    Feedback updateStatus(Long feedbackId, Boolean status);
    List<Feedback> getFeedbackByUser(Long userId);
    void deleteFeedbackOfUser(Long feedbackId, Long userId);
}