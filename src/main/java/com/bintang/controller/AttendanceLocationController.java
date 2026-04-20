package com.bintang.controller;

import com.bintang.entity.AttendanceLocation;
import com.bintang.repository.AttendanceLocationRepository;
import com.bintang.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/settings/attendance-locations")
public class AttendanceLocationController {

    @Autowired
    private AttendanceLocationRepository locationRepository;
    
    @Autowired
    private AuditService auditService;

    @GetMapping
    public String listLocations(Model model) {
        model.addAttribute("locations", locationRepository.findAll());
        model.addAttribute("content", "settings/locations");
        return "layout";
    }

    @PostMapping
    public String saveLocation(@ModelAttribute AttendanceLocation location) {
        boolean isNew = (location.getId() == null);
        locationRepository.save(location);
        
        String action = isNew ? "CREATE_LOCATION" : "UPDATE_LOCATION";
        auditService.log(action, "Admin", "AttendanceLocation", location.getId(), 
                "Menambahkan/Mengubah lokasi absensi: " + location.getName());
        
        return "redirect:/settings/attendance-locations?success";
    }

    @PostMapping("/delete/{id}")
    public String deleteLocation(@PathVariable Long id) {
        locationRepository.deleteById(id);
        auditService.log("DELETE_LOCATION", "Admin", "AttendanceLocation", id, "Menghapus lokasi absensi ID: " + id);
        return "redirect:/settings/attendance-locations?deleted";
    }
}
