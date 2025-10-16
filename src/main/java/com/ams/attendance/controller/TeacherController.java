package com.ams.attendance.controller;

import com.ams.attendance.dto.AttendanceDTO;
import com.ams.attendance.dto.LeaveRequestDTO;
import com.ams.attendance.enums.LeaveStatus;
import com.ams.attendance.service.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
@PreAuthorize("hasRole('TEACHER')") 
public class TeacherController {

    private final TeacherService teacherService;

    @PostMapping("/attendance/mark/{courseId}")
    public ResponseEntity<Void> markAttendanceByClass(
            @PathVariable Long courseId,
            @Valid @RequestBody List<AttendanceDTO> attendanceList) {
        
        teacherService.markAttendanceByClass(courseId, attendanceList);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/leave/pending")
    public ResponseEntity<List<LeaveRequestDTO>> getPendingLeaveRequests() {
        return ResponseEntity.ok(teacherService.getPendingLeaveRequests()); 
    }

    @PutMapping("/leave/review/{requestId}")
    public ResponseEntity<LeaveRequestDTO> reviewLeaveRequest(
            @PathVariable Long requestId,
            @RequestParam LeaveStatus status,
            @RequestParam(required = false) String notes) {
        
       
        Long approverId = 1L; 

        LeaveRequestDTO updatedRequest = teacherService.reviewLeaveRequest(requestId, approverId, status, notes);
        return ResponseEntity.ok(updatedRequest);
    }
    
    @GetMapping("/reports/course/{courseId}")
    public ResponseEntity<List<AttendanceDTO>> getCourseReport(
            @PathVariable Long courseId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        
        List<AttendanceDTO> report = teacherService.getCourseAttendanceReport(courseId, from, to);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/courses/{courseId}/students")
    public ResponseEntity<List<Long>> getStudentsInCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(teacherService.getStudentsByCourse(courseId));
    }
}