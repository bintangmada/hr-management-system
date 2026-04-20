package com.bintang.controller;

import com.bintang.entity.Employee;
import com.bintang.repository.*;
import com.bintang.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private JobRepository jobRepository;
    @Autowired private DepartmentRepository departmentRepository;
    @Autowired private AuditService auditService;

    @GetMapping
    public String listEmployees(Model model) {
        model.addAttribute("employees", employeeRepository.findAll());
        model.addAttribute("jobs", jobRepository.findAll());
        model.addAttribute("departments", departmentRepository.findAll());
        model.addAttribute("content", "employees/index");
        return "layout";
    }

    @PostMapping
    public String saveEmployee(@ModelAttribute Employee employee, jakarta.servlet.http.HttpServletRequest request, RedirectAttributes redirectAttributes) {
        boolean isNew = (employee.getId() == null);
        employeeRepository.save(employee);
        
        // Fetch Admin Info
        Long adminId = (Long) request.getAttribute("employeeId");
        String role = (String) request.getAttribute("role");
        Employee admin = employeeRepository.findById(adminId).orElse(null);

        String actionMsg = isNew ? "Menambahkan" : "Mengubah";
        if (admin != null) {
            auditService.log(isNew ? "CREATE_EMPLOYEE" : "UPDATE_EMPLOYEE", role, 
                admin.getFirstName() + " " + admin.getLastName(), admin.getNik(), 
                "Employee", employee.getId(), actionMsg + " Karyawan: " + employee.getFirstName());
        }
        
        redirectAttributes.addFlashAttribute("successMessage", 
            "Karyawan '" + employee.getFirstName() + "' berhasil " + (isNew ? "ditambahkan" : "diperbarui") + "!");
            
        return "redirect:/employees";
    }

    @PostMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable Long id, jakarta.servlet.http.HttpServletRequest request, RedirectAttributes redirectAttributes) {
        employeeRepository.deleteById(id);
        
        // Fetch Admin Info
        Long adminId = (Long) request.getAttribute("employeeId");
        String role = (String) request.getAttribute("role");
        Employee admin = employeeRepository.findById(adminId).orElse(null);

        if (admin != null) {
            auditService.log("DELETE_EMPLOYEE", role, admin.getFirstName() + " " + admin.getLastName(), admin.getNik(), 
                "Employee", id, "Menghapus Karyawan ID: " + id);
        }
        redirectAttributes.addFlashAttribute("successMessage", "Data karyawan berhasil dihapus!");
        return "redirect:/employees";
    }
}
