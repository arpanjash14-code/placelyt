package com.placelyt.placelyt.controller;

import com.placelyt.placelyt.dto.SkillResponse;
import com.placelyt.placelyt.entity.Skill;
import com.placelyt.placelyt.service.SkillService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping
    public ResponseEntity<SkillResponse> createSkill(
            @RequestBody Skill skill) {

        return ResponseEntity.ok(
                skillService.createSkill(skill)
        );
    }

    @GetMapping
    public ResponseEntity<List<SkillResponse>> getAllSkills() {

        return ResponseEntity.ok(
                skillService.getAllSkills()
        );
    }
}