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

    /**
     * Checks if two date ranges overlap.
     * A null endDate is treated as "ongoing indefinitely" (open-ended).
     */
    public boolean datesOverlap(LocalDate startA, LocalDate endA, LocalDate startB, LocalDate endB) {
        LocalDate effectiveEndA = (endA == null) ? LocalDate.MAX : endA;
        LocalDate effectiveEndB = (endB == null) ? LocalDate.MAX : endB;

        boolean startsBeforeOtherEnds = !startA.isAfter(effectiveEndB);
        boolean endsAfterOtherStarts = !effectiveEndA.isBefore(startB);

        return startsBeforeOtherEnds && endsAfterOtherStarts;
    }

    /**
     * Calculates an employee's total allocation percentage across all
     * existing assignments whose project dates overlap with the given new project.
     */
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
}