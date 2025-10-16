package com.ams.attendance.service;

import com.ams.attendance.dto.AuthRequest;
import com.ams.attendance.dto.AuthResponse;
import com.ams.attendance.dto.UserDTO;
import com.ams.attendance.entity.User;
import com.ams.attendance.enums.UserRole;
import com.ams.attendance.repository.UserRepository;
import com.ams.attendance.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final AdminService adminService; 

    
    public UserDTO registerUser(UserDTO userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("User with this email already exists.");
        }
        
        UserRole role = userDto.getRole() != null ? userDto.getRole() : determineDefaultRole(userDto.getEmail());
        userDto.setRole(role);

        return adminService.createUser(userDto);
    }
    
    private UserRole determineDefaultRole(String email) {
        if (email.endsWith("@student.com")) return UserRole.STUDENT;
        if (email.endsWith("@teacher.com")) return UserRole.TEACHER;
        return UserRole.EMPLOYEE;
    }


   
    public AuthResponse login(AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );
            
            final User user = (User) authentication.getPrincipal(); 
            
            final String jwt = jwtUtil.generateToken(user);
            
            String role = user.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
            
            return new AuthResponse(
                jwt,
                user.getEmail(),
                role,
                user.getId(), 
                user.getName()
            );

        } catch (Exception e) {
            throw new RuntimeException("Authentication failed: Invalid email or password.", e);
        }
    }
}
