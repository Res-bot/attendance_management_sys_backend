package com.ams.attendance.controller;

import com.ams.attendance.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/teacher/email")
@RequiredArgsConstructor
@PreAuthorize("hasRole('TEACHER')") 
public class EmailController {

    private final TeacherService teacherService;
    
    
    @PostMapping("/notify-absent/{courseId}")
    public ResponseEntity<String> notifyAbsentStudents(
            @PathVariable Long courseId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate attendanceDate) {

        teacherService.notifyAbsentStudents(courseId, attendanceDate);
        
        return ResponseEntity.ok("Absence notification process started for Course " 
                                + courseId + " on " + attendanceDate.toString());
    }
}