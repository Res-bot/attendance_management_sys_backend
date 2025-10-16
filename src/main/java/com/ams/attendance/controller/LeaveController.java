package com.ams.attendance.controller;


import com.ams.attendance.dto.LeaveRequestDTO;
import com.ams.attendance.enums.LeaveStatus;
import com.ams.attendance.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    
    @PostMapping("/{applicantId}")
    public ResponseEntity<LeaveRequestDTO> applyForLeave(
            @PathVariable Long applicantId,
            @RequestBody LeaveRequestDTO leaveRequestDTO) {
        try {
            LeaveRequestDTO newRequest = leaveService.applyForLeave(applicantId, leaveRequestDTO);
            return new ResponseEntity<>(newRequest, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    
    @GetMapping("/history/{applicantId}")
    public ResponseEntity<List<LeaveRequestDTO>> getLeaveHistory(@PathVariable Long applicantId) {
        List<LeaveRequestDTO> history = leaveService.getLeaveHistoryByApplicant(applicantId);
        return ResponseEntity.ok(history);
    }

    
    @GetMapping("/pending")
    public ResponseEntity<List<LeaveRequestDTO>> getAllPendingRequests() {
        List<LeaveRequestDTO> pendingRequests = leaveService.getAllPendingRequests();
        return ResponseEntity.ok(pendingRequests);
    }

    public static class UpdateStatusRequest {
        private Long approverId;
        private LeaveStatus status;
        private String rejectionReason;

        public Long getApproverId() { return approverId; }
        public void setApproverId(Long approverId) { this.approverId = approverId; }
        public LeaveStatus getStatus() { return status; }
        public void setStatus(LeaveStatus status) { this.status = status; }
        public String getRejectionReason() { return rejectionReason; }
        public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    }

    
    @PutMapping("/{requestId}/status")
    public ResponseEntity<LeaveRequestDTO> updateLeaveStatus(
            @PathVariable Long requestId,
            @RequestBody UpdateStatusRequest requestBody) {
        try {
            if (requestBody.getStatus() == null || requestBody.getApproverId() == null) {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }

            LeaveRequestDTO updatedRequest = leaveService.updateLeaveStatus(
                    requestId,
                    requestBody.getApproverId(),
                    requestBody.getStatus(),
                    requestBody.getRejectionReason());

            return ResponseEntity.ok(updatedRequest);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); 
        }
    }
}
