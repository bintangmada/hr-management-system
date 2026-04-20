package com.bintang.controller;

import com.bintang.entity.Performance;
import com.bintang.repository.EmployeeRepository;
import com.bintang.repository.PerformanceRepository;
import com.bintang.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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
    public String saveEvaluation(@ModelAttribute Performance performance, jakarta.servlet.http.HttpServletRequest request, RedirectAttributes redirectAttributes) {
        performance.setReviewDate(LocalDate.now());
        performanceRepository.save(performance);
        auditService.logWithContext(request, "CREATE_PERFORMANCE", "Performance", performance.getId(), "Menilai kinerja karyawan ID: " + performance.getEmployeeId());
        redirectAttributes.addFlashAttribute("successMessage", "Penilaian kinerja berhasil disimpan!");
        return "redirect:/performance";
    }
}
