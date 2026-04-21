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
    
    @jakarta.persistence.ManyToOne
    @jakarta.persistence.JoinColumn(name = "employeeId", insertable = false, updatable = false)
    private Employee employee;

    private Long employeeId; 
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private String status; // e.g., PRESENT, LATE, ABSENT
    private Double latitude;
    private Double longitude;
    private Boolean isWithinGeo;
    private Boolean isLate;
    private String checkInDetail;
    private String checkOutDetail;
    private Boolean isEarlyLeave;
}
