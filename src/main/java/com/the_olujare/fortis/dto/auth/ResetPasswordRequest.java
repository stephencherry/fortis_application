package com.the_olujare.fortis.dto.auth;

import lombok.*;

/**
 * Carries data required to complete a password reset.
 *
 * token
 *  - Verifies the legitimacy of the reset request which is the current user.
 *  - Typically issued during the forgot-password flow.
 *
 * newPassword
 *  - The new plain-text password provided by the user.
 *  - Encoded and stored securely in the service layer.
 *
 * This DTO is used only once per reset operation.
 * Validation and token checks are handled outside this class.
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResetPasswordRequest {
    private String token;
    private String newPassword;
}