package com.bintang.service;

import com.bintang.entity.AppNotification;
import com.bintang.repository.AppNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppNotificationService {

    @Autowired
    private AppNotificationRepository notificationRepository;

    public void send(String targetNik, String title, String message, String link) {
        AppNotification notification = new AppNotification(targetNik, title, message, link);
        notificationRepository.save(notification);
    }

    public List<AppNotification> getUnread(String nik) {
        return notificationRepository.findByTargetNikAndIsReadOrderByCreatedAtDesc(nik, false);
    }

    public void markAsRead(Long id) {
        notificationRepository.findById(id).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }
}
