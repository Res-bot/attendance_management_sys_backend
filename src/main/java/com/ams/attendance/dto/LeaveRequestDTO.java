package com.ams.attendance.dto;

import com.ams.attendance.entity.LeaveRequest;
import com.ams.attendance.entity.User;
import com.ams.attendance.enums.LeaveStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class LeaveRequestDTO {

    private Long id;

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be today or in the future")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotEmpty(message = "Reason for leave is required")
    private String reason;
    
    
    private Long applicantId; 
    
    private String applicantName; 

    
    private LeaveStatus status; 

    private LocalDateTime requestedOn;
    
    private Long approverId;
    private String approverName;
    
    private String rejectionReason;

    
    public static LeaveRequestDTO fromEntity(LeaveRequest entity) {
        LeaveRequestDTO dto = new LeaveRequestDTO();
        
        dto.setId(entity.getId());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setReason(entity.getReason());
        dto.setStatus(entity.getStatus());
        dto.setRequestedOn(entity.getRequestedOn());
        dto.setRejectionReason(entity.getRejectionReason());

        User applicant = entity.getUser();
        if (applicant != null) {
            dto.setApplicantId(applicant.getId());
            dto.setApplicantName(applicant.getName()); 
        }

        User approver = entity.getApprover();
        if (approver != null) {
            dto.setApproverId(approver.getId());
            dto.setApproverName(approver.getName()); 
        }

        return dto;
    }
}
