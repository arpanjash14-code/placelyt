package com.placelyt.placelyt.repository;

import com.placelyt.placelyt.entity.CareerPathSkill;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface CareerPathSkillRepository
        extends JpaRepository<CareerPathSkill, Long> {

    List<CareerPathSkill> findByCareerPathId(Long careerPathId);

    @EntityGraph(attributePaths = {"careerPath", "skill"})
    List<CareerPathSkill> findByCareerPathIdIn(
            Collection<Long> careerPathIds
    );
}