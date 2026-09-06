package com.placelyt.placelyt.repository;

import com.placelyt.placelyt.entity.Experience;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExperienceRepository extends JpaRepository<Experience, Long> {

    List<Experience> findByUserId(Long userId);
}