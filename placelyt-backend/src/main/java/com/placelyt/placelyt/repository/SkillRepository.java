package com.placelyt.placelyt.repository;

import com.placelyt.placelyt.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Long> {

    Optional<Skill> findByName(String name);
}