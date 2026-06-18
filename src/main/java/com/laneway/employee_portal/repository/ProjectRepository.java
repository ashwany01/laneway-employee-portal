package com.laneway.employee_portal.repository;

import com.laneway.employee_portal.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}