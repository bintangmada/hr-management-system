package com.bintang.controller;

import com.bintang.entity.AppMenu;
import com.bintang.repository.AppMenuRepository;
import com.bintang.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/settings/menu-management")
public class MenuManagementController {

    @Autowired private AppMenuRepository menuRepository;
    @Autowired private AuditService auditService;

    @GetMapping
    public String listMenus(Model model) {
        model.addAttribute("menus", menuRepository.findAll());
        model.addAttribute("parentMenus", menuRepository.findByParentIdOrderBySortOrderAsc(null));
        model.addAttribute("content", "settings/menu-management");
        return "layout";
    }

    @PostMapping
    public String saveMenu(@ModelAttribute AppMenu menu) {
        boolean isNew = (menu.getId() == null);
        
        // Ensure parentId is null if set to 0 or empty (from form)
        if (menu.getParentId() != null && menu.getParentId() == 0) {
            menu.setParentId(null);
        }

        menuRepository.save(menu);
        auditService.log(isNew ? "CREATE_MENU" : "UPDATE_MENU", "Admin", "AppMenu", menu.getId(), 
            (isNew ? "Menambahkan" : "Mengubah") + " Menu: " + menu.getTitle());
        return "redirect:/settings/menu-management?success";
    }

    @PostMapping("/delete/{id}")
    public String deleteMenu(@PathVariable Long id) {
        menuRepository.deleteById(id);
        auditService.log("DELETE_MENU", "Admin", "AppMenu", id, "Menghapus Menu ID: " + id);
        return "redirect:/settings/menu-management?deleted";
    }
}
