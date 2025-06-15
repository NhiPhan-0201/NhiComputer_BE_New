package com.mshop.repository;

import com.mshop.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    // Có thể thêm các query method nếu cần
    List<Feedback> findByUser_UserId(Long userId);
}