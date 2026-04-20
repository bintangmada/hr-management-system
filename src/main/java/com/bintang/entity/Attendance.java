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
@Table(name = "attendance")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long employeeId; // Manual relationship
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private String status; // e.g., PRESENT, LATE, ABSENT
    private Double latitude;
    private Double longitude;
    private Boolean isWithinGeo;
    private Boolean isLate;
}
