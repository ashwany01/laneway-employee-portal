package com.laneway.employee_portal.controller;

import com.laneway.employee_portal.entity.Employee;
import com.laneway.employee_portal.entity.Project;
import com.laneway.employee_portal.entity.ProjectAssignment;
import com.laneway.employee_portal.repository.EmployeeRepository;
import com.laneway.employee_portal.repository.ProjectAssignmentRepository;
import com.laneway.employee_portal.repository.ProjectRepository;
import com.laneway.employee_portal.service.ProjectAssignmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/project-assignments")
public class ProjectAssignmentController {

    private final ProjectAssignmentRepository projectAssignmentRepository;
    private final EmployeeRepository employeeRepository;
    private final ProjectRepository projectRepository;
    private final ProjectAssignmentService projectAssignmentService;

    public ProjectAssignmentController(
            ProjectAssignmentRepository projectAssignmentRepository,
            EmployeeRepository employeeRepository,
            ProjectRepository projectRepository,
            ProjectAssignmentService projectAssignmentService) {
        this.projectAssignmentRepository = projectAssignmentRepository;
        this.employeeRepository = employeeRepository;
        this.projectRepository = projectRepository;
        this.projectAssignmentService = projectAssignmentService;
    }

    @GetMapping
    public List<ProjectAssignment> getAllAssignments() {
        return projectAssignmentRepository.findAll();
    }

    @GetMapping("/employee/{employeeId}")
    public List<ProjectAssignment> getAssignmentsForEmployee(@PathVariable Long employeeId) {
        return projectAssignmentRepository.findByEmployeeId(employeeId);
    }

    @GetMapping("/project/{projectId}")
    public List<ProjectAssignment> getAssignmentsForProject(@PathVariable Long projectId) {
        return projectAssignmentRepository.findByProjectId(projectId);
    }

    @PostMapping
    public ResponseEntity<?> assignEmployeeToProject(@Valid @RequestBody AssignmentRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId()).orElse(null);
        if (employee == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Employee not found with id: " + request.getEmployeeId());
        }

        if (employee.isDeleted()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Cannot assign " + employee.getName() + " to a project: this employee has exited and is no longer active.");
        }

        Project project = projectRepository.findById(request.getProjectId()).orElse(null);
        if (project == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Project not found with id: " + request.getProjectId());
        }

        int existingOverlappingAllocation =
                projectAssignmentService.calculateOverlappingAllocation(employee.getId(), project);

        int requestedAllocation = request.getAllocationPercentage();
        int totalAfterThisAssignment = existingOverlappingAllocation + requestedAllocation;

        if (totalAfterThisAssignment > 100) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    "Cannot assign " + employee.getName() + " at " + requestedAllocation +
                    "% to this project. They are already allocated " + existingOverlappingAllocation +
                    "% across other date-overlapping projects, which would bring their total to " +
                    totalAfterThisAssignment + "%, exceeding the 100% limit."
            );
        }

        ProjectAssignment assignment = new ProjectAssignment();
        assignment.setEmployee(employee);
        assignment.setProject(project);
        assignment.setRoleOnProject(request.getRoleOnProject());
        assignment.setAllocationPercentage(requestedAllocation);

        ProjectAssignment saved = projectAssignmentRepository.save(assignment);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> unassignEmployee(@PathVariable Long id) {
        if (!projectAssignmentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        projectAssignmentRepository.deleteById(id);
        return ResponseEntity.ok("Employee unassigned from project successfully");
    }
}