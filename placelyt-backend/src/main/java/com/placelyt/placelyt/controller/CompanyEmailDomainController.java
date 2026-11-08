
package com.placelyt.placelyt.controller;

import com.placelyt.placelyt.dto.CompanyEmailDomainRequest;
import com.placelyt.placelyt.dto.CompanyEmailDomainResponse;
import com.placelyt.placelyt.service.CompanyEmailDomainService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class CompanyEmailDomainController {

    private final CompanyEmailDomainService domainService;

    public CompanyEmailDomainController(
            CompanyEmailDomainService domainService
    ) {
        this.domainService = domainService;
    }

    @PostMapping("/companies/{companyId}/domains")
    @ResponseStatus(HttpStatus.CREATED)
    public CompanyEmailDomainResponse addDomain(
            @PathVariable Long companyId,
            @Valid @RequestBody CompanyEmailDomainRequest request
    ) {
        return domainService.addDomain(companyId, request);
    }

    @GetMapping("/companies/{companyId}/domains")
    public List<CompanyEmailDomainResponse> getCompanyDomains(
            @PathVariable Long companyId
    ) {
        return domainService.getCompanyDomains(companyId);
    }

    @PatchMapping("/email-domains/{domainId}")
    public CompanyEmailDomainResponse updateDomainStatus(
            @PathVariable Long domainId,
            @RequestParam boolean active
    ) {
        return domainService.updateDomainStatus(domainId, active);
    }
}
