
package com.placelyt.placelyt.dto;

import java.time.LocalDateTime;

public class CompanyEmailDomainResponse {

    private Long id;
    private Long companyId;
    private String domain;
    private boolean active;
    private LocalDateTime createdAt;

    public CompanyEmailDomainResponse() {
    }

    public CompanyEmailDomainResponse(
            Long id,
            Long companyId,
            String domain,
            boolean active,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.companyId = companyId;
        this.domain = domain;
        this.active = active;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
