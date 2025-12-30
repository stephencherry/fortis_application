package com.the_olujare.fortis.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility class for JWT creation, parsing, and validation.
 *
 * Responsibilities:
 * - Generates signed JWT access tokens for authenticated users
 * - Extracts claims (subject, expiration) from incoming tokens
 * - Validates tokens against user identity and expiry time
 *
 * Token structure:
 * - Subject: user email (used as username)
 * - IssuedAt: token creation time
 * - Expiration: controlled via JwtConstants.EXPIRATION_TIME
 * - Signature: HMAC SHA-256
 *
 * Security considerations:
 * - Uses a symmetric secret key for signing and verification
 * - SECRET must be externalized in production (env / secrets manager)
 * - Token validity checks include both identity match and expiry
 *
 * Design intent:
 * - Stateless authentication support
 * - No persistence, no side effects
 * - Single responsibility: JWT handling only
 */

@Component
public class JwtUtil {
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())  // email
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + JwtConstants.EXPIRATION_TIME))
                .signWith(getSignInKey(), Jwts.SIG.HS256)
                .compact();
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        return (email.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64URL.decode(JwtConstants.SECRET);  // ← Correct for JWT
        return Keys.hmacShaKeyFor(keyBytes);
    }

}