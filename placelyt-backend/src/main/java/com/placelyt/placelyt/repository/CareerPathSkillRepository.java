package com.placelyt.placelyt.repository;

import com.placelyt.placelyt.entity.CareerPathSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CareerPathSkillRepository
        extends JpaRepository<CareerPathSkill, Long> {

    List<CareerPathSkill> findByCareerPathId(Long careerPathId);
}