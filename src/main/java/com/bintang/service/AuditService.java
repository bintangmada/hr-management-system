package com.bintang.service;

import com.bintang.entity.AuditLog;
import com.bintang.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class AuditService {
    
    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private com.bintang.repository.EmployeeRepository employeeRepository;

    public void logWithContext(jakarta.servlet.http.HttpServletRequest request, String action, String entityName, Long entityId, String details) {
        Long employeeId = (Long) request.getAttribute("employeeId");
        String role = (String) request.getAttribute("role");
        if (employeeId != null) {
            employeeRepository.findById(employeeId).ifPresent(emp -> {
                log(action, role, emp.getFirstName() + " " + emp.getLastName(), emp.getNik(), entityName, entityId, details);
            });
        } else {
            log(action, role != null ? role : "System", "Unknown", "N/A", entityName, entityId, details);
        }
    }

    public void log(String action, String actorRole, String actorName, String actorNik, String entityName, Long entityId, String details) {
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setActorRole(actorRole);
        log.setActorName(actorName);
        log.setActorNik(actorNik);
        log.setEntityName(entityName);
        log.setEntityId(entityId);
        log.setDetails(details);
        log.setCreatedAt(LocalDateTime.now());
        auditLogRepository.save(log);
    }
    
    // Fallback for old calls while migrating
    public void log(String action, String performedBy, String entityName, Long entityId, String details) {
        log(action, "Unknown", performedBy, "N/A", entityName, entityId, details);
    }
}
