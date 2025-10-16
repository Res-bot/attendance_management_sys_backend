package com.ams.attendance.controller;

import com.ams.attendance.dto.AttendanceDTO;
import com.ams.attendance.entity.User;
import com.ams.attendance.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    private Long getCurrentUserId(Authentication authentication) {
        if (authentication.getPrincipal() instanceof User user) {
            return user.getId();
        }
        throw new SecurityException("Unable to retrieve user ID from authentication context.");
    }

    @PostMapping("/checkin")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'STUDENT', 'TEACHER')")
    public ResponseEntity<AttendanceDTO> checkIn(
            Authentication authentication,
            @RequestParam(required = false) String notes) {

        Long userId = getCurrentUserId(authentication);
        LocalDateTime checkInTime = LocalDateTime.now();
        
        AttendanceDTO record = attendanceService.checkIn(
            userId, 
            checkInTime, 
            notes != null ? notes : "Self Check-in via API"
        );
        return new ResponseEntity<>(record, HttpStatus.CREATED);
    }

    @PutMapping("/checkout")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'STUDENT', 'TEACHER')")
    public ResponseEntity<AttendanceDTO> checkOut(Authentication authentication) {
        
        Long userId = getCurrentUserId(authentication);
        LocalDateTime checkOutTime = LocalDateTime.now();

        AttendanceDTO record = attendanceService.checkOut(userId, checkOutTime);
        return ResponseEntity.ok(record);
    }

    @GetMapping("/report/self")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'STUDENT', 'TEACHER')")
    public ResponseEntity<List<AttendanceDTO>> getPersonalReport(
            Authentication authentication,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        Long userId = getCurrentUserId(authentication);
        
        List<AttendanceDTO> report = attendanceService.getAttendanceReportByUser(
            userId, 
            startDate, 
            endDate
        );
        return ResponseEntity.ok(report);
    }
}

