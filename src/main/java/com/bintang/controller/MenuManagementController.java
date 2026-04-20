package com.bintang.controller;

import com.bintang.entity.AppMenu;
import com.bintang.repository.AppMenuRepository;
import com.bintang.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/settings/menu-management")
public class MenuManagementController {

    @Autowired private AppMenuRepository menuRepository;
    @Autowired private com.bintang.repository.EmployeeRepository employeeRepository;
    @Autowired private AuditService auditService;

    @GetMapping
    public String listMenus(Model model) {
        model.addAttribute("menus", menuRepository.findAll());
        model.addAttribute("parentMenus", menuRepository.findByParentIdOrderBySortOrderAsc(null));
        model.addAttribute("employees", employeeRepository.findAll(org.springframework.data.domain.Sort.by("firstName")));
        model.addAttribute("content", "settings/menu-management");
        return "layout";
    }

    @PostMapping
    public String saveMenu(@ModelAttribute AppMenu menu, jakarta.servlet.http.HttpServletRequest request, RedirectAttributes redirectAttributes) {
        boolean isNew = (menu.getId() == null);
        
        // Ensure parentId is null if set to 0 or empty (from form)
        if (menu.getParentId() != null && menu.getParentId() == 0) {
            menu.setParentId(null);
        }

        menuRepository.save(menu);
        auditService.logWithContext(request, isNew ? "CREATE_MENU" : "UPDATE_MENU", "AppMenu", menu.getId(), 
            (isNew ? "Menambahkan" : "Mengubah") + " Menu: " + menu.getTitle());
            
        redirectAttributes.addFlashAttribute("successMessage", 
            "Menu '" + menu.getTitle() + "' berhasil " + (isNew ? "ditambahkan" : "diperbarui") + "!");
            
        return "redirect:/settings/menu-management";
    }

    @PostMapping("/delete/{id}")
    public String deleteMenu(@PathVariable Long id, jakarta.servlet.http.HttpServletRequest request, RedirectAttributes redirectAttributes) {
        menuRepository.deleteById(id);
        auditService.logWithContext(request, "DELETE_MENU", "AppMenu", id, "Menghapus Menu ID: " + id);
        redirectAttributes.addFlashAttribute("successMessage", "Menu berhasil dihapus!");
        return "redirect:/settings/menu-management";
    }
}
