package com.the_olujare.fortis.service;

import com.the_olujare.fortis.config.AppProperties;
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
import com.the_olujare.fortis.util.EmailUtil;
import com.the_olujare.fortis.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
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
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final FortisUserRepository fortisUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final EmailUtil emailUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AppProperties appProperties;


    public AuthResponse register(RegisterRequest registerRequest) {
        if (fortisUserRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        FortisUser fortisUser = FortisUser.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role("USER")
                .enabled(false)
                .build();

        fortisUserRepository.save(fortisUser);

        String token = UUID.randomUUID().toString();
        EmailVerificationToken emailVerificationToken = EmailVerificationToken.builder()
                .token(token)
                .fortisUser(fortisUser)
                .expiryDate(LocalDateTime.now().plusHours(24))
                .build();

        emailVerificationTokenRepository.save(emailVerificationToken);
        emailUtil.sendVerificationEmailAsync(fortisUser.getEmail(), token);

        return AuthResponse.builder()
                .token("PENDING_VERIFICATION")
                .refreshToken(null)
                .username(fortisUser.getUsername())
                .email(fortisUser.getEmail())
                .message("Registration successful. Please verify your email")
                .build();
    }

    public AuthResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        FortisUser fortisUser = fortisUserRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!fortisUser.isEnabled()) {
            throw new DisabledException("User is disabled");
        }

        String accessToken = jwtUtil.generateToken(fortisUser);
        RefreshToken refreshToken = createRefreshToken(fortisUser);

        return AuthResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken.getToken())
                .username(fortisUser.getUsername())
                .email(fortisUser.getEmail())
                .build();
    }

    public void forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        FortisUser fortisUser = fortisUserRepository.findByEmail(forgotPasswordRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User with the email does not exist: " + forgotPasswordRequest.getEmail()));

        passwordResetTokenRepository.deleteByFortisUser(fortisUser);

        // Generate secure token
        String token = UUID.randomUUID().toString();

        PasswordResetToken passwordResetToken = PasswordResetToken.builder()
                .token(token)
                .fortisUser(fortisUser)
                .expiryDate(LocalDateTime.now().plusHours(1))
                .used(false)
                .build();

        passwordResetTokenRepository.save(passwordResetToken);

        emailUtil.sendPasswordResetEmailAsync(fortisUser.getEmail(), token);
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

    public boolean isValidResetToken(String token) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));

        if (passwordResetToken.isUsed() || passwordResetToken.isExpired()) {
            throw new RuntimeException("Reset Token is invalid or has expired! Try again!!");
        }

        return true;
    }
    public VerificationResult verifyEmail(String token) {
        EmailVerificationToken emailVerificationToken = emailVerificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));

        if (emailVerificationToken.isUsed()) {
            return VerificationResult.builder()
                    .message("Email already verified. You can now log in.")
                    .success(true)
                    .alreadyVerified(true)
                    .email(emailVerificationToken.getFortisUser().getEmail())
                    .build();
        }

        if (emailVerificationToken.isExpired()) {
            throw new RuntimeException("Verification link has expired. Request a new one");
        }

        FortisUser fortisUser = emailVerificationToken.getFortisUser();
        fortisUser.setEnabled(true);
        fortisUserRepository.save(fortisUser);

        emailVerificationToken.setUsed(true);
        emailVerificationTokenRepository.save(emailVerificationToken);

        return VerificationResult.builder()
                .message("Email has been successfully verified! You can now log in.")
                .success(true)
                .alreadyVerified(false)
                .email(fortisUser.getEmail())
                .build();
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