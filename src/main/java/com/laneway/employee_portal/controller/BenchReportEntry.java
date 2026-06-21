package com.laneway.employee_portal.controller;

import com.laneway.employee_portal.entity.Employee;

public class BenchReportEntry {

    private Employee employee;
    private int currentAllocation;

    public BenchReportEntry(Employee employee, int currentAllocation) {
        this.employee = employee;
        this.currentAllocation = currentAllocation;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public int getCurrentAllocation() {
        return currentAllocation;
    }

    public void setCurrentAllocation(int currentAllocation) {
        this.currentAllocation = currentAllocation;
    }
}