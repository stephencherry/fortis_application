package com.the_olujare.fortis.service;

import com.the_olujare.fortis.dto.auth.*;
import com.the_olujare.fortis.entity.EmailVerificationToken;
import com.the_olujare.fortis.entity.PasswordResetToken;
import com.the_olujare.fortis.entity.RefreshToken;
import com.the_olujare.fortis.entity.FortisUser;
import com.the_olujare.fortis.exception.ResourceNotFoundException;
import com.the_olujare.fortis.repository.EmailVerificationTokenRepository;
import com.the_olujare.fortis.repository.FortisUserRepository;
import com.the_olujare.fortis.repository.PasswordResetTokenRepository;
import com.the_olujare.fortis.repository.RefreshTokenRepository;
import com.the_olujare.fortis.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Central authentication and account security service.
 *
 * Handles the full auth lifecycle:
 * - User registration with email verification
 * - Secure login using Spring Security authentication
 * - JWT access token generation
 * - Refresh token creation, rotation, and revocation
 * - Password reset via time-bound tokens
 * - Account activation through email verification
 * - Explicit logout by revoking refresh tokens
 *
 * Security guarantees:
 * - Passwords are always hashed before persistence
 * - Tokens are time-limited and single-use where applicable
 * - Refresh tokens support rotation to reduce replay risk
 * - Disabled accounts cannot authenticate until verified
 *
 * Design notes:
 * - Business logic is isolated from controllers
 * - Persistence concerns are delegated to repositories
 * - JWT logic is encapsulated in JwtUtil
 * - Email sending is mocked for development and test visibility
 *
 * This service enforces correctness first.
 * Convenience comes second.
 */

@Service
@RequiredArgsConstructor
public class AuthService {

    private final FortisUserRepository fortisUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    //private final JavaMailSender javaMailSender;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthResponse register(RegisterRequest registerRequest) {
        if (fortisUserRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        FortisUser fortisUser = FortisUser.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role("USER")
                .enabled(false)
                .build();

        fortisUserRepository.save(fortisUser);

        //Generate verification token

        String token = UUID.randomUUID().toString();
        EmailVerificationToken emailVerificationToken = EmailVerificationToken.builder()
                .token(token)
                .fortisUser(fortisUser)
                .expiryDate(LocalDateTime.now().plusHours(24))
                .build();

        emailVerificationTokenRepository.save(emailVerificationToken);

        //Printing verification link for development.
        String verificationLink = "http://localhost:8080/api/auth/verify?token=" + token;
        System.out.println("====== Printing Email Verification Link Here ===");
        System.out.println(verificationLink);

        return AuthResponse.builder()
                .token("PENDING_VERIFICATION")
                .refreshToken(null)
                .username(fortisUser.getUsername())
                .email(fortisUser.getEmail())
                .message("Registration successful. Please verify your email (check console for link).")
                .build();
    }

    public AuthResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        FortisUser fortisUser = fortisUserRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        refreshTokenRepository.findAllByFortisUser(fortisUser)
                .forEach(refreshToken -> {
                    refreshToken.setRevoked(true);
                    refreshTokenRepository.save(refreshToken);
                });

        String accessToken = jwtUtil.generateToken(fortisUser);
        RefreshToken newRefreshToken = createRefreshToken(fortisUser);

        return AuthResponse.builder()
                .token(accessToken)
                .refreshToken(newRefreshToken.getToken())
                .username(fortisUser.getUsername())
                .email(fortisUser.getEmail())
                .build();
    }

    public void forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        FortisUser fortisUser = fortisUserRepository.findByEmail(forgotPasswordRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + forgotPasswordRequest.getEmail()));

        // Generate secure token
        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .fortisUser(fortisUser)
                .expiryDate(LocalDateTime.now().plusHours(1)) // 1 hour expiry
                .used(false)
                .build();

        passwordResetTokenRepository.save(resetToken);

        // Simulate sending email (in real app, send actual email)
        String resetLink = "http://localhost:8080/api/auth/reset-password?token=" + token;
        System.out.println("=== PASSWORD RESET LINK (for testing) ===");
        System.out.println(resetLink);
        System.out.println("=========================================");

        // In production: send via mailSender
        // sendResetEmail(user.getEmail(), resetLink);
    }

    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(resetPasswordRequest.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid or expired reset token"));

        if (resetToken.isUsed() || resetToken.isExpired()) {
            throw new RuntimeException("Token is invalid or has expired");
        }

        FortisUser fortisUser = resetToken.getFortisUser();
        fortisUser.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
        fortisUserRepository.save(fortisUser);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }

    public String verifyEmail(String token) {
        EmailVerificationToken emailVerificationToken = emailVerificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));

        if (emailVerificationToken.isUsed() || emailVerificationToken.isExpired()) {
            throw new RuntimeException("Token is invalid or expired");
        }

        FortisUser fortisUser = emailVerificationToken.getFortisUser();
        fortisUser.setEnabled(true);
        fortisUserRepository.save(fortisUser);

        emailVerificationToken.setUsed(true);
        emailVerificationTokenRepository.save(emailVerificationToken);

        return "Email has been successfully verified! You can now log in.";
    }

    private RefreshToken createRefreshToken(FortisUser fortisUser) {
        RefreshToken refreshToken = RefreshToken.builder()
                .fortisUser(fortisUser)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusSeconds(7 * 24 * 60 * 60)) // 7 days
                .revoked(false)
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    public AuthResponse refreshToken(String refreshTokenStr) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (refreshToken.isRevoked() || refreshToken.getExpiryDate().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token expired or revoked");
        }

        FortisUser fortisUser = refreshToken.getFortisUser();

        // Optional: rotate refresh token (more secure)
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
        RefreshToken newRefreshToken = createRefreshToken(fortisUser);

        String newAccessToken = jwtUtil.generateToken(fortisUser);

        return AuthResponse.builder()
                .token(newAccessToken)
                .refreshToken(newRefreshToken.getToken())
                .username(fortisUser.getUsername())
                .email(fortisUser.getEmail())
                .build();
    }

    public void logout(String refreshTokenStr) {
        refreshTokenRepository.findByToken(refreshTokenStr)
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                });
    }
}