
package com.placelyt.placelyt.controller;

import com.placelyt.placelyt.dto.UserSkillResponse;
import com.placelyt.placelyt.entity.UserSkill;
import com.placelyt.placelyt.service.AuthenticatedUserService;
import com.placelyt.placelyt.service.UserSkillService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-skills")
public class UserSkillController {

    private final UserSkillService userSkillService;
    private final AuthenticatedUserService authenticatedUserService;

    public UserSkillController(
            UserSkillService userSkillService,
            AuthenticatedUserService authenticatedUserService) {

        this.userSkillService = userSkillService;
        this.authenticatedUserService = authenticatedUserService;
    }

    @PostMapping("/{userId}/{skillId}")
    public ResponseEntity<UserSkillResponse> addSkill(
            @PathVariable Long userId,
            @PathVariable Long skillId,
            @RequestBody UserSkill userSkill) {

        authenticatedUserService.requireCurrentUser(userId);

        return ResponseEntity.ok(
                userSkillService.addSkill(
                        userId,
                        skillId,
                        userSkill
                )
        );
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<UserSkillResponse>> getUserSkills(
            @PathVariable Long userId) {

        authenticatedUserService.requireCurrentUser(userId);

        return ResponseEntity.ok(
                userSkillService.getUserSkills(userId)
        );
    }

    @PutMapping("/{userId}/{userSkillId}")
    public ResponseEntity<UserSkillResponse> updateSkill(
            @PathVariable Long userId,
            @PathVariable Long userSkillId,
            @RequestBody UserSkill userSkill) {

        authenticatedUserService.requireCurrentUser(userId);

        return ResponseEntity.ok(
                userSkillService.updateSkill(
                        userId,
                        userSkillId,
                        userSkill
                )
        );
    }

    @DeleteMapping("/{userId}/{userSkillId}")
    public ResponseEntity<Void> removeSkill(
            @PathVariable Long userId,
            @PathVariable Long userSkillId) {

        authenticatedUserService.requireCurrentUser(userId);

        userSkillService.removeSkill(
                userId,
                userSkillId
        );

        return ResponseEntity.noContent().build();
    }
}
