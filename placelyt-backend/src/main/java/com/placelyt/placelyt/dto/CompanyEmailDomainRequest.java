
package com.placelyt.placelyt.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CompanyEmailDomainRequest {

    @NotBlank(message = "Email domain is required")
    @Size(max = 255, message = "Email domain cannot exceed 255 characters")
    private String domain;

    public CompanyEmailDomainRequest() {
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
