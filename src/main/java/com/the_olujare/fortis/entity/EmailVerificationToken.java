package com.the_olujare.fortis.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Represents an email verification token linked to a user account.
 * Used during account activation and email confirmation flows.
 *
 * token
 *  - Unique verification value sent to the user via email.
 *
 * user
 *  - The account associated with this verification token.
 *  - One-to-one relationship ensures a single active token per user.
 *
 * expiryDate
 *  - Defines how long the token remains valid.
 *
 * used
 *  - Prevents token reuse after successful verification.
 *
 * isExpired()
 *  - Convenience method to check whether the token is no longer valid.
 *
 * This entity supports secure email verification.
 * Tokens are time-bound and single-use by design.
 *
 * Email verification token entity with performance optimizations.
 * Indexes:
 * - token: For fast lookups during verification (most common query)
 * - user_id: For finding tokens by user
 * These indexes dramatically improve query performance from seconds to milliseconds.
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "email_verification_token", indexes = {
        @Index(name = "idx_token", columnList = "token"),
        @Index(name = "idx_user_id", columnList = "user_id")
})
public class EmailVerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private FortisUser fortisUser;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Builder.Default
    @Column(nullable = false)
    private boolean used = false;

    public boolean isExpired() {
        return expiryDate.isBefore(LocalDateTime.now());
    }
}