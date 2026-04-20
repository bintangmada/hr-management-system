package com.bintang.controller;

import com.bintang.entity.Employee;
import com.bintang.repository.EmployeeRepository;
import com.bintang.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.HashMap;

@Controller
@RequestMapping("/settings/user-management")
public class UserController {

    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private AuditService auditService;

    @GetMapping("/lookup/{nik}")
    @ResponseBody
    public ResponseEntity<?> lookupUser(@PathVariable String nik) {
        return employeeRepository.findByNik(nik)
            .map(u -> {
                Map<String, Object> resp = new HashMap<>();
                resp.put("success", true);
                resp.put("name", u.getFirstName() + " " + u.getLastName());
                resp.put("role", u.getRole());
                return ResponseEntity.ok(resp);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", employeeRepository.findAll());
        model.addAttribute("content", "settings/user-list");
        return "layout";
    }

    @PostMapping("/update-role")
    public String updateRole(@RequestParam Long id, @RequestParam String role, @RequestParam(required = false) Boolean isActive, jakarta.servlet.http.HttpServletRequest request, RedirectAttributes redirectAttributes) {
        Employee emp = employeeRepository.findById(id).orElseThrow();
        String oldRole = emp.getRole();
        emp.setRole(role);
        if (isActive != null) emp.setIsActive(isActive);
        employeeRepository.save(emp);
        
        auditService.logWithContext(request, "UPDATE_USER_ACCESS", "Employee", id, 
            "Update akses " + emp.getFirstName() + ": Role " + oldRole + " -> " + role + " (Active: " + emp.getIsActive() + ")");
        
        redirectAttributes.addFlashAttribute("successMessage", "Hak akses user berhasil diperbarui!");
        return "redirect:/settings/user-management";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam Long id, @RequestParam String newPassword, jakarta.servlet.http.HttpServletRequest request, RedirectAttributes redirectAttributes) {
        Employee emp = employeeRepository.findById(id).orElseThrow();
        emp.setPassword(newPassword);
        employeeRepository.save(emp);
        
        auditService.logWithContext(request, "RESET_PASSWORD", "Employee", id, "Reset password untuk user: " + emp.getFirstName());
        redirectAttributes.addFlashAttribute("successMessage", "Password user berhasil direset!");
        return "redirect:/settings/user-management";
    }
}
