package com.placelyt.placelyt.repository;

import com.placelyt.placelyt.entity.Education;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EducationRepository extends JpaRepository<Education, Long> {

    List<Education> findByUserId(Long userId);
}