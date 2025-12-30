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
 */


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailVerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @OneToOne
    @JoinColumn(name = "user_id")
    private FortisUser fortisUser;

    private LocalDateTime expiryDate;

    @Builder.Default
    private boolean used = false;

    public boolean isExpired() {
        return expiryDate.isBefore(LocalDateTime.now());
    }
}