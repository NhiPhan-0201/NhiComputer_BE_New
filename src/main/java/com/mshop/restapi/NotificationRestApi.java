package com.mshop.restapi;

import com.mshop.entity.Notification;
import com.mshop.repository.NotificationRepository;
import com.mshop.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
@CrossOrigin("*")
@RestController
@RequestMapping("/api/notifications")
public class NotificationRestApi {
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationService notificationService;

    // Lấy danh sách thông báo của user
    @GetMapping("/{userId}")
    public List<Notification> getNotificationsByUser(@PathVariable Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // Tạo thông báo mới (có thể dùng cho test hoặc các sự kiện khác)
    @PostMapping("/")
    public Notification createNotification(@RequestBody Notification noti) {
        return notificationService.createNotification(noti.getUserId(), noti.getOrderId(), noti.getMessage());
    }

    // Đánh dấu đã đọc
    // NotificationRestApi.java
    @CrossOrigin(origins = "http://localhost:4200")
    @Transactional
    @PatchMapping("/read/{id}")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        Optional<Notification> optional = notificationRepository.findById(id);
        if (optional.isEmpty()) {
            System.out.println("Notification not found: " + id);
            return ResponseEntity.notFound().build();
        }

        Notification notification = optional.get();
        notification.setStatus(1);
        notificationRepository.save(notification);
        System.out.println("Notification đã đánh dấu đã đọc: " + id);

        return ResponseEntity.ok().build();
    }



}