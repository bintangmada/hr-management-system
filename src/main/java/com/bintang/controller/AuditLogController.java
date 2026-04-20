package com.bintang.controller;

import com.bintang.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AuditLogController {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @GetMapping("/audit-logs")
    public String listAuditLogs(Model model) {
        model.addAttribute("logs", auditLogRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")));
        model.addAttribute("content", "admin/audit-logs");
        return "layout";
    }
}
