package com.bintang.controller;

import com.bintang.dto.DashboardStatsDTO;
import com.bintang.service.DashboardService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        
        // Security check: Only Admin can access full dashboard analytics
        if (!"ADMIN".equals(role)) {
            return "redirect:/attendance";
        }

        DashboardStatsDTO stats = dashboardService.getAdminStats();
        model.addAttribute("stats", stats);
        model.addAttribute("content", "dashboard");
        return "layout";
    }
}
