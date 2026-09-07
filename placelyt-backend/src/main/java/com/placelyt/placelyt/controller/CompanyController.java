package com.placelyt.placelyt.controller;

import com.placelyt.placelyt.dto.CompanyResponse;
import com.placelyt.placelyt.entity.Company;
import com.placelyt.placelyt.service.CompanyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping
    public ResponseEntity<CompanyResponse> createCompany(
            @RequestBody Company company) {

        return ResponseEntity.ok(
                companyService.createCompany(company)
        );
    }

    @GetMapping
    public ResponseEntity<List<CompanyResponse>> getAllCompanies() {

        return ResponseEntity.ok(
                companyService.getAllCompanies()
        );
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<CompanyResponse> getCompany(
            @PathVariable Long companyId) {

        return ResponseEntity.ok(
                companyService.getCompanyById(companyId)
        );
    }

    @PutMapping("/{companyId}")
    public ResponseEntity<CompanyResponse> updateCompany(
            @PathVariable Long companyId,
            @RequestBody Company company) {

        return ResponseEntity.ok(
                companyService.updateCompany(
                        companyId,
                        company
                )
        );
    }

    @DeleteMapping("/{companyId}")
    public ResponseEntity<Void> deleteCompany(
            @PathVariable Long companyId) {

        companyService.deleteCompany(companyId);

        return ResponseEntity.noContent().build();
    }
}