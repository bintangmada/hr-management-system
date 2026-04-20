package com.bintang.controller;

import com.bintang.service.SettingService;
import com.bintang.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/settings")
public class SettingController {

    @Autowired
    private SettingService settingService;
    
    @Autowired
    private AuditService auditService;

    @GetMapping("/attendance")
    public String attendanceSettings(Model model) {
        model.addAttribute("officePolygon", settingService.getSettingValue("OFFICE_POLYGON", "[]"));
        model.addAttribute("officeRadius", settingService.getSettingValue("OFFICE_RADIUS", "100"));
        model.addAttribute("content", "settings/attendance");
        return "layout";
    }

    @PostMapping("/attendance")
    public String saveAttendanceSettings(
            @RequestParam String officePolygon,
            @RequestParam String officeRadius) {
        
        settingService.updateSetting("OFFICE_POLYGON", officePolygon, "Area polygon lokasi kantor (GeoJSON/Points)");
        settingService.updateSetting("OFFICE_RADIUS", officeRadius, "Radius kehadiran dalam meter");
        
        auditService.log("UPDATE_SETTING", "Admin", "Setting", null, "Mengubah pengaturan geofencing");
        
        return "redirect:/settings/attendance?success";
    }
}
