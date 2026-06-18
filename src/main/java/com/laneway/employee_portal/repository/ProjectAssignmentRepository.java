package com.laneway.employee_portal.repository;

import com.laneway.employee_portal.entity.ProjectAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectAssignmentRepository extends JpaRepository<ProjectAssignment, Long> {
    List<ProjectAssignment> findByEmployeeId(Long employeeId);
    List<ProjectAssignment> findByProjectId(Long projectId);
}