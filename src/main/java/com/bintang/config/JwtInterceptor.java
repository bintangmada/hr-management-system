package com.bintang.config;

import com.bintang.entity.AppMenu;
import com.bintang.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private com.bintang.repository.AppMenuRepository menuRepository;

    @Autowired
    private com.bintang.repository.AppNotificationRepository notificationRepository;

    @Autowired
    private com.bintang.repository.EmployeeRepository employeeRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = null;
        
        // Check for JWT in cookies
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("JWT_TOKEN".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token != null && jwtUtil.validateToken(token)) {
            String role = jwtUtil.extractRole(token);
            request.setAttribute("employeeId", jwtUtil.extractEmployeeId(token));
            request.setAttribute("nik", jwtUtil.extractNik(token));
            request.setAttribute("role", role);

            String path = request.getRequestURI();
            boolean isAdminPath = path.startsWith("/settings") || 
                                 path.startsWith("/employees") || 
                                 path.startsWith("/admin") || 
                                 path.startsWith("/payroll") || 
                                 path.startsWith("/performance");

            if (isAdminPath && !"ADMIN".equals(role)) {
                response.sendRedirect("/attendance?error=unauthorized");
                return false;
            }
            return true;
        }

        response.sendRedirect("/login");
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, org.springframework.web.servlet.ModelAndView modelAndView) throws Exception {
        if (modelAndView != null && request.getAttribute("role") != null) {
            String role = (String) request.getAttribute("role");
            modelAndView.addObject("role", role);
            modelAndView.addObject("nik", request.getAttribute("nik"));

            // Fetch and Filter Menus
            List<com.bintang.entity.AppMenu> allMenus = menuRepository.findByIsActiveOrderBySortOrderAsc(true);
            String currentNik = (String) request.getAttribute("nik");
            
            List<com.bintang.entity.AppMenu> filteredMenus = allMenus.stream()
                .filter(m -> {
                    // Specific NIK Override
                    if (m.getPermittedNiks() != null && !m.getPermittedNiks().trim().isEmpty()) {
                        String[] allowedNiks = m.getPermittedNiks().split(",");
                        for (String n : allowedNiks) {
                            if (n.trim().equals(currentNik)) return true;
                        }
                        return false; // If NIK list is present but user not in it
                    }
                    // Standard Role Based
                    return "ALL".equals(m.getRoleRequired()) || role.equals(m.getRoleRequired());
                })
                .collect(java.util.stream.Collectors.toList());

            // Group Parents and Children
            List<MenuWrapper> menuStructure = new java.util.ArrayList<>();
            java.util.Map<Long, MenuWrapper> parentMap = new java.util.HashMap<>();

            // First pass: Parents
            for (com.bintang.entity.AppMenu m : filteredMenus) {
                if (m.getParentId() == null) {
                    MenuWrapper w = new MenuWrapper(m);
                    menuStructure.add(w);
                    parentMap.put(m.getId(), w);
                }
            }

            // Second pass: Children
            for (com.bintang.entity.AppMenu m : filteredMenus) {
                if (m.getParentId() != null && parentMap.containsKey(m.getParentId())) {
                    parentMap.get(m.getParentId()).getChildren().add(m);
                }
            }

            modelAndView.addObject("sidebarMenus", menuStructure);

            // Fetch Notifications (Mixed: Read & Unread)
            List<com.bintang.entity.AppNotification> recentNotifications = notificationRepository.findTop5ByTargetNikOrderByCreatedAtDesc(currentNik);
            long unreadCount = notificationRepository.findByTargetNikAndIsReadOrderByCreatedAtDesc(currentNik, false).size();
            
            modelAndView.addObject("recentNotifications", recentNotifications);
            modelAndView.addObject("unreadCount", unreadCount);

            // Fetch Employee Name for Avatar
            employeeRepository.findByNik(currentNik).ifPresent(emp -> {
                modelAndView.addObject("employeeName", emp.getFirstName() + " " + emp.getLastName());
            });
        }
    }

    // Inner class for hierarchy
    public static class MenuWrapper {
        private com.bintang.entity.AppMenu menu;
        private List<AppMenu> children = new java.util.ArrayList<>();
        public MenuWrapper(com.bintang.entity.AppMenu menu) { this.menu = menu; }
        public com.bintang.entity.AppMenu getMenu() { return menu; }
        public List<com.bintang.entity.AppMenu> getChildren() { return children; }
    }
}
