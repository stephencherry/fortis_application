package com.the_olujare.fortis.dto.auth;

import lombok.*;

/**
 * Carries data required to create a new user account.
 * Used only during the registration process.
 *
 * username
 *  - Public display name chosen by the user.
 *
 * email
 *  - Unique identifier for the account.
 *
 * password
 *  - Received in plain text from the client.
 *  - Hashed and stored securely in the service layer.
 *
 * This DTO contains only sign-up data.
 * Validation and persistence are handled outside this class.
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
}