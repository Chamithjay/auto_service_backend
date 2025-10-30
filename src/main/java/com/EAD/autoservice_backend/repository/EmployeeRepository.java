package com.EAD.autoservice_backend.repository;

import com.EAD.autoservice_backend.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // Find all employees
    List<Employee> findAll();

    // Find employee by NIC
    Optional<Employee> findByNic(String nic);
}


