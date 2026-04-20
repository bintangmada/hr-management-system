package com.bintang.repository;

import com.bintang.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    
    @Query("SELECT a FROM Attendance a WHERE a.employeeId = :empId AND a.checkInTime BETWEEN :start AND :end ORDER BY a.checkInTime DESC")
    List<Attendance> findByEmployeeIdAndDateRange(
            @Param("empId") Long empId, 
            @Param("start") LocalDateTime start, 
            @Param("end") LocalDateTime end);

    @Query("SELECT a FROM Attendance a WHERE a.employeeId = :empId AND a.checkInTime BETWEEN :start AND :end")
    Optional<Attendance> findTodayAttendance(
            @Param("empId") Long empId, 
            @Param("start") LocalDateTime start, 
            @Param("end") LocalDateTime end);
            
    List<Attendance> findByEmployeeIdOrderByCheckInTimeDesc(Long employeeId);
}
