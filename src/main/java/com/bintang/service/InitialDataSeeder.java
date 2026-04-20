package com.bintang.service;

import com.bintang.entity.*;
import com.bintang.repository.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class InitialDataSeeder {

    @Autowired private RegionRepository regionRepository;
    @Autowired private JobRepository jobRepository;
    @Autowired private DepartmentRepository departmentRepository;
    @Autowired private LocationRepository locationRepository;
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private SettingRepository settingRepository;

    @PostConstruct
    public void seed() {
        if (regionRepository.count() == 0) {
            // Seed Regions
            Region asia = regionRepository.save(new Region(null, "Asia"));
            
            // Seed Locations
            Location jakarta = locationRepository.save(new Location(null, "Jl. Sudirman No. 1", "12190", "Jakarta", "DKI Jakarta", "ID"));
            
            // Seed Jobs
            Job manager = jobRepository.save(new Job("MNGR", "Manager HR", 15000000.0, 25000000.0));
            Job staff = jobRepository.save(new Job("STAF", "Staff Admin", 5000000.0, 10000000.0));
            
            // Seed Departments
            Department hcm = departmentRepository.save(new Department(null, "Human Capital Management", null, jakarta.getId()));
            
            // Seed Employees
            Employee bintang = new Employee();
            bintang.setNik("2026001");
            bintang.setFirstName("Bintang");
            bintang.setLastName("Mada");
            bintang.setEmail("bintang@perusahaan.com");
            bintang.setHireDate(LocalDate.now().minusYears(1));
            bintang.setJobId(manager.getId());
            bintang.setDepartmentId(hcm.getId());
            employeeRepository.save(bintang);
            
            // Seed Settings
            if (settingRepository.findBySettingKey("OFFICE_RADIUS").isEmpty()) {
                settingRepository.save(new Setting(null, "OFFICE_RADIUS", "100", "Radius absensi dalam meter"));
            }
        }
    }
}
