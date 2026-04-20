package com.bintang.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "leave_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long employeeId; // Relationship to Employee
    
    private String leaveType; // e.g., ANNUAL, SICK, SPECIAL
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    
    // PENDING, APPROVED, REJECTED
    private String status = "PENDING";
    
    private String adminComment;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;

    @jakarta.persistence.Transient
    public Long getDurationDays() {
        if (startDate != null && endDate != null) {
            return java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        }
        return 0L;
    }
}
