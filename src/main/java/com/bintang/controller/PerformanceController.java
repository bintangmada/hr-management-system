package com.bintang.controller;

import com.bintang.entity.Performance;
import com.bintang.repository.EmployeeRepository;
import com.bintang.repository.PerformanceRepository;
import com.bintang.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@Controller
@RequestMapping("/performance")
public class PerformanceController {

    @Autowired private PerformanceRepository performanceRepository;
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private AuditService auditService;

    @GetMapping
    public String listPerformance(Model model) {
        model.addAttribute("evaluations", performanceRepository.findAll());
        model.addAttribute("employees", employeeRepository.findAll());
        model.addAttribute("content", "performance/index");
        return "layout";
    }

    @PostMapping
    public String saveEvaluation(@ModelAttribute Performance performance) {
        performance.setReviewDate(LocalDate.now());
        performanceRepository.save(performance);
        auditService.log("CREATE_PERFORMANCE", "Admin", "Performance", performance.getId(), "Menilai kinerja karyawan ID: " + performance.getEmployeeId());
        return "redirect:/performance?success";
    }
}
