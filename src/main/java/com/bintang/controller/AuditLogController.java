package com.bintang.controller;

import com.bintang.dto.DataTablesResponse;
import com.bintang.entity.AuditLog;
import com.bintang.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AuditLogController {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @GetMapping("/audit-logs")
    public String listAuditLogs(Model model) {
        model.addAttribute("content", "admin/audit-logs");
        return "layout";
    }

    @GetMapping("/api/audit-logs")
    @ResponseBody
    public DataTablesResponse<AuditLog> getAuditLogsData(HttpServletRequest request) {
        String drawParam = request.getParameter("draw");
        String startParam = request.getParameter("start");
        String lengthParam = request.getParameter("length");
        
        int draw = drawParam != null ? Integer.parseInt(drawParam) : 1;
        int start = startParam != null ? Integer.parseInt(startParam) : 0;
        int length = lengthParam != null ? Integer.parseInt(lengthParam) : 10;
        
        // Sorting
        String orderColumnIndex = request.getParameter("order[0][column]");
        String orderDir = request.getParameter("order[0][dir]");
        
        String[] columnNames = {"createdAt", "actorRole", "actorName", "actorNik", "action", "entityName", "details"};
        String sortColumn = "createdAt";
        if (orderColumnIndex != null) {
            try {
                int colIdx = Integer.parseInt(orderColumnIndex);
                if (colIdx < columnNames.length) sortColumn = columnNames[colIdx];
            } catch (Exception e) {
                // fallback to default
            }
        }
        
        Sort sort = Sort.by("asc".equalsIgnoreCase(orderDir) ? Sort.Direction.ASC : Sort.Direction.DESC, sortColumn);
        Pageable pageable = PageRequest.of(start / length, length, sort);
        
        // Dynamic Specification for Per-Column Search
        Specification<AuditLog> spec = (root, query, cb) -> {
            var predicates = new java.util.ArrayList<Predicate>();
            
            for (int i = 0; i < columnNames.length; i++) {
                String colSearch = request.getParameter("columns[" + i + "][search][value]");
                if (colSearch != null && !colSearch.isEmpty()) {
                    predicates.add(cb.like(cb.lower(root.get(columnNames[i]).as(String.class)), 
                        "%" + colSearch.toLowerCase() + "%"));
                }
            }
            
            // Also support global search if provided
            String globalSearch = request.getParameter("search[value]");
            if (globalSearch != null && !globalSearch.isEmpty()) {
                var searchPredicates = new java.util.ArrayList<Predicate>();
                for (String col : columnNames) {
                    searchPredicates.add(cb.like(cb.lower(root.get(col).as(String.class)), 
                        "%" + globalSearch.toLowerCase() + "%"));
                }
                predicates.add(cb.or(searchPredicates.toArray(new Predicate[0])));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        Page<AuditLog> page = auditLogRepository.findAll(spec, pageable);
        
        return new DataTablesResponse<>(
            draw, 
            page.getTotalElements(), 
            page.getTotalElements(), 
            page.getContent()
        );
    }
}
