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

    @Autowired private AttendanceLocationRepository attendanceLocationRepository;

    @PostConstruct
    public void seed() {
        if (regionRepository.count() == 0) {
            // Seed Regions
            Region asia = regionRepository.save(new Region(null, "Asia"));
            
            // Seed Locations (Organizational)
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
            bintang.setPassword("admin123");
            bintang.setRole("ADMIN");
            employeeRepository.save(bintang);
        } else {
            // Jika data sudah ada, pastikan user demo punya password dan role
            employeeRepository.findByNik("2026001").ifPresent(emp -> {
                boolean changed = false;
                if (emp.getPassword() == null || emp.getPassword().isEmpty()) {
                    emp.setPassword("admin123");
                    changed = true;
                }
                if (emp.getRole() == null || emp.getRole().isEmpty()) {
                    emp.setRole("ADMIN");
                    changed = true;
                }
                if (changed) employeeRepository.save(emp);
            });
        }

        // Seed Attendance Location (Geofencing)
        if (attendanceLocationRepository.count() == 0) {
            AttendanceLocation monas = new AttendanceLocation();
            monas.setName("Kantor Pusat (Monas Area)");
            monas.setPolygonJson("[[-6.174,106.8256],[-6.174,106.8276],[-6.176,106.8276],[-6.176,106.8256]]");
            monas.setRadius(200);
            monas.setDescription("Area testing geofencing di pusat Jakarta.");
            attendanceLocationRepository.save(monas);
        }

        // Seed Settings
        if (settingRepository.findBySettingKey("OFFICE_RADIUS").isEmpty()) {
            settingRepository.save(new Setting(null, "OFFICE_RADIUS", "100", "Radius absensi dalam meter"));
        }
    }
}
