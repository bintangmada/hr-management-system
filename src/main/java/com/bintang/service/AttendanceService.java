package com.bintang.service;

import com.bintang.entity.Attendance;
import com.bintang.repository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;
    
    @Autowired
    private AttendanceLocationRepository locationRepository;

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

    public void checkIn(Long employeeId, double lat, double lng) {
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
        attendance.setCheckInTime(LocalDateTime.now());
        attendance.setLatitude(lat);
        attendance.setLongitude(lng);
        attendance.setIsWithinGeo(isWithinGeo);
        attendance.setStatus(isWithinGeo ? "HADIR (" + locationName + ")" : "DILUAR_AREA");
        
        attendanceRepository.save(attendance);
    }
}
