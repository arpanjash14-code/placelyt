
package com.placelyt.placelyt.repository;

import com.placelyt.placelyt.entity.Application;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository
        extends JpaRepository<Application, Long> {

    /**
     * Retrieves all applications submitted by a specific student.
     * Fetches related entities needed for ApplicationResponse
     * to avoid unnecessary lazy-loading queries.
     */
    @EntityGraph(attributePaths = {
            "user",
            "job",
            "job.company"
    })
    List<Application> findByUserId(Long userId);

    /**
     * Checks whether a student has already applied to a job.
     */
    Optional<Application> findByUserIdAndJobId(
            Long userId,
            Long jobId
    );

    /**
     * Retrieves applications submitted to jobs belonging
     * to a specific company.
     *
     * Recruiter access must still be authorized in the
     * service layer before this query is used.
     */
    @EntityGraph(attributePaths = {
            "user",
            "job",
            "job.company"
    })
    List<Application> findByJob_Company_Id(Long companyId);
}
