package com.ams.attendance.service;

import com.ams.attendance.dto.AttendanceDTO;
import com.ams.attendance.dto.LeaveRequestDTO;
import com.ams.attendance.dto.DashboardStatsDTO;
import com.ams.attendance.entity.Attendance;
import com.ams.attendance.entity.User;
import com.ams.attendance.entity.LeaveRequest;
import com.ams.attendance.enums.AttendanceStatus;
import com.ams.attendance.enums.LeaveStatus;
import com.ams.attendance.repository.AttendanceRepository;
import com.ams.attendance.repository.LeaveRepository;
import com.ams.attendance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {
    
    private final AttendanceRepository attendanceRepository;
    private final LeaveRepository leaveRepository;
    private final UserRepository userRepository; // Needed for Leave application

    private LeaveRequestDTO convertToLeaveDto(LeaveRequest request) {
        LeaveRequestDTO dto = new LeaveRequestDTO();
        dto.setId(request.getId());
        dto.setApplicantId(request.getApplicant().getId());
        dto.setApplicantName(request.getApplicant().getName());
        dto.setStartDate(request.getStartDate());
        dto.setEndDate(request.getEndDate());
        dto.setReason(request.getReason());
        dto.setStatus(request.getStatus());
        dto.setRequestedOn(request.getRequestedOn());
        return dto;
    }

    
    public AttendanceDTO checkIn(Long userId, LocalDateTime time) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Student user not found."));
        
        Attendance attendance = new Attendance();
        attendance.setUser(user);
        attendance.setCheckInTime(time);
        attendance.setStatus(AttendanceStatus.PRESENT);
        
        Attendance savedAttendance = attendanceRepository.save(attendance);
        
        AttendanceDTO dto = new AttendanceDTO();
        dto.setUserId(userId);
        dto.setCheckInTime(savedAttendance.getCheckInTime());
        dto.setStatus(AttendanceStatus.PRESENT);
        return dto;
    }

    
    public LeaveRequestDTO applyForClassLeave(Long userId, LeaveRequestDTO requestDto) {
        User applicant = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Applicant user not found."));

        LeaveRequest request = new LeaveRequest();
        request.setApplicant(applicant);
        request.setStartDate(requestDto.getStartDate());
        request.setEndDate(requestDto.getEndDate());
        request.setReason(requestDto.getReason());
        request.setStatus(LeaveStatus.PENDING);
        request.setRequestedOn(LocalDateTime.now());

        LeaveRequest savedRequest = leaveRepository.save(request);
        return convertToLeaveDto(savedRequest);
    }

    
    public Double getAttendancePercentage(Long userId, Long courseId) {
        List<Attendance> allRecords = attendanceRepository
            .findByUser_IdAndCourse_Id(userId, courseId);
        
        long totalClasses = allRecords.size();
        if (totalClasses == 0) return 100.0; 

        long presentCount = allRecords.stream()
            .filter(a -> a.getStatus() == AttendanceStatus.PRESENT || a.getStatus() == AttendanceStatus.LATE)
            .count();
        
        return (double) Math.round((presentCount * 100.0 / totalClasses) * 100) / 100;
    }
    
    
    public List<String> getLowAttendanceAlerts(Long userId) {
        List<Long> enrolledCourseIds = List.of(1L, 2L, 3L); 
        final double THRESHOLD = 75.0;
        
        return enrolledCourseIds.stream()
            .map(courseId -> {
                Double percentage = getAttendancePercentage(userId, courseId);
                if (percentage < THRESHOLD) {
                    return "Alert: Attendance for Course " + courseId + " is " + percentage + "%, which is below the required 75%.";
                }
                return null;
            })
            .filter(alert -> alert != null)
            .collect(Collectors.toList());
    }

    
    public DashboardStatsDTO getDashboardStats(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Student user not found."));
        
        List<Attendance> allStudentAttendance = attendanceRepository.findByUser_Id(userId);
        
        long present = allStudentAttendance.stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT).count();
        long absent = allStudentAttendance.stream().filter(a -> a.getStatus() == AttendanceStatus.ABSENT).count();
        long totalDays = allStudentAttendance.size();

        Double attendancePct = (totalDays > 0) ? (double) Math.round((present * 100.0 / totalDays) * 100) / 100 : 100.0;
        
        DashboardStatsDTO dto = new DashboardStatsDTO();
        dto.setUserName(user.getName());
        dto.setRole(user.getRole().name());
        
        dto.setTotalAttendanceRecords((int) totalDays);
        dto.setPresentCount(present);
        dto.setAbsentCount(absent);
        dto.setAttendancePercentage(attendancePct);
        
        dto.setApprovedLeaveCount(leaveRepository.countByApplicant_IdAndStatus(userId, LeaveStatus.APPROVED));
        dto.setPendingLeaveCount(leaveRepository.countByApplicant_IdAndStatus(userId, LeaveStatus.PENDING));

        
        return dto;
    }
}
