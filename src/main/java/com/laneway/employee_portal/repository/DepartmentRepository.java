package com.laneway.employee_portal.repository;

import com.laneway.employee_portal.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
