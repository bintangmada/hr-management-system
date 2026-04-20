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

@Entity
@Table(name = "performance_evaluations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Performance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long employeeId; // Manual relationship
    private Long evaluatorId; // Manual relationship to Employee
    private LocalDate reviewDate;
    private Integer score; // e.g., 1-5 or 1-100
    private String comments;
    private String period; // e.g., "Q1-2026"
}
