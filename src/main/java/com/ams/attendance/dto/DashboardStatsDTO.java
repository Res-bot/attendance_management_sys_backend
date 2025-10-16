package com.ams.attendance.dto;

import lombok.Data;

import java.util.Map;

@Data
public class DashboardStatsDTO {

    
    private String userName;
    private String role;
    
    private int totalWorkingDays; 
    private int totalAttendanceRecords;
    
    
    private Double attendancePercentage; 
    
    private Double totalHoursPresent; 
    private Double averageCheckInTime; 

    private long presentCount;
    private long absentCount;
    private long lateCount; 
    
    
    private long approvedLeaveCount;
    private long pendingLeaveCount;

    
    private long totalUsers;
    
    private Map<String, Double> departmentAttendanceBreakdown; 
    
    private Map<String, Double> topDefaulters; 
}

