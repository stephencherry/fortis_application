package com.the_olujare.fortis.repository;

import com.the_olujare.fortis.entity.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository for managing EmailVerificationToken entities.
 * Provides database access for email verification flows.
 *
 * Extends JpaRepository to inherit basic CRUD operations.
 *
 * findByToken()
 *  - Retrieves a verification token using its string value.
 *  - Used when validating email confirmation requests.
 *
 * Returns Optional to safely handle missing or invalid tokens.
 */

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    Optional<EmailVerificationToken> findByToken(String token);
}