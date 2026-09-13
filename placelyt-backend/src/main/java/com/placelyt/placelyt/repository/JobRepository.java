package com.placelyt.placelyt.repository;

import com.placelyt.placelyt.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByCompanyId(Long companyId);

    @Query("""
            SELECT j
            FROM Job j
            JOIN j.company c
            WHERE LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(c.industry) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    List<Job> searchByKeyword(@Param("keyword") String keyword);
}