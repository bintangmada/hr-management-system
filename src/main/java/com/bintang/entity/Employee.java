package com.bintang.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String nik;
    
    private String firstName;
    private String lastName;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    private String phoneNumber;
    private LocalDate hireDate;
    
    @ManyToOne
    @JoinColumn(name = "jobId", insertable = false, updatable = false)
    private Job job;

    @Column(name = "jobId")
    private String jobId;

    private Double salary;
    private Double commissionPct;
    private Long managerId; // Manual relationship to self

    @ManyToOne
    @JoinColumn(name = "departmentId", insertable = false, updatable = false)
    private Department department;

    @Column(name = "departmentId")
    private Long departmentId;
    
    private String password;
    private String role; // ADMIN or EMPLOYEE
    private Boolean isActive = true;
}
