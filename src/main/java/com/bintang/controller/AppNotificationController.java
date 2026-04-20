package com.bintang.controller;

import com.bintang.entity.AppNotification;
import com.bintang.repository.AppNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/notifications")
public class AppNotificationController {

    @Autowired
    private AppNotificationRepository notificationRepository;

    @GetMapping("/read/{id}")
    public String markAsReadAndRedirect(@PathVariable Long id) {
        notificationRepository.findById(id).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
        
        AppNotification n = notificationRepository.findById(id).orElse(null);
        if (n != null && n.getLink() != null) {
            return "redirect:" + n.getLink();
        }
        
        return "redirect:/dashboard";
    }
}
