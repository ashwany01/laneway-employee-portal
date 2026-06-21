package com.laneway.employee_portal.controller;

import com.laneway.employee_portal.entity.Employee;
import com.laneway.employee_portal.enums.AccessRole;
import com.laneway.employee_portal.enums.EmploymentStatus;
import com.laneway.employee_portal.enums.WorkLocation;
import com.laneway.employee_portal.repository.EmployeeRepository;
import com.laneway.employee_portal.specification.EmployeeSpecifications;
import jakarta.validation.Valid;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeController(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public Page<Employee> getAllEmployees(
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) WorkLocation location,
            @RequestParam(required = false) EmploymentStatus status,
            @RequestParam(required = false) Long projectId,
            Pageable pageable) {

        Specification<Employee> spec = Specification
                .where(EmployeeSpecifications.hasDepartment(departmentId))
                .and(EmployeeSpecifications.hasLocation(location))
                .and(EmployeeSpecifications.hasStatus(status))
                .and(EmployeeSpecifications.hasProject(projectId));

        return employeeRepository.findAll(spec, pageable);
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
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
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
                    employee.setPassword(passwordEncoder.encode(updatedEmployee.getPassword()));
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
                    employee.setEmploymentStatus(EmploymentStatus.EXITED);
                    employeeRepository.save(employee);
                    return ResponseEntity.ok("Employee soft-deleted successfully");
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/import")
    public ResponseEntity<CsvImportResult> importEmployeesFromCsv(@RequestParam("file") MultipartFile file) {
        CsvImportResult result = new CsvImportResult();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .build();

            Iterable<CSVRecord> records = csvFormat.parse(reader);

            int rowNumber = 1;
            for (CSVRecord record : records) {
                rowNumber++;
                try {
                    String name = record.get("name");
                    String email = record.get("email");
                    String password = record.get("password");
                    String designation = record.get("designation");
                    String accessRoleStr = record.get("accessRole");
                    String workLocationStr = record.get("workLocation");
                    String dateOfJoiningStr = record.get("dateOfJoining");
                    String employmentStatusStr = record.get("employmentStatus");

                    if (name == null || name.isBlank()) {
                        result.addFailure(rowNumber, "Name is required");
                        continue;
                    }
                    if (email == null || email.isBlank()) {
                        result.addFailure(rowNumber, "Email is required");
                        continue;
                    }
                    if (employeeRepository.findByEmail(email).isPresent()) {
                        result.addFailure(rowNumber, "Email already exists: " + email);
                        continue;
                    }

                    Employee employee = new Employee();
                    employee.setName(name);
                    employee.setEmail(email);
                    employee.setPassword(passwordEncoder.encode(password));
                    employee.setDesignation(designation);
                    employee.setAccessRole(AccessRole.valueOf(accessRoleStr.toUpperCase()));
                    employee.setWorkLocation(WorkLocation.valueOf(workLocationStr.toUpperCase()));
                    employee.setDateOfJoining(LocalDate.parse(dateOfJoiningStr));
                    employee.setEmploymentStatus(EmploymentStatus.valueOf(employmentStatusStr.toUpperCase()));
                    employee.setDeleted(false);

                    employeeRepository.save(employee);
                    result.addSuccess(rowNumber, email);

                } catch (Exception rowError) {
                    result.addFailure(rowNumber, "Invalid data - " + rowError.getMessage());
                }
            }

            result.setTotalRows(rowNumber - 1);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }

        return ResponseEntity.ok(result);
    }
}