package com.laneway.employee_portal.controller;

import com.laneway.employee_portal.entity.Employee;
import com.laneway.employee_portal.repository.EmployeeRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;

    public EmployeeController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        return employeeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createEmployee(@Valid @RequestBody Employee employee) {
        if (employeeRepository.findByEmail(employee.getEmail()).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("An employee with this email already exists: " + employee.getEmail());
        }
        employee.setDeleted(false);
        Employee saved = employeeRepository.save(employee);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable Long id, @Valid @RequestBody Employee updatedEmployee) {
        return employeeRepository.findById(id)
                .map(employee -> {
                    employee.setName(updatedEmployee.getName());
                    employee.setEmail(updatedEmployee.getEmail());
                    employee.setDesignation(updatedEmployee.getDesignation());
                    employee.setAccessRole(updatedEmployee.getAccessRole());
                    employee.setDepartment(updatedEmployee.getDepartment());
                    employee.setWorkLocation(updatedEmployee.getWorkLocation());
                    employee.setTimezone(updatedEmployee.getTimezone());
                    employee.setManager(updatedEmployee.getManager());
                    employee.setDateOfJoining(updatedEmployee.getDateOfJoining());
                    employee.setEmploymentStatus(updatedEmployee.getEmploymentStatus());
                    Employee saved = employeeRepository.save(employee);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> softDeleteEmployee(@PathVariable Long id) {
        return employeeRepository.findById(id)
                .map(employee -> {
                    employee.setDeleted(true);
                    employee.setEmploymentStatus(com.laneway.employee_portal.enums.EmploymentStatus.EXITED);
                    employeeRepository.save(employee);
                    return ResponseEntity.ok("Employee soft-deleted successfully");
                })
                .orElse(ResponseEntity.notFound().build());
    }
}