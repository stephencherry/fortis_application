package com.the_olujare.fortis.repository;

import com.the_olujare.fortis.entity.FortisUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for managing User entities.
 * Provides access to user data for authentication and account management.
 *
 * Extends JpaRepository to inherit standard CRUD operations.
 *
 * findByEmail()
 *  - Retrieves a user using their email address.
 *  - Used during login and token validation.
 *
 * existsByEmail()
 *  - Checks if an email is already registered.
 *  - Prevents duplicate account creation during registration.
 *
 * Returns Optional where absence is a valid outcome.
 */


public interface FortisUserRepository extends JpaRepository<FortisUser, Long> {
    Optional<FortisUser> findByEmail(String email);
    boolean existsByEmail(String email);
}