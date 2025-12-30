package com.the_olujare.fortis.repository;

import com.the_olujare.fortis.entity.FortisUser;
import com.the_olujare.fortis.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
/**
 * Repository for managing RefreshToken entities.
 * Used to support token refresh and logout workflows.
 *
 * Inherits standard CRUD operations from JpaRepository.
 *
 * findByToken()
 *  - Retrieves a refresh token using its string value.
 *  - Used when issuing new access tokens or invalidating sessions.
 *
 * Returns Optional to safely handle revoked or expired tokens.
 */


public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    List<RefreshToken> findAllByFortisUser(FortisUser fortisUser);
}