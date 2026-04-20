package com.bintang.controller;

import com.bintang.entity.AttendanceLocation;
import com.bintang.repository.AttendanceLocationRepository;
import com.bintang.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String saveLocation(@ModelAttribute AttendanceLocation location, jakarta.servlet.http.HttpServletRequest request, RedirectAttributes redirectAttributes) {
        boolean isNew = (location.getId() == null);
        locationRepository.save(location);
        
        String actionType = isNew ? "CREATE_LOCATION" : "UPDATE_LOCATION";
        String actionMsg = isNew ? "Menambahkan" : "Mengubah";
        auditService.logWithContext(request, actionType, "AttendanceLocation", location.getId(), 
                actionMsg + " lokasi absensi: " + location.getName());
        
        redirectAttributes.addFlashAttribute("successMessage", "Lokasi absensi berhasil " + (isNew ? "disimpan" : "diperbarui") + "!");
        return "redirect:/settings/attendance-locations";
    }

    @PostMapping("/delete/{id}")
    public String deleteLocation(@PathVariable Long id, jakarta.servlet.http.HttpServletRequest request, RedirectAttributes redirectAttributes) {
        locationRepository.deleteById(id);
        auditService.logWithContext(request, "DELETE_LOCATION", "AttendanceLocation", id, "Menghapus lokasi absensi ID: " + id);
        redirectAttributes.addFlashAttribute("successMessage", "Lokasi absensi berhasil dihapus!");
        return "redirect:/settings/attendance-locations";
    }
}
