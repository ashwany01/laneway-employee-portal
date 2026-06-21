package com.laneway.employee_portal.controller;

import com.laneway.employee_portal.entity.Employee;
import java.util.List;

public class HierarchyResponse {

    private Employee employee;
    private List<Employee> managementChain;
    private List<Employee> directReports;

    public HierarchyResponse(Employee employee, List<Employee> managementChain, List<Employee> directReports) {
        this.employee = employee;
        this.managementChain = managementChain;
        this.directReports = directReports;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public List<Employee> getManagementChain() {
        return managementChain;
    }

    public void setManagementChain(List<Employee> managementChain) {
        this.managementChain = managementChain;
    }

    public List<Employee> getDirectReports() {
        return directReports;
    }

    public void setDirectReports(List<Employee> directReports) {
        this.directReports = directReports;
    }
}