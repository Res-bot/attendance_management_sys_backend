package com.ams.attendance.service;

import com.ams.attendance.dto.AttendanceDTO;
import com.ams.attendance.entity.Attendance;
import com.ams.attendance.entity.User;
import com.ams.attendance.enums.AttendanceStatus;
import com.ams.attendance.repository.AttendanceRepository;
import com.ams.attendance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository; 

    private AttendanceDTO convertToDto(Attendance attendance) {
        AttendanceDTO dto = new AttendanceDTO();
        dto.setId(attendance.getId());
        dto.setUserId(attendance.getUser().getId());
        dto.setUserName(attendance.getUser().getName());
        dto.setCheckInTime(attendance.getCheckInTime());
        dto.setCheckOutTime(attendance.getCheckOutTime());
        dto.setStatus(attendance.getStatus());
        dto.setNotes(attendance.getNotes());
        
        if (attendance.getCheckOutTime() != null) {
            Duration duration = Duration.between(attendance.getCheckInTime(), attendance.getCheckOutTime());
            dto.setTotalHours(duration.toMinutes() / 60.0);
        }
        dto.setAttendanceDate(attendance.getCheckInTime().toLocalDate());
        return dto;
    }

    
    public AttendanceDTO checkIn(Long userId, LocalDateTime checkInTime, String notes) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found."));

        
        Attendance attendance = new Attendance();
        attendance.setUser(user);
        attendance.setCheckInTime(checkInTime);
        attendance.setStatus(AttendanceStatus.PRESENT); 
        attendance.setNotes(notes);

        Attendance savedAttendance = attendanceRepository.save(attendance);
        return convertToDto(savedAttendance);
    }

    
    public AttendanceDTO checkOut(Long userId, LocalDateTime checkOutTime) {
        Attendance latestAttendance = attendanceRepository
            .findTopByUser_IdAndCheckOutTimeIsNullOrderByCheckInTimeDesc(userId)
            .orElseThrow(() -> new RuntimeException("No active check-in found for this user."));

        latestAttendance.setCheckOutTime(checkOutTime);
        

        Attendance updatedAttendance = attendanceRepository.save(latestAttendance);
        return convertToDto(updatedAttendance);
    }


    public List<AttendanceDTO> getAttendanceReportByUser(Long userId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay(); 

        List<Attendance> records = attendanceRepository
            .findByUser_IdAndCheckInTimeBetweenOrderByCheckInTimeAsc(userId, startDateTime, endDateTime);

        return records.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
}
