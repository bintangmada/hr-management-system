package com.bintang.repository;

import com.bintang.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByEmployeeId(Long employeeId);
    
    // Find today's attendance for an employee
    Optional<Attendance> findFirstByEmployeeIdAndCheckInTimeBetweenOrderByCheckInTimeDesc(
        Long employeeId, LocalDateTime start, LocalDateTime end);
}
