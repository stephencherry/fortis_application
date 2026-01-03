package com.the_olujare.fortis.repository;

import com.the_olujare.fortis.entity.FortisUser;
import com.the_olujare.fortis.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for managing PasswordResetToken entities.
 * Supports the password recovery and reset workflow.
 *
 * Inherits standard CRUD operations from JpaRepository.
 *
 * findByToken()
 *  - Locates a password reset token by its string value.
 *  - Used to validate reset requests before updating passwords.
 *
 * Returns Optional to safely handle expired or invalid tokens.
 */

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByFortisUser(FortisUser fortisUser);
}