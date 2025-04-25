package com.example.lab8_ex2.repository;

import com.example.lab8_ex2.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}

