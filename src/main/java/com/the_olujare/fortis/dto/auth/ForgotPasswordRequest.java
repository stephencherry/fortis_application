package com.the_olujare.fortis.dto.auth;

import lombok.*;

/**
 * Carries data required to initiate the password recovery process.
 *
 * email
 *  - Identifies the account requesting a password reset.
 *  - Used to send a reset link or token to the user.
 *
 * This DTO contains only what is necessary.
 * Validation and security checks are handled in the service layer.
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForgotPasswordRequest {
    private String email;
}