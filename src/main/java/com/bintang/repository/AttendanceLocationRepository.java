package com.bintang.repository;

import com.bintang.entity.AttendanceLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendanceLocationRepository extends JpaRepository<AttendanceLocation, Long> {
}
