package com.bintang.controller;

import com.bintang.entity.Payroll;
import com.bintang.repository.EmployeeRepository;
import com.bintang.repository.PayrollRepository;
import com.bintang.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@Controller
@RequestMapping("/payroll")
public class PayrollController {

    @Autowired private PayrollRepository payrollRepository;
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private AuditService auditService;

    @GetMapping
    public String listPayroll(Model model) {
        model.addAttribute("payrolls", payrollRepository.findAll());
        model.addAttribute("employees", employeeRepository.findAll());
        model.addAttribute("content", "payroll/index");
        return "layout";
    }

    @PostMapping
    public String generatePayroll(@ModelAttribute Payroll payroll) {
        payroll.setPaymentDate(LocalDate.now());
        payroll.setNetSalary(payroll.getBasicSalary() + payroll.getAllowances() - payroll.getDeductions());
        payrollRepository.save(payroll);
        auditService.log("GENERATE_PAYROLL", "Admin", "Payroll", payroll.getId(), "Membuat payroll untuk karyawan ID: " + payroll.getEmployeeId());
        return "redirect:/payroll?success";
    }
}
