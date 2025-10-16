package com.ams.attendance.dto;

import lombok.Data;

@Data
public class CourseDTO {
    private Long id;
    private String courseCode;
    private String name;
    private Integer credits;
    private Long departmentId; 
}
