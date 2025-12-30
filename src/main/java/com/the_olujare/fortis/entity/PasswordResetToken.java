package com.the_olujare.fortis.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Represents a password reset token associated with a user account.
 * Used during the password recovery process.
 *
 * token
 *  - Unique value issued to authorize a password reset.
 *  - Typically delivered to the user via email.
 *
 * user
 *  - The account requesting the password reset.
 *  - One-to-one mapping ensures controlled token ownership.
 *
 * expiryDate
 *  - Limits how long the token remains valid.
 *
 * used
 *  - Marks whether the token has already been consumed.
 *  - Prevents replay attacks.
 *
 * isExpired()
 *  - Utility method to check token validity based on time.
 *
 * This entity enforces secure, time-bound, single-use password resets.
 */


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private FortisUser fortisUser;

    private LocalDateTime expiryDate;

    @Builder.Default
    private boolean used = false;

    public boolean isExpired() {
        return expiryDate.isBefore(LocalDateTime.now());
    }
}