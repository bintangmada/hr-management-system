package com.bintang.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class DashboardStatsDTO {
    // Summary Stats
    private long totalEmployees;
    private long presentToday;
    private long lateToday;
    private long onLeaveToday;
    private long absentToday;
    
    // Attendance Trend (Last 7 Days)
    private List<String> trendLabels; // e.g., ["15 Apr", "16 Apr", ...]
    private List<Long> trendData;     // e.g., [10, 12, 11, ...]
    
    // Department Distribution
    private List<String> deptLabels;  // e.g., ["IT", "HR", "Sales"]
    private List<Long> deptData;      // e.g., [5, 2, 8]
    
    // Recent Activity
    private List<com.bintang.entity.Attendance> recentActivities;
}
