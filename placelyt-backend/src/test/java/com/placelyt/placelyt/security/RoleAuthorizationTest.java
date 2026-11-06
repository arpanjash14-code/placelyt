
package com.placelyt.placelyt.security;

import com.placelyt.placelyt.config.JwtAuthenticationFilter;
import com.placelyt.placelyt.controller.CompanyController;
import com.placelyt.placelyt.controller.SkillController;
import com.placelyt.placelyt.controller.UserController;
import com.placelyt.placelyt.service.CompanyService;
import com.placelyt.placelyt.service.SkillService;
import com.placelyt.placelyt.service.UserService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        CompanyController.class,
        SkillController.class,
        UserController.class
})
@AutoConfigureMockMvc(addFilters = false)
@Import({
        RoleAuthorizationTest.MethodSecurityTestConfiguration.class,
        RoleAuthorizationTest.TestAuthorizationExceptionHandler.class
})
class RoleAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CompanyService companyService;

    @MockitoBean
    private SkillService skillService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // Test-only method security configuration

    @TestConfiguration
    @EnableMethodSecurity
    static class MethodSecurityTestConfiguration {
    }

    // Converts method-security denials into HTTP 403 responses

    @RestControllerAdvice
    static class TestAuthorizationExceptionHandler {

        @ExceptionHandler(AuthorizationDeniedException.class)
        public ResponseEntity<Void> handleAccessDenied(
                AuthorizationDeniedException exception) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // STUDENT restrictions

    @Test
    @WithMockUser(roles = "STUDENT")
    void studentCannotCreateCompany() throws Exception {
        mockMvc.perform(post("/api/companies")
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void studentCannotUpdateCompany() throws Exception {
        mockMvc.perform(put("/api/companies/1")
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void studentCannotDeleteCompany() throws Exception {
        mockMvc.perform(delete("/api/companies/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void studentCannotCreateSkill() throws Exception {
        mockMvc.perform(post("/api/skills")
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void studentCannotListAllUsers() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void studentCannotGetUserById() throws Exception {
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isForbidden());
    }

    // RECRUITER restrictions

    @Test
    @WithMockUser(roles = "RECRUITER")
    void recruiterCannotCreateCompany() throws Exception {
        mockMvc.perform(post("/api/companies")
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "RECRUITER")
    void recruiterCannotCreateSkill() throws Exception {
        mockMvc.perform(post("/api/skills")
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "RECRUITER")
    void recruiterCannotListAllUsers() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
    }

    // ADMIN access

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanCreateCompany() throws Exception {
        mockMvc.perform(post("/api/companies")
                .contentType("application/json")
                .content("{}"))
                .andExpect(result -> assertNotEquals(
                        HttpStatus.FORBIDDEN.value(),
                        result.getResponse().getStatus()
                ));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanCreateSkill() throws Exception {
        mockMvc.perform(post("/api/skills")
                .contentType("application/json")
                .content("{}"))
                .andExpect(result -> assertNotEquals(
                        HttpStatus.FORBIDDEN.value(),
                        result.getResponse().getStatus()
                ));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanListAllUsers() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(result -> assertNotEquals(
                        HttpStatus.FORBIDDEN.value(),
                        result.getResponse().getStatus()
                ));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanGetUserById() throws Exception {
        mockMvc.perform(get("/api/users/1"))
                .andExpect(result -> assertNotEquals(
                        HttpStatus.FORBIDDEN.value(),
                        result.getResponse().getStatus()
                ));
    }

    // Normal authenticated student access

    @Test
    @WithMockUser(roles = "STUDENT")
    void studentCanListSkills() throws Exception {
        mockMvc.perform(get("/api/skills"))
                .andExpect(result -> assertNotEquals(
                        HttpStatus.FORBIDDEN.value(),
                        result.getResponse().getStatus()
                ));
    }
}
