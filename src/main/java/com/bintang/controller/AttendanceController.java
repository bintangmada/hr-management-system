package com.bintang.controller;

import com.bintang.entity.Attendance;
import com.bintang.entity.Employee;
import com.bintang.repository.AttendanceLocationRepository;
import com.bintang.repository.EmployeeRepository;
import com.bintang.service.AttendanceService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AttendanceLocationRepository locationRepository;

    @GetMapping
    public String attendancePage(HttpServletRequest request, Model model) {
        Long employeeId = (Long) request.getAttribute("employeeId");
        Employee employee = employeeRepository.findById(employeeId).orElseThrow();
        
        Attendance todayAttendance = attendanceService.getTodayAttendance(employeeId);
        
        model.addAttribute("employee", employee);
        model.addAttribute("todayAttendance", todayAttendance);
        model.addAttribute("locations", locationRepository.findAll());
        model.addAttribute("content", "attendance/index");
        return "layout";
    }

    @PostMapping("/check-in")
    public String processCheckIn(
            HttpServletRequest request,
            @RequestParam double lat,
            @RequestParam double lng) {
        
        Long employeeId = (Long) request.getAttribute("employeeId");
        String role = (String) request.getAttribute("role");
        attendanceService.checkIn(employeeId, role, lat, lng);
        
        return "redirect:/attendance?success=checkin";
    }

    @PostMapping("/check-out")
    public String processCheckOut(
            HttpServletRequest request,
            @RequestParam Long attendanceId,
            @RequestParam double lat,
            @RequestParam double lng) {
        
        Long employeeId = (Long) request.getAttribute("employeeId");
        String role = (String) request.getAttribute("role");
        attendanceService.checkOut(attendanceId, role, lat, lng);
        
        return "redirect:/attendance?success=checkout";
    }

    @GetMapping("/rekap")
    public String rekapPage(HttpServletRequest request, Model model) {
        Long employeeId = (Long) request.getAttribute("employeeId");
        List<Attendance> history = attendanceService.getAttendanceHistory(employeeId);
        
        model.addAttribute("history", history);
        model.addAttribute("content", "attendance/history");
        return "layout";
    }
}
