package com.placelyt.placelyt.repository;

import com.placelyt.placelyt.entity.Job;
import com.placelyt.placelyt.entity.JobStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JobRepository
        extends JpaRepository<Job, Long>,
                JpaSpecificationExecutor<Job> {

    @EntityGraph(attributePaths = {"company"})
    List<Job> findByStatus(JobStatus status);

    @EntityGraph(attributePaths = {"company"})
    List<Job> findByCompanyId(Long companyId);

    @EntityGraph(attributePaths = {"company"})
    Optional<Job> findById(Long id);

    @Query("""
            SELECT j
            FROM Job j
            JOIN j.company c
            WHERE LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    @EntityGraph(attributePaths = {"company"})
    List<Job> searchByKeyword(@Param("keyword") String keyword);
}