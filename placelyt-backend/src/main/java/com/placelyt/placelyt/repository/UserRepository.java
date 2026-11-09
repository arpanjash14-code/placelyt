
package com.placelyt.placelyt.repository;

import com.placelyt.placelyt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    boolean existsByRole(String role);

    Optional<User> findByEmailVerificationTokenHash(
            String emailVerificationTokenHash
    );

    List<User> findByRoleAndApprovalStatusOrderByIdAsc(
            String role,
            com.placelyt.placelyt.entity.RecruiterApprovalStatus approvalStatus
    );

    Optional<User> findByIdAndRole(Long id, String role);

    @Modifying
    @Query("""
            UPDATE User u
            SET u.emailVerified = true,
                u.emailVerifiedAt = :verifiedAt,
                u.emailVerificationTokenHash = null,
                u.emailVerificationTokenExpiresAt = null
            WHERE u.emailVerificationTokenHash = :tokenHash
              AND u.emailVerificationTokenExpiresAt > :now
              AND u.emailVerified = false
              AND u.role = 'RECRUITER'
            """)
    int consumeEmailVerificationToken(
            @Param("tokenHash") String tokenHash,
            @Param("now") LocalDateTime now,
            @Param("verifiedAt") LocalDateTime verifiedAt
    );
}
