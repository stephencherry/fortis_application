package com.the_olujare.fortis.controller;

import com.the_olujare.fortis.config.RateLimited;
import com.the_olujare.fortis.dto.auth.*;
import com.the_olujare.fortis.repository.EmailVerificationTokenRepository;
import com.the_olujare.fortis.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Exposes authentication and account-related REST endpoints.
 * Acts as a thin layer between HTTP requests and AuthService.
 *
 * Public endpoints:
 * POST /api/auth/register
 *  - Creates a new user account.
 *  - Returns a JWT and basic user details on success.
 *
 * POST /api/auth/login
 *  - Authenticates user credentials.
 *  - Issues a fresh JWT.
 *  - Rate-limited to reduce brute-force attempts.
 *
 * POST /api/auth/forgot-password
 *  - Initiates the password recovery flow.
 *  - Sends a reset link or token to the user’s email.
 *  - Rate-limited to prevent abuse.
 *
 * POST /api/auth/reset-password
 *  - Validates the reset token.
 *  - Updates the user’s password.
 *
 * GET /api/auth/verify
 *  - Verifies a user’s email address using a token.
 *
 * POST /api/auth/refresh
 *  - Exchanges a valid refresh token for a new access token.
 *  - Rate-limited to prevent token abuse.
 *
 * POST /api/auth/logout
 *  - Invalidates the provided refresh token.
 *
 * Rate limiting is enforced using @RateLimited on sensitive endpoints.
 * ResponseEntity is used to provide clear HTTP responses.
 * All business logic and security checks are handled by AuthService.
 */

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @RateLimited
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @RateLimited
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        authService.forgotPassword(forgotPasswordRequest);
        return ResponseEntity.ok("Password reset link has been sent to your email");
    }

    @GetMapping("/reset-password/validate")
    public ResponseEntity<?> validateResetToken(@RequestParam String token) {
        try {
            boolean valid = authService.isValidResetToken(token);
            return ResponseEntity.ok(Map.of("valid", valid, "message", "Token is valid"));
        } catch (RuntimeException runtimeException) {
            return ResponseEntity.badRequest().body(Map.of("valid", false, "message", runtimeException.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        authService.resetPassword(resetPasswordRequest);
        return ResponseEntity.ok("Password rest successful");
    }
    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        try {
            VerificationResult result = authService.verifyEmail(token);
            return ResponseEntity.ok(result);
        } catch (RuntimeException runtimeException) {
            VerificationResult errorResult = VerificationResult.builder()
                    .message(runtimeException.getMessage())
                    .success(false)
                    .alreadyVerified(false)
                    .build();
            return ResponseEntity.badRequest().body(errorResult);
        }
    }

    @PostMapping("/refresh")
    @RateLimited
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody Map<String, String> refreshRequest) {
        String refreshToken = refreshRequest.get("refreshToken");
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody Map<String, String> logoutRequest) {
        String refreshToken = logoutRequest.get("refreshToken");
        authService.logout(refreshToken);
        return ResponseEntity.ok("Logged out successfully");
    }
}