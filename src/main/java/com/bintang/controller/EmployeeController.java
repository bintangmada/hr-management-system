package com.bintang.controller;

import com.bintang.entity.Employee;
import com.bintang.repository.*;
import com.bintang.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String saveEmployee(@ModelAttribute Employee employee) {
        employeeRepository.save(employee);
        auditService.log("CREATE_EMPLOYEE", "Admin", "Employee", employee.getId(), "Menambahkan/Mengubah Karyawan: " + employee.getFirstName() + " " + employee.getLastName());
        return "redirect:/employees?success";
    }

    @PostMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable Long id) {
        employeeRepository.deleteById(id);
        auditService.log("DELETE_EMPLOYEE", "Admin", "Employee", id, "Menghapus Karyawan ID: " + id);
        return "redirect:/employees?deleted";
    }
}
