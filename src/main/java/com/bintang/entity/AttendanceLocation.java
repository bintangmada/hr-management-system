package com.bintang.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "attendance_locations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String polygonJson;
    
    private Integer radius;
    
    private String minCheckInTime;
    private String minCheckOutTime;
    private String targetStartTime;
    
    private String description;
}
