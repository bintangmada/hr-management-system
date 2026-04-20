package com.bintang.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String action; // e.g., CREATE, UPDATE, DELETE
    private String performedBy; // Legacy fallback
    
    private String actorRole;
    private String actorName;
    private String actorNik;
    
    private String entityName;
    private Long entityId;
    private String details;
    private LocalDateTime createdAt;
}
