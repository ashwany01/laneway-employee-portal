package com.laneway.employee_portal.specification;

import com.laneway.employee_portal.entity.Employee;
import com.laneway.employee_portal.entity.ProjectAssignment;
import com.laneway.employee_portal.enums.EmploymentStatus;
import com.laneway.employee_portal.enums.WorkLocation;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

public class EmployeeSpecifications {

    public static Specification<Employee> hasDepartment(Long departmentId) {
        return (root, query, cb) -> {
            if (departmentId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("department").get("id"), departmentId);
        };
    }

    public static Specification<Employee> hasLocation(WorkLocation location) {
        return (root, query, cb) -> {
            if (location == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("workLocation"), location);
        };
    }

    public static Specification<Employee> hasStatus(EmploymentStatus status) {
        return (root, query, cb) -> {
            if (status == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("employmentStatus"), status);
        };
    }

    public static Specification<Employee> hasProject(Long projectId) {
        return (root, query, cb) -> {
            if (projectId == null) {
                return cb.conjunction();
            }
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<ProjectAssignment> paRoot = subquery.from(ProjectAssignment.class);
            subquery.select(paRoot.get("id"));
            subquery.where(
                    cb.equal(paRoot.get("employee"), root),
                    cb.equal(paRoot.get("project").get("id"), projectId)
            );
            return cb.exists(subquery);
        };
    }
}