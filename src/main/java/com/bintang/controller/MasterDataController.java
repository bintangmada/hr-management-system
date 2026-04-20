package com.bintang.controller;

import com.bintang.entity.*;
import com.bintang.repository.*;
import com.bintang.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String saveRegion(@ModelAttribute Region region) {
        boolean isNew = (region.getId() == null);
        regionRepository.save(region);
        auditService.log(isNew ? "CREATE_REGION" : "UPDATE_REGION", "Admin", "Region", region.getId(), 
            (isNew ? "Menambahkan" : "Mengubah") + " Region: " + region.getName());
        return "redirect:/settings/master-data/regions?success";
    }

    @PostMapping("/jobs")
    public String saveJob(@ModelAttribute Job job) {
        boolean isNew = !jobRepository.existsById(job.getId());
        jobRepository.save(job);
        auditService.log(isNew ? "CREATE_JOB" : "UPDATE_JOB", "Admin", "Job", null, 
            (isNew ? "Menambahkan" : "Mengubah") + " Jabatan: " + job.getTitle());
        return "redirect:/settings/master-data/jobs?success";
    }

    @PostMapping("/locations")
    public String saveLocation(@ModelAttribute Location location) {
        boolean isNew = (location.getId() == null);
        locationRepository.save(location);
        auditService.log(isNew ? "CREATE_LOCATION" : "UPDATE_LOCATION", "Admin", "Location", location.getId(), 
            (isNew ? "Menambahkan" : "Mengubah") + " Lokasi Cabang: " + location.getCity());
        return "redirect:/settings/master-data/locations?success";
    }

    @PostMapping("/departments")
    public String saveDepartment(@ModelAttribute Department dept) {
        boolean isNew = (dept.getId() == null);
        departmentRepository.save(dept);
        auditService.log(isNew ? "CREATE_DEPARTMENT" : "UPDATE_DEPARTMENT", "Admin", "Department", dept.getId(), 
            (isNew ? "Menambahkan" : "Mengubah") + " Departemen: " + dept.getName());
        return "redirect:/settings/master-data/departments?success";
    }

    // DELETE ACTIONS
    @PostMapping("/regions/delete/{id}")
    public String deleteRegion(@PathVariable Long id) {
        regionRepository.deleteById(id);
        auditService.log("DELETE_REGION", "Admin", "Region", id, "Menghapus Region ID: " + id);
        return "redirect:/settings/master-data/regions?deleted";
    }

    @PostMapping("/jobs/delete/{id}")
    public String deleteJob(@PathVariable String id) {
        jobRepository.deleteById(id);
        auditService.log("DELETE_JOB", "Admin", "Job", null, "Menghapus Jabatan ID: " + id);
        return "redirect:/settings/master-data/jobs?deleted";
    }

    @PostMapping("/locations/delete/{id}")
    public String deleteLocation(@PathVariable Long id) {
        locationRepository.deleteById(id);
        auditService.log("DELETE_LOCATION", "Admin", "Location", id, "Menghapus Lokasi Cabang ID: " + id);
        return "redirect:/settings/master-data/locations?deleted";
    }

    @PostMapping("/departments/delete/{id}")
    public String deleteDepartment(@PathVariable Long id) {
        departmentRepository.deleteById(id);
        auditService.log("DELETE_DEPARTMENT", "Admin", "Department", id, "Menghapus Departemen ID: " + id);
        return "redirect:/settings/master-data/departments?deleted";
    }
}
