package com.laneway.employee_portal.service;

import com.laneway.employee_portal.entity.Project;
import com.laneway.employee_portal.entity.ProjectAssignment;
import com.laneway.employee_portal.repository.ProjectAssignmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProjectAssignmentService {

    private final ProjectAssignmentRepository projectAssignmentRepository;

    public ProjectAssignmentService(ProjectAssignmentRepository projectAssignmentRepository) {
        this.projectAssignmentRepository = projectAssignmentRepository;
    }

    public boolean datesOverlap(LocalDate startA, LocalDate endA, LocalDate startB, LocalDate endB) {
        LocalDate effectiveEndA = (endA == null) ? LocalDate.MAX : endA;
        LocalDate effectiveEndB = (endB == null) ? LocalDate.MAX : endB;

        boolean startsBeforeOtherEnds = !startA.isAfter(effectiveEndB);
        boolean endsAfterOtherStarts = !effectiveEndA.isBefore(startB);

        return startsBeforeOtherEnds && endsAfterOtherStarts;
    }

    public int calculateOverlappingAllocation(Long employeeId, Project newProject) {
        List<ProjectAssignment> existingAssignments = projectAssignmentRepository.findByEmployeeId(employeeId);

        int total = 0;
        for (ProjectAssignment assignment : existingAssignments) {
            Project existingProject = assignment.getProject();
            boolean overlaps = datesOverlap(
                    existingProject.getStartDate(), existingProject.getEndDate(),
                    newProject.getStartDate(), newProject.getEndDate()
            );
            if (overlaps) {
                total += assignment.getAllocationPercentage();
            }
        }
        return total;
    }

    public int calculateCurrentTotalAllocation(Long employeeId) {
        List<ProjectAssignment> assignments = projectAssignmentRepository.findByEmployeeId(employeeId);
        LocalDate today = LocalDate.now();

        int total = 0;
        for (ProjectAssignment assignment : assignments) {
            Project project = assignment.getProject();
            boolean isCurrentlyActive = !project.getStartDate().isAfter(today)
                    && (project.getEndDate() == null || !project.getEndDate().isBefore(today));
            if (isCurrentlyActive) {
                total += assignment.getAllocationPercentage();
            }
        }
        return total;
    }
}