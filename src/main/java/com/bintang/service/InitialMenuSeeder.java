package com.bintang.service;

import com.bintang.entity.AppMenu;
import com.bintang.repository.AppMenuRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Arrays;

@Service
public class InitialMenuSeeder {

    @Autowired
    private AppMenuRepository menuRepository;

    @PostConstruct
    public void seedMenus() {
        if (menuRepository.count() > 0) return;

        // 1. Dashboard
        AppMenu dashboard = createMenu("Dashboard", "fas fa-chart-line", "/dashboard", "ALL", null, 1);
        
        // 2. Karyawan
        createMenu("Karyawan", "fas fa-user-friends", "/employees", "ADMIN", null, 2);
        
        // 3. Payroll
        createMenu("Payroll", "fas fa-money-check-alt", "/payroll", "ADMIN", null, 3);
        
        // 4. Performa
        createMenu("Performa", "fas fa-star", "/performance", "ADMIN", null, 4);
        
        // 5. Master Data (Parent)
        AppMenu masterData = createMenu("Master Data", "fas fa-database", "#", "ADMIN", null, 5);
        createMenu("Regions", null, "/settings/master-data/regions", "ADMIN", masterData.getId(), 1);
        createMenu("Jabatan", null, "/settings/master-data/jobs", "ADMIN", masterData.getId(), 2);
        createMenu("Departemen", null, "/settings/master-data/departments", "ADMIN", masterData.getId(), 3);
        createMenu("Cabang (Lokasi)", null, "/settings/master-data/locations", "ADMIN", masterData.getId(), 4);
        
        // 6. Lokasi Absen
        createMenu("Lokasi Absen", "fas fa-map-marked-alt", "/settings/attendance-locations", "ADMIN", null, 6);
        
        // 7. Audit Logs
        createMenu("Audit Logs", "fas fa-history", "/admin/audit-logs", "ADMIN", null, 7);
        
        // 8. Manajemen Menu (New)
        createMenu("Manajemen Menu", "fas fa-list-ul", "/settings/menu-management", "ADMIN", null, 8);

        // 9. Absensi Digital
        createMenu("Absensi Digital", "fas fa-fingerprint", "/attendance", "ALL", null, 9);
    }

    private AppMenu createMenu(String title, String icon, String url, String role, Long parentId, Integer order) {
        AppMenu menu = new AppMenu();
        menu.setTitle(title);
        menu.setIcon(icon);
        menu.setUrl(url);
        menu.setRoleRequired(role);
        menu.setParentId(parentId);
        menu.setSortOrder(order);
        menu.setIsActive(true);
        return menuRepository.save(menu);
    }
}
