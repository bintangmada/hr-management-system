package com.bintang.repository;

import com.bintang.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByEmployeeIdOrderByCreatedAtDesc(Long employeeId);
    List<LeaveRequest> findByStatusOrderByCreatedAtDesc(String status);
    List<LeaveRequest> findByEmployeeIdInOrderByCreatedAtDesc(List<Long> employeeIds);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(l) FROM LeaveRequest l WHERE l.status = 'APPROVED' AND :date BETWEEN l.startDate AND l.endDate")
    long countApprovedLeaveOnDate(@org.springframework.data.repository.query.Param("date") java.time.LocalDate date);
}
