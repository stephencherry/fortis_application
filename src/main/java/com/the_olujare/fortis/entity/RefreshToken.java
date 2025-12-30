package com.the_olujare.fortis.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Represents a refresh token used to issue new access tokens.
 * Supports long-lived authentication without re-entering credentials.
 *
 * token
 *  - Unique refresh token value issued after authentication.
 *
 * user
 *  - The account this refresh token belongs to.
 *  - One-to-one mapping allows controlled token management per user.
 *
 * expiryDate
 *  - Defines how long the refresh token remains valid.
 *
 * revoked
 *  - Indicates whether the token has been explicitly invalidated.
 *  - Used during logout or security events.
 *
 * This entity enables secure token rotation.
 * Access tokens remain short-lived, while refresh tokens are tightly controlled.
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private FortisUser fortisUser;

    private Instant expiryDate;

    @Builder.Default
    private boolean revoked = false;
}