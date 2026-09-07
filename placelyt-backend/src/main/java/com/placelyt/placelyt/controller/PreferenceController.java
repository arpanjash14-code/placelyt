package com.placelyt.placelyt.controller;

import com.placelyt.placelyt.dto.PreferenceResponse;
import com.placelyt.placelyt.entity.Preference;
import com.placelyt.placelyt.service.PreferenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/preferences")
public class PreferenceController {

    private final PreferenceService preferenceService;

    public PreferenceController(PreferenceService preferenceService) {
        this.preferenceService = preferenceService;
    }

    @PostMapping("/{userId}")
    public ResponseEntity<PreferenceResponse> createPreference(
            @PathVariable Long userId,
            @RequestBody Preference preference) {

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

        return ResponseEntity.ok(
                preferenceService.getPreferenceByUserId(userId)
        );
    }

    @PutMapping("/{userId}")
    public ResponseEntity<PreferenceResponse> updatePreference(
            @PathVariable Long userId,
            @RequestBody Preference preference) {

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

        preferenceService.deletePreference(userId);

        return ResponseEntity.noContent().build();
    }
}