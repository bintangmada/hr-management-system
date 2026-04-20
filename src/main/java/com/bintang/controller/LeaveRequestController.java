package com.bintang.controller;

import com.bintang.entity.Employee;
import com.bintang.entity.LeaveRequest;
import com.bintang.repository.EmployeeRepository;
import com.bintang.repository.LeaveRequestRepository;
import com.bintang.service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/leaves")
public class LeaveRequestController {

    @Autowired private LeaveRequestRepository leaveRequestRepository;
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private AuditService auditService;
    @Autowired private com.bintang.service.AppNotificationService notificationService;

    // View for Employees: Their history + application form
    @GetMapping
    public String listMyLeaves(HttpServletRequest request, Model model) {
        Long employeeId = (Long) request.getAttribute("employeeId");
        model.addAttribute("myLeaves", leaveRequestRepository.findByEmployeeIdOrderByCreatedAtDesc(employeeId));
        model.addAttribute("newLeave", new LeaveRequest());
        model.addAttribute("content", "leaves/index");
        return "layout";
    }

    // Submit New Leave Request (Employee)
    @PostMapping
    public String submitLeave(@ModelAttribute LeaveRequest leaveRequest, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        Long employeeId = (Long) request.getAttribute("employeeId");
        leaveRequest.setEmployeeId(employeeId);
        leaveRequest.setStatus("PENDING");
        leaveRequest.setCreatedAt(LocalDateTime.now());
        leaveRequestRepository.save(leaveRequest);

        Employee emp = employeeRepository.findById(employeeId).orElseThrow();
        String role = (String) request.getAttribute("role");
        auditService.log("LEAVE_REQUEST", role, emp.getFirstName() + " " + emp.getLastName(), emp.getNik(), 
            "LeaveRequest", leaveRequest.getId(), "Mengajukan cuti: " + leaveRequest.getLeaveType());

        redirectAttributes.addFlashAttribute("successMessage", "Pengajuan cuti berhasil dikirim!");
        return "redirect:/leaves";
    }

    // View for Admins & Managers: Manage requests
    @GetMapping("/manage")
    public String manageLeaves(HttpServletRequest request, Model model) {
        String role = (String) request.getAttribute("role");
        Long employeeId = (Long) request.getAttribute("employeeId");

        List<LeaveRequest> requests;
        boolean isNotManager = false;
        
        if ("ADMIN".equals(role)) {
            requests = leaveRequestRepository.findAll();
        } else {
            // Find subordinates for this manager
            List<Employee> subordinates = employeeRepository.findByManagerId(employeeId);
            if (subordinates.isEmpty()) {
                isNotManager = true;
                model.addAttribute("isNotManager", isNotManager);
                model.addAttribute("content", "leaves/manage");
                return "layout";
            }
            List<Long> subordinateIds = subordinates.stream().map(Employee::getId).toList();
            requests = leaveRequestRepository.findByEmployeeIdInOrderByCreatedAtDesc(subordinateIds);
        }

        // Map Employee Names for View
        java.util.Map<Long, Employee> empMap = new java.util.HashMap<>();
        for (LeaveRequest lr : requests) {
            if (!empMap.containsKey(lr.getEmployeeId())) {
                employeeRepository.findById(lr.getEmployeeId()).ifPresent(emp -> empMap.put(emp.getId(), emp));
            }
        }

        model.addAttribute("allLeaves", requests);
        model.addAttribute("empMap", empMap);
        model.addAttribute("isNotManager", isNotManager);
        model.addAttribute("content", "leaves/manage");
        return "layout";
    }

    // Approve Leave Request (Admin)
    @PostMapping("/approve/{id}")
    public String approveLeave(@PathVariable Long id, @RequestParam(required = false) String comment, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        LeaveRequest lr = leaveRequestRepository.findById(id).orElseThrow();
        lr.setStatus("APPROVED");
        lr.setAdminComment(comment);
        lr.setUpdatedAt(LocalDateTime.now());
        leaveRequestRepository.save(lr);

        // Fetch Admin Info for Audit
        Long adminId = (Long) request.getAttribute("employeeId");
        String role = (String) request.getAttribute("role");
        Employee admin = employeeRepository.findById(adminId).orElse(null);
        
        if (admin != null) {
            auditService.log("LEAVE_APPROVE", role, admin.getFirstName() + " " + admin.getLastName(), admin.getNik(), 
                "LeaveRequest", id, "Menyetujui pengajuan cuti ID: " + id);
        }
        
        // Notify Employee
        Employee emp = employeeRepository.findById(lr.getEmployeeId()).orElse(null);
        if (emp != null) {
            notificationService.send(emp.getNik(), "Cuti Disetujui! ✅", 
                "Pengajuan cuti " + lr.getLeaveType() + " Anda telah disetujui.", "/leaves");
        }

        redirectAttributes.addFlashAttribute("successMessage", "Pengajuan cuti telah disetujui!");
        return "redirect:/leaves/manage";
    }

    // Reject Leave Request (Admin)
    @PostMapping("/reject/{id}")
    public String rejectLeave(@PathVariable Long id, @RequestParam(required = false) String comment, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        LeaveRequest lr = leaveRequestRepository.findById(id).orElseThrow();
        lr.setStatus("REJECTED");
        lr.setAdminComment(comment);
        lr.setUpdatedAt(LocalDateTime.now());
        leaveRequestRepository.save(lr);

        // Fetch Admin Info for Audit
        Long adminId = (Long) request.getAttribute("employeeId");
        String role = (String) request.getAttribute("role");
        Employee admin = employeeRepository.findById(adminId).orElse(null);
        
        if (admin != null) {
            auditService.log("LEAVE_REJECT", role, admin.getFirstName() + " " + admin.getLastName(), admin.getNik(), 
                "LeaveRequest", id, "Menolak pengajuan cuti ID: " + id);
        }
        
        // Notify Employee
        Employee emp = employeeRepository.findById(lr.getEmployeeId()).orElse(null);
        if (emp != null) {
            notificationService.send(emp.getNik(), "Cuti Ditolak ❌", 
                "Pengajuan cuti " + lr.getLeaveType() + " Anda ditolak. Catatan: " + (comment != null ? comment : "-"), "/leaves");
        }

        redirectAttributes.addFlashAttribute("successMessage", "Pengajuan cuti telah ditolak!");
        return "redirect:/leaves/manage";
    }
}
