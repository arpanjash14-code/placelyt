
package com.placelyt.placelyt.repository;

import com.placelyt.placelyt.entity.CompanyEmailDomain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyEmailDomainRepository
        extends JpaRepository<CompanyEmailDomain, Long> {

    Optional<CompanyEmailDomain> findByDomainAndActiveTrue(String domain);

    Optional<CompanyEmailDomain> findByDomainAndCompanyIdAndActiveTrue(
            String domain,
            Long companyId
    );

    boolean existsByDomainAndActiveTrue(String domain);

    List<CompanyEmailDomain> findByCompanyIdOrderByDomainAsc(Long companyId);

    boolean existsByDomain(String domain);
}
