package com.bintang.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "app_notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String targetNik;
    private String title;
    private String message;
    private String link;
    private boolean isRead = false;
    
    private LocalDateTime createdAt = LocalDateTime.now();

    public AppNotification(String targetNik, String title, String message, String link) {
        this.targetNik = targetNik;
        this.title = title;
        this.message = message;
        this.link = link;
    }
}
