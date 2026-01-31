package com.the_olujare.fortis.dto.auth;

import lombok.*;

/**
 * Represents the authentication response returned to the client.
 * Sent after successful actions such as registration, login, or token refresh.
 *
 * token
 *  - Short-lived JWT used to authenticate API requests.
 *
 * refreshToken
 *  - Long-lived token used to obtain a new access token.
 *
 * username and email
 *  - Basic user details for client-side state and display.
 *
 * message
 *  - Optional feedback message (e.g. login success, token refreshed).
 *
 * This DTO intentionally excludes sensitive internal fields.
 * It acts as a clean contract between the authentication layer and the client.
 */

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String username;
    private String email;
    private String refreshToken;
    private String message;
}