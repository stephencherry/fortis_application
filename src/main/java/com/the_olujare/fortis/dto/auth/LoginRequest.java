package com.the_olujare.fortis.dto.auth;

import lombok.*;

/**
 * Carries user credentials during the login process.
 * Uses email as the unique identifier instead of username.
 *
 * email
 *  - Identifies the user attempting to authenticate.
 *
 * password
 *  - Plain-text password provided by the client.
 *  - Compared against the encoded password stored in the database.
 *
 * This DTO exists only for request transport.
 * Password encoding and validation are handled in the authentication service.
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {
    private String email;
    private String password;
}