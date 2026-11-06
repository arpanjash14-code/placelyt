
package com.placelyt.placelyt.controller;

import com.placelyt.placelyt.dto.PreferenceResponse;
import com.placelyt.placelyt.entity.Preference;
import com.placelyt.placelyt.service.AuthenticatedUserService;
import com.placelyt.placelyt.service.PreferenceService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/preferences")
public class PreferenceController {

    private final PreferenceService preferenceService;
    private final AuthenticatedUserService authenticatedUserService;

    public PreferenceController(
            PreferenceService preferenceService,
            AuthenticatedUserService authenticatedUserService) {

        this.preferenceService = preferenceService;
        this.authenticatedUserService = authenticatedUserService;
    }

    @PostMapping("/{userId}")
    public ResponseEntity<PreferenceResponse> createPreference(
            @PathVariable Long userId,
            @RequestBody Preference preference) {

        authenticatedUserService.requireCurrentUser(userId);

        return ResponseEntity.ok(
                preferenceService.createPreference(
                        userId,
                        preference
                )
        );
    }

    @GetMapping("/{userId}")
    public ResponseEntity<PreferenceResponse> getPreference(
            @PathVariable Long userId) {

        authenticatedUserService.requireCurrentUser(userId);

        return ResponseEntity.ok(
                preferenceService.getPreferenceByUserId(userId)
        );
    }

    @PutMapping("/{userId}")
    public ResponseEntity<PreferenceResponse> updatePreference(
            @PathVariable Long userId,
            @RequestBody Preference preference) {

        authenticatedUserService.requireCurrentUser(userId);

        return ResponseEntity.ok(
                preferenceService.updatePreference(
                        userId,
                        preference
                )
        );
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deletePreference(
            @PathVariable Long userId) {

        authenticatedUserService.requireCurrentUser(userId);

        preferenceService.deletePreference(userId);

        return ResponseEntity.noContent().build();
    }
}
