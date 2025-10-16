package com.ams.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class AuthResponse {
    private String jwtToken;
    private String email;
    private String role;
    
    private Long userId; 
    
    private String userName; 
}
