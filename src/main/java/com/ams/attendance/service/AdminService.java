package com.ams.attendance.service;

import com.ams.attendance.dto.UserDTO;
import com.ams.attendance.dto.DepartmentDTO;
import com.ams.attendance.dto.CourseDTO;
import com.ams.attendance.entity.Course;
import com.ams.attendance.entity.Department;
import com.ams.attendance.entity.User;
import com.ams.attendance.enums.UserRole;
import com.ams.attendance.repository.CourseRepository;
import com.ams.attendance.repository.DepartmentRepository;
import com.ams.attendance.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService implements UserDetailsService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository; 
    private final PasswordEncoder passwordEncoder;
    private final CourseRepository courseRepository;

    @PostConstruct
    private void createAdminUser() {
        Optional<User> optionalUser = userRepository.findByRole(UserRole.ADMIN);
        if (optionalUser.isEmpty()) {
            User admin = new User();
            admin.setName("Default Admin");
            admin.setEmail("admin@ams.com");
            admin.setPassword(passwordEncoder.encode("admin123")); 
            admin.setRole(UserRole.ADMIN);
            admin.setDepartment("IT"); 
            userRepository.save(admin);
            System.out.println("Default Admin User created!");
        } else {
            System.out.println("Admin User Already exists.");
        }
    }
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
    
    // Create User (Registration/Admin Add User)
    public UserDTO createUser(UserDTO userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("User with this email already exists.");
        }
        
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword())); 
        user.setRole(userDto.getRole() != null ? userDto.getRole() : UserRole.EMPLOYEE); 
        user.setDepartment(userDto.getDepartment());
        user.setDesignation(userDto.getDesignation());
        
        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }
    
    // Get User by ID
    public UserDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));
        return convertToDto(user);
    }
    
    // Update User
    public UserDTO updateUser(Long userId, UserDTO userDto) {
        User existingUser = userRepository.findById(userId)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));

        if (userDto.getName() != null) existingUser.setName(userDto.getName());
        if (userDto.getDepartment() != null) existingUser.setDepartment(userDto.getDepartment());
        if (userDto.getDesignation() != null) existingUser.setDesignation(userDto.getDesignation());
        
        
        if (userDto.getRole() != null) {
            existingUser.setRole(userDto.getRole());
        }
        
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        User updatedUser = userRepository.save(existingUser);
        return convertToDto(updatedUser);
    }
    
    // Delete User
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UsernameNotFoundException("User not found with ID: " + userId);
        }
        userRepository.deleteById(userId);
    }


    // Get All Users
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }
    
    
    //  Department Management (Create)
    private DepartmentDTO convertToDto(Department department) {
    DepartmentDTO dto = new DepartmentDTO();
    dto.setId(department.getId());
    dto.setName(department.getName());
    dto.setDescription(department.getDescription());
    return dto;
}

    public DepartmentDTO createDepartment(DepartmentDTO dto) {
        if (departmentRepository.findByName(dto.getName()).isPresent()) {
            throw new RuntimeException("Department already exists: " + dto.getName());
        }
        Department department = new Department();
        department.setName(dto.getName());
        department.setDescription(dto.getDescription());
        Department savedDept = departmentRepository.save(department);
        return convertToDto(savedDept);
    }
    
    // Course Management (Create)
    private CourseDTO convertToDto(Course course) {
    CourseDTO dto = new CourseDTO();
    dto.setId(course.getId());
    dto.setName(course.getName());
    dto.setCourseCode(course.getCourseCode());
    dto.setCredits(course.getCredits());
    // Only set department ID if the course is linked to one
    if (course.getDepartment() != null) {
        dto.setDepartmentId(course.getDepartment().getId());
    }
    return dto;
}

    public CourseDTO createCourse(CourseDTO dto) {
        if (courseRepository.findByCourseCode(dto.getCourseCode()).isPresent()) {
            throw new RuntimeException("Course with this code already exists: " + dto.getCourseCode());
        }
        
        Department department = departmentRepository.findById(dto.getDepartmentId())
            .orElseThrow(() -> new RuntimeException("Department not found."));

        Course course = new Course();
        course.setCourseCode(dto.getCourseCode());
        course.setName(dto.getName());
        course.setCredits(dto.getCredits());
        course.setDepartment(department);
        
        Course savedCourse = courseRepository.save(course);
        return convertToDto(savedCourse);
    }

    // 3. Find Users Filtered by Role (Feature 2)
    public List<UserDTO> findUsersByRole(UserRole role) {
        // The repository method is assumed to return List<User> or Optional<List<User>>
        // If the repository method is `List<User> findByRole(UserRole role);`, this is correct.
        return userRepository.findByRole(role).stream().map(this::convertToDto).toList();
    }
    
    // 4. Bulk User Upload (Placeholder Logic)
    public String uploadBulkUsers(byte[] excelData) {
        // This is where you would integrate Apache POI
        System.out.println("Processing bulk upload of size: " + excelData.length + " bytes.");
        // Example logic: Process the file and return a status message
        
        // In a real scenario, this would involve complex file parsing and transactional saves.
        
        return "Bulk upload initiated. Check logs for status.";
    }
    
    // 5. Generate Full Attendance Report (Placeholder Logic)
    public String generateFullAttendanceReport(String period) {
        System.out.println("Generating full attendance report for period: " + period);
        // This is where you would query Attendance and aggregation logic
        
        // Example logic:
        // switch(period) { case "MONTHLY": ... case "ANNUAL": ... }
        
        return "Report generation successful for " + period + ".";
    }

    // Helper method for DTO conversion
    private UserDTO convertToDto(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setDepartment(user.getDepartment());
        dto.setDesignation(user.getDesignation());
        // Password hash is intentionally omitted for security
        return dto;
    }
}