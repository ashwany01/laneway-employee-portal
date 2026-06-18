package com.laneway.employee_portal.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

public class AssignmentRequest {

    @NotNull(message = "Employee id is required")
    private Long employeeId;

    @NotNull(message = "Project id is required")
    private Long projectId;

    @NotNull(message = "Role on project is required")
    private String roleOnProject;

    @NotNull(message = "Allocation percentage is required")
    @Min(value = 1, message = "Allocation must be at least 1%")
    @Max(value = 100, message = "Allocation cannot exceed 100%")
    private Integer allocationPercentage;

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getRoleOnProject() {
        return roleOnProject;
    }

    public void setRoleOnProject(String roleOnProject) {
        this.roleOnProject = roleOnProject;
    }

    public Integer getAllocationPercentage() {
        return allocationPercentage;
    }

    public void setAllocationPercentage(Integer allocationPercentage) {
        this.allocationPercentage = allocationPercentage;
    }
}