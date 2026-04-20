package com.bintang.controller;

import com.bintang.entity.*;
import com.bintang.repository.*;
import com.bintang.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/settings/master-data")
public class MasterDataController {

    @Autowired private RegionRepository regionRepository;
    @Autowired private CountryRepository countryRepository;
    @Autowired private LocationRepository locationRepository;
    @Autowired private DepartmentRepository departmentRepository;
    @Autowired private JobRepository jobRepository;
    @Autowired private AuditService auditService;

    @GetMapping
    public String listAll() {
        return "redirect:/settings/master-data/regions";
    }

    @GetMapping("/regions")
    public String listRegions(Model model) {
        model.addAttribute("regions", regionRepository.findAll());
        model.addAttribute("content", "settings/regions");
        return "layout";
    }

    @GetMapping("/jobs")
    public String listJobs(Model model) {
        model.addAttribute("jobs", jobRepository.findAll());
        model.addAttribute("content", "settings/jobs");
        return "layout";
    }

    @GetMapping("/departments")
    public String listDepartments(Model model) {
        model.addAttribute("departments", departmentRepository.findAll());
        model.addAttribute("content", "settings/departments");
        return "layout";
    }

    @GetMapping("/locations")
    public String listLocations(Model model) {
        model.addAttribute("locations", locationRepository.findAll());
        model.addAttribute("content", "settings/locations-org");
        return "layout";
    }

    // SAVE ACTIONS
    @PostMapping("/regions")
    public String saveRegion(@ModelAttribute Region region, jakarta.servlet.http.HttpServletRequest request, RedirectAttributes redirectAttributes) {
        boolean isNew = (region.getId() == null);
        regionRepository.save(region);
        auditService.logWithContext(request, isNew ? "CREATE_REGION" : "UPDATE_REGION", "Region", region.getId(), 
            (isNew ? "Menambahkan" : "Mengubah") + " Region: " + region.getName());
        redirectAttributes.addFlashAttribute("successMessage", "Region berhasil " + (isNew ? "disimpan" : "diperbarui") + "!");
        return "redirect:/settings/master-data/regions";
    }

    @PostMapping("/jobs")
    public String saveJob(@ModelAttribute Job job, jakarta.servlet.http.HttpServletRequest request, RedirectAttributes redirectAttributes) {
        boolean isNew = !jobRepository.existsById(job.getId());
        jobRepository.save(job);
        auditService.logWithContext(request, isNew ? "CREATE_JOB" : "UPDATE_JOB", "Job", null, 
            (isNew ? "Menambahkan" : "Mengubah") + " Jabatan: " + job.getTitle());
        redirectAttributes.addFlashAttribute("successMessage", "Jabatan berhasil " + (isNew ? "disimpan" : "diperbarui") + "!");
        return "redirect:/settings/master-data/jobs";
    }

    @PostMapping("/locations")
    public String saveLocation(@ModelAttribute Location location, jakarta.servlet.http.HttpServletRequest request, RedirectAttributes redirectAttributes) {
        boolean isNew = (location.getId() == null);
        locationRepository.save(location);
        auditService.logWithContext(request, isNew ? "CREATE_LOCATION" : "UPDATE_LOCATION", "Location", location.getId(), 
            (isNew ? "Menambahkan" : "Mengubah") + " Lokasi Cabang: " + location.getCity());
        redirectAttributes.addFlashAttribute("successMessage", "Lokasi berhasil " + (isNew ? "disimpan" : "diperbarui") + "!");
        return "redirect:/settings/master-data/locations";
    }

    @PostMapping("/departments")
    public String saveDepartment(@ModelAttribute Department dept, jakarta.servlet.http.HttpServletRequest request, RedirectAttributes redirectAttributes) {
        boolean isNew = (dept.getId() == null);
        departmentRepository.save(dept);
        auditService.logWithContext(request, isNew ? "CREATE_DEPARTMENT" : "UPDATE_DEPARTMENT", "Department", dept.getId(), 
            (isNew ? "Menambahkan" : "Mengubah") + " Departemen: " + dept.getName());
        redirectAttributes.addFlashAttribute("successMessage", "Departemen berhasil " + (isNew ? "disimpan" : "diperbarui") + "!");
        return "redirect:/settings/master-data/departments";
    }

    // DELETE ACTIONS
    @PostMapping("/regions/delete/{id}")
    public String deleteRegion(@PathVariable Long id, jakarta.servlet.http.HttpServletRequest request, RedirectAttributes redirectAttributes) {
        regionRepository.deleteById(id);
        auditService.logWithContext(request, "DELETE_REGION", "Region", id, "Menghapus Region ID: " + id);
        redirectAttributes.addFlashAttribute("successMessage", "Region berhasil dihapus!");
        return "redirect:/settings/master-data/regions";
    }

    @PostMapping("/jobs/delete/{id}")
    public String deleteJob(@PathVariable String id, jakarta.servlet.http.HttpServletRequest request, RedirectAttributes redirectAttributes) {
        jobRepository.deleteById(id);
        auditService.logWithContext(request, "DELETE_JOB", "Job", null, "Menghapus Jabatan ID: " + id);
        redirectAttributes.addFlashAttribute("successMessage", "Jabatan berhasil dihapus!");
        return "redirect:/settings/master-data/jobs";
    }

    @PostMapping("/locations/delete/{id}")
    public String deleteLocation(@PathVariable Long id, jakarta.servlet.http.HttpServletRequest request, RedirectAttributes redirectAttributes) {
        locationRepository.deleteById(id);
        auditService.logWithContext(request, "DELETE_LOCATION", "Location", id, "Menghapus Lokasi Cabang ID: " + id);
        redirectAttributes.addFlashAttribute("successMessage", "Lokasi berhasil dihapus!");
        return "redirect:/settings/master-data/locations";
    }

    @PostMapping("/departments/delete/{id}")
    public String deleteDepartment(@PathVariable Long id, jakarta.servlet.http.HttpServletRequest request, RedirectAttributes redirectAttributes) {
        departmentRepository.deleteById(id);
        auditService.logWithContext(request, "DELETE_DEPARTMENT", "Department", id, "Menghapus Departemen ID: " + id);
        redirectAttributes.addFlashAttribute("successMessage", "Departemen berhasil dihapus!");
        return "redirect:/settings/master-data/departments";
    }
}
