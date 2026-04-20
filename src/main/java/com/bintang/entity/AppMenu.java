package com.bintang.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "app_menus")
@Data
public class AppMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String icon;
    private String url;
    
    // ADMIN, EMPLOYEE, or ALL
    private String roleRequired;
    
    private Long parentId;
    
    private Integer sortOrder;
    
    private Boolean isActive = true;

    private String permittedNiks; // Comma separated NIKs
}
