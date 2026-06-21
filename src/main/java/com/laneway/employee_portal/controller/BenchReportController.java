package com.laneway.employee_portal.controller;

import com.laneway.employee_portal.entity.Employee;
import com.laneway.employee_portal.repository.EmployeeRepository;
import com.laneway.employee_portal.service.ProjectAssignmentService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/reports")
public class BenchReportController {

    private final EmployeeRepository employeeRepository;
    private final ProjectAssignmentService projectAssignmentService;

    public BenchReportController(EmployeeRepository employeeRepository, ProjectAssignmentService projectAssignmentService) {
        this.employeeRepository = employeeRepository;
        this.projectAssignmentService = projectAssignmentService;
    }

    @GetMapping("/bench")
    public List<BenchReportEntry> getBenchReport(@RequestParam(defaultValue = "50") int threshold) {
        List<Employee> allEmployees = employeeRepository.findAll();
        List<BenchReportEntry> benchList = new ArrayList<>();

        for (Employee employee : allEmployees) {
            if (employee.isDeleted()) {
                continue;
            }
            int currentAllocation = projectAssignmentService.calculateCurrentTotalAllocation(employee.getId());
            if (currentAllocation < threshold) {
                benchList.add(new BenchReportEntry(employee, currentAllocation));
            }
        }

        return benchList;
    }
}