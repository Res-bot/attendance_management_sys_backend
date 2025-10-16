package com.ams.attendance.controller;

import com.ams.attendance.dto.CourseDTO;
import com.ams.attendance.dto.DepartmentDTO;
import com.ams.attendance.dto.UserDTO;
import com.ams.attendance.enums.UserRole;
import com.ams.attendance.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") 
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers(@RequestParam(required = false) UserRole role) {
        if (role != null) {
            return ResponseEntity.ok(adminService.findUsersByRole(role));
        }
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/users/department/{department}")
    public ResponseEntity<List<UserDTO>> getUsersByDepartment(@PathVariable String department) {
        return ResponseEntity.ok(adminService.findUsersByDepartment(department));
    }

    @GetMapping("/users/designation/{designation}")
    public ResponseEntity<List<UserDTO>> getUsersByDesignation(@PathVariable String designation) {
        return ResponseEntity.ok(adminService.findUsersByDesignation(designation));
    }

    @GetMapping("/users/department/{department}/role/{role}")
    public ResponseEntity<List<UserDTO>> getUsersByDepartmentAndRole(
            @PathVariable String department,
            @PathVariable UserRole role) {
        return ResponseEntity.ok(adminService.findUsersByDepartmentAndRole(department, role));
    }

    @GetMapping("/users/search")
    public ResponseEntity<List<UserDTO>> searchUsersByEmail(@RequestParam String emailKeyword) {
        return ResponseEntity.ok(adminService.searchUsersByEmail(emailKeyword));
    }

    @GetMapping("/users/role/{role}/sorted")
    public ResponseEntity<List<UserDTO>> getUsersByRoleSorted(@PathVariable UserRole role) {
        return ResponseEntity.ok(adminService.findUsersByRoleSorted(role));
    }

    @PostMapping("/users")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDto) {
        UserDTO createdUser = adminService.createUser(userDto);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDto) {
        UserDTO updatedUser = adminService.updateUser(id, userDto);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/departments")
    public ResponseEntity<DepartmentDTO> createDepartment(@Valid @RequestBody DepartmentDTO dto) {
        return new ResponseEntity<>(adminService.createDepartment(dto), HttpStatus.CREATED);
    }
    
    @PostMapping("/courses")
    public ResponseEntity<CourseDTO> createCourse(@Valid @RequestBody CourseDTO dto) {
        return new ResponseEntity<>(adminService.createCourse(dto), HttpStatus.CREATED);
    }    
}

