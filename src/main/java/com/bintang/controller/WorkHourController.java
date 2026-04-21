package com.bintang.controller;

import com.bintang.entity.AttendanceLocation;
import com.bintang.repository.AttendanceLocationRepository;
import com.bintang.service.SettingService;
import com.bintang.service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/settings/work-hours")
public class WorkHourController {

    @Autowired
    private SettingService settingService;

    @Autowired
    private AttendanceLocationRepository locationRepository;

    @Autowired
    private AuditService auditService;

    @GetMapping
    public String workHourSettings(Model model) {
        model.addAttribute("globalMinCheckIn", settingService.getSettingValue("MIN_CHECKIN_TIME", "07:00"));
        model.addAttribute("globalMinCheckOut", settingService.getSettingValue("MIN_CHECKOUT_TIME", "16:00"));
        model.addAttribute("locations", locationRepository.findAll());
        model.addAttribute("content", "settings/work-hours");
        return "layout";
    }

    @PostMapping("/global")
    public String saveGlobalWorkHours(
            @RequestParam String minCheckIn,
            @RequestParam String minCheckOut,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        
        settingService.updateSetting("MIN_CHECKIN_TIME", minCheckIn, "Jam minimal check-in standar perusahaan");
        settingService.updateSetting("MIN_CHECKOUT_TIME", minCheckOut, "Jam minimal check-out standar perusahaan");
        
        auditService.logWithContext(request, "UPDATE_SETTING_GLOBAL", "Setting", null, "Mengubah jam kerja global: " + minCheckIn + " - " + minCheckOut);
        redirectAttributes.addFlashAttribute("successMessage", "Jam kerja global berhasil diperbarui!");
        
        return "redirect:/settings/work-hours";
    }

    @PostMapping("/location")
    public String saveLocationWorkHours(
            @RequestParam Long locationId,
            @RequestParam String minCheckIn,
            @RequestParam String minCheckOut,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        
        AttendanceLocation loc = locationRepository.findById(locationId).orElseThrow();
        loc.setMinCheckInTime(minCheckIn);
        loc.setMinCheckOutTime(minCheckOut);
        locationRepository.save(loc);
        
        auditService.logWithContext(request, "UPDATE_SETTING_LOCATION", "AttendanceLocation", locationId, 
                "Mengubah jam kerja lokasi " + loc.getName() + ": " + minCheckIn + " - " + minCheckOut);
        
        redirectAttributes.addFlashAttribute("successMessage", "Jam kerja lokasi " + loc.getName() + " berhasil diperbarui!");
        
        return "redirect:/settings/work-hours";
    }
}
