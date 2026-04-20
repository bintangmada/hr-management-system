package com.bintang.repository;

import com.bintang.entity.AppNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AppNotificationRepository extends JpaRepository<AppNotification, Long> {
    List<AppNotification> findByTargetNikAndIsReadOrderByCreatedAtDesc(String targetNik, boolean isRead);
    List<AppNotification> findTop5ByTargetNikOrderByCreatedAtDesc(String targetNik);
}
