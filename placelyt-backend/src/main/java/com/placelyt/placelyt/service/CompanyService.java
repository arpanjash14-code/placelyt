package com.placelyt.placelyt.service;

import com.placelyt.placelyt.dto.CompanyResponse;
import com.placelyt.placelyt.entity.Company;
import com.placelyt.placelyt.repository.CompanyRepository;
import org.springframework.stereotype.Service;
import com.placelyt.placelyt.exception.DuplicateCompanyException;
import com.placelyt.placelyt.exception.CompanyNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public CompanyResponse createCompany(Company company) {

        if (companyRepository.findByName(company.getName()).isPresent()) {
    throw new DuplicateCompanyException("Company already exists");
}

        Company savedCompany =
                companyRepository.save(company);

        return toResponse(savedCompany);
    }

    public List<CompanyResponse> getAllCompanies() {

        return companyRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public CompanyResponse getCompanyById(Long companyId) {

        Company company =
                companyRepository.findById(companyId)
                        .orElseThrow(() ->
        new CompanyNotFoundException("Company not found"));

        return toResponse(company);
    }

    public CompanyResponse updateCompany(
            Long companyId,
            Company updatedCompany) {

        Company existingCompany =
                companyRepository.findById(companyId)
                        .orElseThrow(() ->
                                new RuntimeException("Company not found"));

        existingCompany.setName(updatedCompany.getName());
        existingCompany.setIndustry(updatedCompany.getIndustry());
        existingCompany.setWebsite(updatedCompany.getWebsite());
        existingCompany.setLocation(updatedCompany.getLocation());
        existingCompany.setDescription(updatedCompany.getDescription());
        existingCompany.setCompanySize(updatedCompany.getCompanySize());
        existingCompany.setFoundedYear(updatedCompany.getFoundedYear());

        Company savedCompany =
                companyRepository.save(existingCompany);

        return toResponse(savedCompany);
    }

    public void deleteCompany(Long companyId) {

        Company company =
                companyRepository.findById(companyId)
                        .orElseThrow(() ->
                                new RuntimeException("Company not found"));

        companyRepository.delete(company);
    }

    private CompanyResponse toResponse(Company company) {

        return new CompanyResponse(
                company.getId(),
                company.getName(),
                company.getIndustry(),
                company.getWebsite(),
                company.getLocation(),
                company.getDescription(),
                company.getCompanySize(),
                company.getFoundedYear()
        );
    }
}