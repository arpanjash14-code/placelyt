
package com.placelyt.placelyt.controller;

import com.placelyt.placelyt.service.*;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class StudentDataAuthorizationTest {

    @Test
    void studentProfileRejectsUnauthorizedUser() {

        StudentProfileService service =
                mock(StudentProfileService.class);

        AuthenticatedUserService auth =
                mock(AuthenticatedUserService.class);

        StudentProfileController controller =
                new StudentProfileController(service, auth);

        doThrow(new AccessDeniedException("Forbidden"))
                .when(auth).requireCurrentUser(99L);

        assertThrows(
                AccessDeniedException.class,
                () -> controller.getProfile(99L)
        );

        verifyNoInteractions(service);
    }

    @Test
    void educationRejectsUnauthorizedUser() {

        EducationService service =
                mock(EducationService.class);

        AuthenticatedUserService auth =
                mock(AuthenticatedUserService.class);

        EducationController controller =
                new EducationController(service, auth);

        doThrow(new AccessDeniedException("Forbidden"))
                .when(auth).requireCurrentUser(99L);

        assertThrows(
                AccessDeniedException.class,
                () -> controller.getEducation(99L)
        );

        verifyNoInteractions(service);
    }

    @Test
    void experienceRejectsUnauthorizedUser() {

        ExperienceService service =
                mock(ExperienceService.class);

        AuthenticatedUserService auth =
                mock(AuthenticatedUserService.class);

        ExperienceController controller =
                new ExperienceController(service, auth);

        doThrow(new AccessDeniedException("Forbidden"))
                .when(auth).requireCurrentUser(99L);

        assertThrows(
                AccessDeniedException.class,
                () -> controller.getExperiences(99L)
        );

        verifyNoInteractions(service);
    }

    @Test
    void preferenceRejectsUnauthorizedUser() {

        PreferenceService service =
                mock(PreferenceService.class);

        AuthenticatedUserService auth =
                mock(AuthenticatedUserService.class);

        PreferenceController controller =
                new PreferenceController(service, auth);

        doThrow(new AccessDeniedException("Forbidden"))
                .when(auth).requireCurrentUser(99L);

        assertThrows(
                AccessDeniedException.class,
                () -> controller.getPreference(99L)
        );

        verifyNoInteractions(service);
    }

    @Test
    void userSkillsRejectsUnauthorizedUser() {

        UserSkillService service =
                mock(UserSkillService.class);

        AuthenticatedUserService auth =
                mock(AuthenticatedUserService.class);

        UserSkillController controller =
                new UserSkillController(service, auth);

        doThrow(new AccessDeniedException("Forbidden"))
                .when(auth).requireCurrentUser(99L);

        assertThrows(
                AccessDeniedException.class,
                () -> controller.getUserSkills(99L)
        );

        verifyNoInteractions(service);
    }
}
