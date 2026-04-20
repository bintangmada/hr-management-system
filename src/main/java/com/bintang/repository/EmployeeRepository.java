package com.bintang.repository;

import com.bintang.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByNik(String nik);
    Optional<Employee> findByEmail(String email);
    Optional<Employee> findByNikAndEmail(String nik, String email);
}
