package com.bintang.controller;

import com.bintang.entity.Employee;
import com.bintang.repository.EmployeeRepository;
import com.bintang.service.AttendanceService;
import com.bintang.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private SettingService settingService;

    @GetMapping
    public String attendancePage(Model model) {
        model.addAttribute("officePolygon", settingService.getSettingValue("OFFICE_POLYGON", "[]"));
        model.addAttribute("content", "attendance/index");
        return "layout";
    }

    @PostMapping("/check-in")
    public String processCheckIn(
            @RequestParam String nik,
            @RequestParam String email,
            @RequestParam double lat,
            @RequestParam double lng,
            Model model) {
        
        Optional<Employee> employeeOpt = employeeRepository.findByNikAndEmail(nik, email);
        
        if (employeeOpt.isEmpty()) {
            return "redirect:/attendance?error=user_not_found";
        }
        
        attendanceService.checkIn(employeeOpt.get().getId(), lat, lng);
        
        return "redirect:/attendance?success";
    }
}
