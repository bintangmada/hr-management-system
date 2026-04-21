package com.bintang.service;

import com.bintang.entity.Attendance;
import com.bintang.entity.AttendanceLocation;
import com.bintang.repository.AttendanceLocationRepository;
import com.bintang.repository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;
    
    @Autowired
    private AttendanceLocationRepository locationRepository;

    @Autowired
    private com.bintang.service.AuditService auditService;

    @Autowired
    private com.bintang.repository.EmployeeRepository employeeRepository;

    @Autowired
    private SettingService settingService;

    @Autowired
    private AppNotificationService notificationService;

    public boolean isPointInPolygon(double lat, double lng, String polygonJson) {
        if (polygonJson == null || polygonJson.equals("[]")) return false;
        try {
            ObjectMapper mapper = new ObjectMapper();
            double[][] poly = mapper.readValue(polygonJson, double[][].class);
            
            boolean isInside = false;
            int n = poly.length;
            for (int i = 0, j = n - 1; i < n; j = i++) {
                if (((poly[i][0] > lat) != (poly[j][0] > lat)) &&
                    (lng < (poly[j][1] - poly[i][1]) * (lat - poly[i][0]) / (poly[j][0] - poly[i][0]) + poly[i][1])) {
                    isInside = !isInside;
                }
            }
            return isInside;
        } catch (Exception e) {
            return false;
        }
    }

    public Attendance getTodayAttendance(Long employeeId) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        return attendanceRepository.findTodayAttendance(employeeId, startOfDay, endOfDay).orElse(null);
    }

    public void checkIn(Long employeeId, String role, double lat, double lng) {
        List<AttendanceLocation> locations = locationRepository.findAll();
        boolean isWithinGeo = false;
        String locationName = "DILUAR_AREA";
        
        for (AttendanceLocation loc : locations) {
            if (isPointInPolygon(lat, lng, loc.getPolygonJson())) {
                isWithinGeo = true;
                locationName = loc.getName();
                break;
            }
        }
        
        Attendance attendance = new Attendance();
        attendance.setEmployeeId(employeeId);
        LocalDateTime now = LocalDateTime.now();
        attendance.setCheckInTime(now);
        attendance.setLatitude(lat);
        attendance.setLongitude(lng);
        attendance.setIsWithinGeo(isWithinGeo);
        
        // Late calculation (e.g., > 09:00 AM)
        boolean isLate = now.toLocalTime().isAfter(java.time.LocalTime.of(9, 0));
        attendance.setIsLate(isLate);
        
        attendance.setStatus(isWithinGeo ? (isLate ? "TERLAMBAT" : "HADIR") + " (" + locationName + ")" : "DILUAR_AREA");
        
        attendanceRepository.save(attendance);

        // --- VALIDATION AND NOTIFICATION ---
        
        // Find which location matched (if any)
        AttendanceLocation currentLoc = locations.stream()
                .filter(l -> isPointInPolygon(lat, lng, l.getPolygonJson()))
                .findFirst()
                .orElse(null);

        employeeRepository.findById(employeeId).ifPresent(emp -> {
            try {
                // 1. Determine Min Check-in Time (Location Specific OR Global)
                String minTimeStr = (currentLoc != null && currentLoc.getMinCheckInTime() != null && !currentLoc.getMinCheckInTime().isBlank()) 
                                    ? currentLoc.getMinCheckInTime() 
                                    : settingService.getSettingValue("MIN_CHECKIN_TIME", "07:00");
                
                LocalTime minTime = LocalTime.parse(minTimeStr);
                if (now.toLocalTime().isBefore(minTime)) {
                    String locNameMsg = (currentLoc != null) ? " di " + currentLoc.getName() : "";
                    notificationService.send(emp.getNik(), "Peringatan Check-in Awal", 
                        "Anda melakukan check-in pada " + now.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")) + locNameMsg +
                        ". Jam minimal check-in yang diizinkan adalah " + minTimeStr + ".", 
                        "/attendance");
                }
            } catch (Exception e) {
                // Ignore if setting is invalid
            }

            // Add Audit Log
            auditService.log("CHECK_IN", role, emp.getFirstName() + " " + emp.getLastName(), emp.getNik(), 
                "Attendance", attendance.getId(), "Check-in: (" + attendance.getStatus() + ")");
        });
    }

    public void checkOut(Long attendanceId, String role, double lat, double lng) {
        Attendance attendance = attendanceRepository.findById(attendanceId).orElseThrow();
        attendance.setCheckOutTime(LocalDateTime.now());
        attendanceRepository.save(attendance);

        // --- VALIDATION AND NOTIFICATION ---
        
        // Find which location matched (based on current coordinates)
        AttendanceLocation currentLoc = locationRepository.findAll().stream()
                .filter(l -> isPointInPolygon(lat, lng, l.getPolygonJson()))
                .findFirst()
                .orElse(null);

        employeeRepository.findById(attendance.getEmployeeId()).ifPresent(emp -> {
            try {
                // 1. Determine Min Check-out Time (Location Specific OR Global)
                String minTimeStr = (currentLoc != null && currentLoc.getMinCheckOutTime() != null && !currentLoc.getMinCheckOutTime().isBlank()) 
                                    ? currentLoc.getMinCheckOutTime() 
                                    : settingService.getSettingValue("MIN_CHECKOUT_TIME", "16:00");
                
                LocalTime minTime = LocalTime.parse(minTimeStr);
                LocalDateTime now = LocalDateTime.now();
                if (now.toLocalTime().isBefore(minTime)) {
                    String locNameMsg = (currentLoc != null) ? " di " + currentLoc.getName() : "";
                    notificationService.send(emp.getNik(), "Peringatan Check-out Awal", 
                        "Anda melakukan check-out pada " + now.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")) + locNameMsg +
                        ". Jam minimal pulang yang diizinkan adalah " + minTimeStr + ".", 
                        "/attendance");
                }
            } catch (Exception e) {
                // Ignore if setting is invalid
            }

            // Add Audit Log
            auditService.log("CHECK_OUT", role, emp.getFirstName() + " " + emp.getLastName(), emp.getNik(), 
                "Attendance", attendance.getId(), "Check-out dilakukan");
        });
    }

    public List<Attendance> getAttendanceHistory(Long employeeId) {
        return attendanceRepository.findByEmployeeIdOrderByCheckInTimeDesc(employeeId);
    }
}
