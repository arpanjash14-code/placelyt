package com.placelyt.placelyt.service;

import com.placelyt.placelyt.dto.SkillResponse;
import com.placelyt.placelyt.entity.Skill;
import com.placelyt.placelyt.repository.SkillRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SkillService {

    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public SkillResponse createSkill(Skill skill) {

        if (skillRepository.findByName(skill.getName()).isPresent()) {
            throw new RuntimeException("Skill already exists");
        }

        Skill savedSkill = skillRepository.save(skill);

        return toResponse(savedSkill);
    }

    public List<SkillResponse> getAllSkills() {

        return skillRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private SkillResponse toResponse(Skill skill) {

        return new SkillResponse(
                skill.getId(),
                skill.getName(),
                skill.getCategory()
        );
    }
}