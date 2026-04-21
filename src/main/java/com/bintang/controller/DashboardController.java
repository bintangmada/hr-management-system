package com.bintang.controller;

import com.bintang.entity.Attendance;
import com.bintang.entity.Employee;
import com.bintang.entity.Payroll;
import com.bintang.entity.Performance;
import com.bintang.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Controller
public class DashboardController {

    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private AttendanceRepository attendanceRepository;
    @Autowired private PayrollRepository payrollRepository;
    @Autowired private PerformanceRepository performanceRepository;
    @Autowired private LeaveRequestRepository leaveRequestRepository;

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        String nik = (String) request.getAttribute("nik");
        Long employeeId = (Long) request.getAttribute("employeeId");

        if ("ADMIN".equals(role)) {
            prepareAdminDashboard(model);
        } else {
            prepareEmployeeDashboard(model, employeeId, nik);
        }

        model.addAttribute("content", "dashboard");
        return "layout";
    }

    private void prepareAdminDashboard(Model model) {
        model.addAttribute("totalEmployees", employeeRepository.count());
        
        // Attendance Today
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        
        List<Attendance> todayAttendance = attendanceRepository.findAll().stream()
            .filter(a -> a.getCheckInTime().isAfter(startOfDay) && a.getCheckInTime().isBefore(endOfDay))
            .toList();

        model.addAttribute("presentToday", todayAttendance.size());
        model.addAttribute("lateToday", todayAttendance.stream()
            .filter(a -> a.getIsLate() != null && a.getIsLate())
            .count());

        // On Leave Today
        long onLeaveToday = leaveRequestRepository.findAll().stream()
            .filter(l -> "APPROVED".equals(l.getStatus()) && l.getStartDate() != null && l.getEndDate() != null)
            .filter(l -> !LocalDate.now().isBefore(l.getStartDate()) && !LocalDate.now().isAfter(l.getEndDate()))
            .count();
        model.addAttribute("onLeaveToday", onLeaveToday);

        // Recent Generic Activity (Last 5 attendances across company)
        List<Attendance> recentActivity = attendanceRepository.findAll().stream()
            .sorted((a, b) -> b.getCheckInTime().compareTo(a.getCheckInTime()))
            .limit(5)
            .toList();
        model.addAttribute("recentActivity", recentActivity);
    }

    private void prepareEmployeeDashboard(Model model, Long employeeId, String nik) {
        Employee emp = employeeRepository.findById(employeeId).orElse(null);
        model.addAttribute("employee", emp);

        // Stats Bulan Ini
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = LocalDate.now().atTime(LocalTime.MAX);
        List<Attendance> monthlyAttendance = attendanceRepository.findByEmployeeIdAndDateRange(employeeId, startOfMonth, endOfMonth);
        
        long daysPresent = monthlyAttendance.size();
        long timesLate = monthlyAttendance.stream().filter(a -> a.getIsLate() != null && a.getIsLate()).count();
        
        model.addAttribute("daysPresent", daysPresent);
        model.addAttribute("timesLate", timesLate);

        // Today Status
        Optional<Attendance> today = attendanceRepository.findTodayAttendance(employeeId, LocalDate.now().atStartOfDay(), LocalDate.now().atTime(LocalTime.MAX));
        model.addAttribute("todayAttendance", today.orElse(null));

        // Latest Payroll
        List<Payroll> payrolls = payrollRepository.findByEmployeeId(employeeId);
        Payroll latestPayroll = payrolls.stream()
            .sorted((a, b) -> b.getPaymentDate().compareTo(a.getPaymentDate()))
            .findFirst().orElse(null);
        model.addAttribute("latestPayroll", latestPayroll);

        // Latest Performance
        List<Performance> evaluations = performanceRepository.findByEmployeeId(employeeId);
        Performance latestEval = evaluations.stream()
            .sorted((a, b) -> b.getReviewDate().compareTo(a.getReviewDate()))
            .findFirst().orElse(null);
        model.addAttribute("latestPerformance", latestEval);

        // Leave Calculations
        int defaultQuota = 12; // Base annual quota
        int currentYear = LocalDate.now().getYear();
        List<com.bintang.entity.LeaveRequest> allMyLeaves = leaveRequestRepository.findByEmployeeIdOrderByCreatedAtDesc(employeeId);
        long daysTaken = 0;
        
        for (com.bintang.entity.LeaveRequest lr : allMyLeaves) {
            if ("CUTI TAHUNAN".equals(lr.getLeaveType()) && "APPROVED".equals(lr.getStatus())) {
                if (lr.getStartDate() != null && lr.getEndDate() != null && lr.getStartDate().getYear() == currentYear) {
                    long diff = java.time.temporal.ChronoUnit.DAYS.between(lr.getStartDate(), lr.getEndDate()) + 1;
                    if (diff > 0) daysTaken += diff;
                }
            }
        }
        model.addAttribute("leaveQuota", defaultQuota);
        model.addAttribute("leaveTaken", daysTaken);
        model.addAttribute("leaveRemaining", Math.max(0, defaultQuota - daysTaken));

        // Recent Logs (Personal)
        List<Attendance> myRecentLogs = attendanceRepository.findByEmployeeIdOrderByCheckInTimeDesc(employeeId).stream()
            .limit(5)
            .toList();
        model.addAttribute("myRecentLogs", myRecentLogs);
    }
}
