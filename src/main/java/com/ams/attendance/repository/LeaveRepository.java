package com.ams.attendance.repository;

import com.ams.attendance.entity.LeaveRequest;
import com.ams.attendance.enums.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LeaveRepository extends JpaRepository<LeaveRequest, Long> {

    
    List<LeaveRequest> findByStatus(LeaveStatus status);

    List<LeaveRequest> findByApplicant_IdOrderByRequestedOnDesc(Long applicantId);

    long countByApplicant_IdAndStatus(Long applicantId, LeaveStatus status);
}
