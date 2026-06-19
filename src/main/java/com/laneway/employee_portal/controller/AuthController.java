package com.laneway.employee_portal.controller;

import com.laneway.employee_portal.entity.Employee;
import com.laneway.employee_portal.repository.EmployeeRepository;
import com.laneway.employee_portal.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        Employee employee = employeeRepository.findByEmail(loginRequest.getEmail()).orElse(null);

        if (employee == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }

        if (employee.isDeleted()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("This account is no longer active");
        }

        boolean passwordMatches = passwordEncoder.matches(loginRequest.getPassword(), employee.getPassword());
        if (!passwordMatches) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }

        String token = jwtUtil.generateToken(employee.getEmail());

        return ResponseEntity.ok(new LoginResponse(token, employee.getName(), employee.getAccessRole().toString()));
    }
}