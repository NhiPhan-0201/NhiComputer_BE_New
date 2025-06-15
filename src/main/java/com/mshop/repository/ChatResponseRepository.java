package com.mshop.repository;

import com.mshop.entity.ChatResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface ChatResponseRepository extends JpaRepository<ChatResponse, Long> {

    // Tìm tất cả các phản hồi có keyword xuất hiện trong câu hỏi (không phân biệt hoa thường)
    @Query("SELECT r FROM ChatResponse r WHERE LOWER(:input) LIKE CONCAT('%', LOWER(r.keyword), '%') OR LOWER(r.keyword) LIKE CONCAT('%', LOWER(:input), '%')")
    List<ChatResponse> findMatchingResponses(@Param("input") String input);

}
