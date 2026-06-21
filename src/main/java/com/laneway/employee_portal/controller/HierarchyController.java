package com.laneway.employee_portal.controller;

import com.laneway.employee_portal.entity.Employee;
import com.laneway.employee_portal.repository.EmployeeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/employees")
public class HierarchyController {

    private final EmployeeRepository employeeRepository;

    public HierarchyController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @GetMapping("/{id}/hierarchy")
    public ResponseEntity<?> getHierarchy(@PathVariable Long id) {
        Employee employee = employeeRepository.findById(id).orElse(null);
        if (employee == null) {
            return ResponseEntity.notFound().build();
        }

        List<Employee> managementChain = new ArrayList<>();
        Employee current = employee.getManager();
        while (current != null) {
            managementChain.add(current);
            current = current.getManager();
        }

        List<Employee> directReports = employeeRepository.findByManagerId(employee.getId());

        HierarchyResponse response = new HierarchyResponse(employee, managementChain, directReports);
        return ResponseEntity.ok(response);
    }
}