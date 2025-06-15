package com.mshop.service;

import com.mshop.entity.Notification;
import com.mshop.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    public Notification createNotification(Long userId, Long orderId, String message) {
        Notification noti = new Notification();
        noti.setUserId(userId);
        noti.setOrderId(orderId);
        noti.setMessage(message);
        noti.setStatus(0); // chưa đọc
        noti.setCreatedAt(new Date());
        System.out.println("Tạo notification cho userId=" + userId + ", orderId=" + orderId + ", message=" + message);
        return notificationRepository.save(noti);
    }

    @Transactional
    public Notification markAsRead(Long id) {
        Notification noti = notificationRepository.findById(id).orElse(null);
        if (noti != null) {
            noti.setStatus(0);
            notificationRepository.saveAndFlush(noti); // dùng saveAndFlush để chắc chắn ghi xuống DB
        }
        return noti;
    }
}