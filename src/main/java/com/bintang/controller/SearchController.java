package com.bintang.controller;

import com.bintang.dto.SearchResultDTO;
import com.bintang.entity.AppMenu;
import com.bintang.entity.Employee;
import com.bintang.repository.AppMenuRepository;
import com.bintang.repository.EmployeeRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SearchController {

    @Autowired
    private AppMenuRepository menuRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping("/search")
    public List<SearchResultDTO> search(@RequestParam("q") String query, HttpServletRequest request) {
        List<SearchResultDTO> results = new ArrayList<>();
        if (query == null || query.trim().length() < 2) return results;

        String q = query.toLowerCase();
        String userRole = (String) request.getAttribute("role");

        // 1. Search Menus
        List<AppMenu> allMenus = menuRepository.findByIsActiveOrderBySortOrderAsc(true);
        List<SearchResultDTO> menuResults = allMenus.stream()
                .filter(m -> m.getTitle().toLowerCase().contains(q) && m.getUrl() != null && !m.getUrl().equals("#"))
                .filter(m -> "ALL".equals(m.getRoleRequired()) || (userRole != null && userRole.equals(m.getRoleRequired())))
                .map(m -> new SearchResultDTO(
                        m.getTitle(), 
                        "Menu Navigasi", 
                        m.getUrl(), 
                        "MENU", 
                        m.getIcon() != null ? m.getIcon() : "fas fa-link"
                ))
                .collect(Collectors.toList());
        results.addAll(menuResults);

        // 2. Search Employees (Admin only)
        if ("ADMIN".equals(userRole)) {
            List<Employee> employees = employeeRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrNikContainingIgnoreCase(query, query, query);
            List<SearchResultDTO> empResults = employees.stream()
                    .map(e -> new SearchResultDTO(
                            e.getFirstName() + " " + e.getLastName(),
                            "Karyawan • " + e.getNik(),
                            "/employees", // Ideally /employees/id, but current system uses list
                            "EMPLOYEE",
                            "fas fa-user"
                    ))
                    .collect(Collectors.toList());
            results.addAll(empResults);
        }

        return results;
    }
}
