
package com.placelyt.placelyt.service;

import com.placelyt.placelyt.dto.CompanyEmailDomainRequest;
import com.placelyt.placelyt.dto.CompanyEmailDomainResponse;
import com.placelyt.placelyt.entity.Company;
import com.placelyt.placelyt.entity.CompanyEmailDomain;
import com.placelyt.placelyt.exception.CompanyNotFoundException;
import com.placelyt.placelyt.exception.InvalidCompanyEmailDomainException;
import com.placelyt.placelyt.repository.CompanyEmailDomainRepository;
import com.placelyt.placelyt.repository.CompanyRepository;
import com.placelyt.placelyt.util.EmailDomainUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
public class CompanyEmailDomainService {

    private static final Pattern DOMAIN_PATTERN = Pattern.compile(
            "^(?=.{1,253}$)(?:[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?\\.)+[a-z]{2,63}$"
    );

    private final CompanyEmailDomainRepository domainRepository;
    private final CompanyRepository companyRepository;

    public CompanyEmailDomainService(
            CompanyEmailDomainRepository domainRepository,
            CompanyRepository companyRepository
    ) {
        this.domainRepository = domainRepository;
        this.companyRepository = companyRepository;
    }

    @Transactional
    public CompanyEmailDomainResponse addDomain(
            Long companyId,
            CompanyEmailDomainRequest request
    ) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() ->
                        new CompanyNotFoundException("Company not found"));

        String domain = normalizeDomain(request.getDomain());

        if (domainRepository.existsByDomain(domain)) {
            throw new InvalidCompanyEmailDomainException(
                    "This email domain is already registered"
            );
        }

        CompanyEmailDomain companyEmailDomain = new CompanyEmailDomain();
        companyEmailDomain.setDomain(domain);
        companyEmailDomain.setCompany(company);
        companyEmailDomain.setActive(true);

        CompanyEmailDomain savedDomain =
                domainRepository.save(companyEmailDomain);

        return toResponse(savedDomain);
    }

    @Transactional(readOnly = true)
    public List<CompanyEmailDomainResponse> getCompanyDomains(
            Long companyId
    ) {
        if (!companyRepository.existsById(companyId)) {
            throw new CompanyNotFoundException("Company not found");
        }

        return domainRepository
                .findByCompanyIdOrderByDomainAsc(companyId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public CompanyEmailDomainResponse updateDomainStatus(
            Long domainId,
            boolean active
    ) {
        CompanyEmailDomain domain = domainRepository.findById(domainId)
                .orElseThrow(() ->
                        new InvalidCompanyEmailDomainException(
                                "Email domain not found"
                        ));

        domain.setActive(active);

        CompanyEmailDomain savedDomain = domainRepository.save(domain);

        return toResponse(savedDomain);
    }

    private String normalizeDomain(String rawDomain) {
        if (rawDomain == null || rawDomain.isBlank()) {
            throw new InvalidCompanyEmailDomainException(
                    "Email domain is required"
            );
        }

        String input = rawDomain.trim().toLowerCase(Locale.ROOT);

        if (input.contains("@") || input.contains(" ")
                || input.contains("/") || input.contains("\\")) {
            throw new InvalidCompanyEmailDomainException(
                    "Enter a domain only, for example: example.com"
            );
        }

        final String normalized;

        try {
            normalized = EmailDomainUtils.extractDomain(
                    "admin@" + input
            );
        } catch (IllegalArgumentException exception) {
            throw new InvalidCompanyEmailDomainException(
                    "Invalid email domain"
            );
        }

        if (!DOMAIN_PATTERN.matcher(normalized).matches()) {
            throw new InvalidCompanyEmailDomainException(
                    "Invalid email domain format"
            );
        }

        return normalized;
    }

    private CompanyEmailDomainResponse toResponse(
            CompanyEmailDomain domain
    ) {
        return new CompanyEmailDomainResponse(
                domain.getId(),
                domain.getCompany().getId(),
                domain.getDomain(),
                domain.isActive(),
                domain.getCreatedAt()
        );
    }
}
