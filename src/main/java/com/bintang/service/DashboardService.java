package com.bintang.service;

import com.bintang.dto.DashboardStatsDTO;
import com.bintang.repository.AttendanceRepository;
import com.bintang.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private com.bintang.repository.LeaveRequestRepository leaveRequestRepository;

    public DashboardStatsDTO getAdminStats() {
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        // 1. Summary Cards
        long totalEmp = employeeRepository.count();
        long onLeave = leaveRequestRepository.countApprovedLeaveOnDate(LocalDate.now());
        
        // Count unique employees present, but exclude those on leave for consistent summary stats
        long present = attendanceRepository.countUniquePresent(startOfDay, endOfDay);
        if (present > (totalEmp - onLeave)) {
            present = totalEmp - onLeave; // Cap presence to expected availability
        }
        
        long late = attendanceRepository.countUniqueLate(startOfDay, endOfDay);
        long absent = totalEmp - present - onLeave;
        if (absent < 0) absent = 0; 

        // 2. Attendance Trend (Last 7 Days)
        List<String> trendLabels = new ArrayList<>();
        List<Long> trendData = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM");

        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime start = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime end = LocalDateTime.of(date, LocalTime.MAX);
            
            trendLabels.add(date.format(formatter));
            trendData.add(attendanceRepository.countByCheckInTimeBetween(start, end));
        }

        // 3. Department Distribution
        List<String> deptLabels = new ArrayList<>();
        List<Long> deptData = new ArrayList<>();
        List<Object[]> deptCounts = employeeRepository.countEmployeesByDepartment();
        
        for (Object[] row : deptCounts) {
            deptLabels.add((String) row[0]);
            deptData.add((Long) row[1]);
        }

        // 4. Recent Activities
        List<com.bintang.entity.Attendance> recent = attendanceRepository.findTop5Recent(PageRequest.of(0, 5));

        return DashboardStatsDTO.builder()
                .totalEmployees(totalEmp)
                .presentToday(present)
                .lateToday(late)
                .onLeaveToday(onLeave)
                .absentToday(absent)
                .trendLabels(trendLabels)
                .trendData(trendData)
                .deptLabels(deptLabels)
                .deptData(deptData)
                .recentActivities(recent)
                .build();
    }
}
