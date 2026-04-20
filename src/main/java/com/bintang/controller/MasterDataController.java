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
    public String listAll(Model model) {
        model.addAttribute("regions", regionRepository.findAll());
        model.addAttribute("countries", countryRepository.findAll());
        model.addAttribute("locations", locationRepository.findAll());
        model.addAttribute("departments", departmentRepository.findAll());
        model.addAttribute("jobs", jobRepository.findAll());
        model.addAttribute("content", "settings/master-data");
        return "layout";
    }

    // REGIONS
    @PostMapping("/regions")
    public String saveRegion(@ModelAttribute Region region) {
        regionRepository.save(region);
        auditService.log("CREATE_REGION", "Admin", "Region", region.getId(), "Menambahkan/Mengubah Region: " + region.getName());
        return "redirect:/settings/master-data?success=region";
    }

    // COUNTRIES
    @PostMapping("/countries")
    public String saveCountry(@ModelAttribute Country country) {
        countryRepository.save(country);
        auditService.log("CREATE_COUNTRY", "Admin", "Country", null, "Menambahkan/Mengubah Country: " + country.getName());
        return "redirect:/settings/master-data?success=country";
    }

    // LOCATIONS
    @PostMapping("/locations")
    public String saveLocation(@ModelAttribute Location location) {
        locationRepository.save(location);
        auditService.log("CREATE_LOCATION", "Admin", "Location", location.getId(), "Menambahkan/Mengubah Lokasi: " + location.getCity());
        return "redirect:/settings/master-data?success=location";
    }

    // JOBS
    @PostMapping("/jobs")
    public String saveJob(@ModelAttribute Job job) {
        jobRepository.save(job);
        auditService.log("CREATE_JOB", "Admin", "Job", null, "Menambahkan/Mengubah Pekerjaan: " + job.getTitle());
        return "redirect:/settings/master-data?success=job";
    }

    // DEPARTMENTS
    @PostMapping("/departments")
    public String saveDepartment(@ModelAttribute Department dept) {
        departmentRepository.save(dept);
        auditService.log("CREATE_DEPARTMENT", "Admin", "Department", dept.getId(), "Menambahkan/Mengubah Departemen: " + dept.getName());
        return "redirect:/settings/master-data?success=dept";
    }
}
