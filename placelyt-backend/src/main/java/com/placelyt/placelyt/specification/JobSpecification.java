package com.placelyt.placelyt.specification;

import com.placelyt.placelyt.dto.JobFilterRequest;
import com.placelyt.placelyt.entity.Job;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class JobSpecification {

    private JobSpecification() {
        // Utility class
    }

    public static Specification<Job> withFilters(
            JobFilterRequest filter) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (filter.getEmploymentType() != null) {

                predicates.add(
                        criteriaBuilder.equal(
                                root.get("employmentType"),
                                filter.getEmploymentType()
                        )
                );
            }

            if (filter.getWorkMode() != null) {

                predicates.add(
                        criteriaBuilder.equal(
                                root.get("workMode"),
                                filter.getWorkMode()
                        )
                );
            }

            if (filter.getLocation() != null
                    && !filter.getLocation().isBlank()) {

                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("location")
                                ),
                                "%" + filter.getLocation()
                                        .trim()
                                        .toLowerCase() + "%"
                        )
                );
            }

            if (filter.getMinimumCgpa() != null) {

                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.get("minimumCgpa"),
                                filter.getMinimumCgpa()
                        )
                );
            }

            if (filter.getGraduationYear() != null) {

                predicates.add(
                        criteriaBuilder.equal(
                                root.get("eligibleGraduationYear"),
                                filter.getGraduationYear()
                        )
                );
            }

            if (filter.getStatus() != null) {

                predicates.add(
                        criteriaBuilder.equal(
                                root.get("status"),
                                filter.getStatus()
                        )
                );
            }

            return criteriaBuilder.and(
                    predicates.toArray(new Predicate[0])
            );
        };
    }
}