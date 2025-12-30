package com.the_olujare.fortis.util;

/**
 * Centralized holder for JWT-related constants.
 *
 * Purpose:
 * - Avoids hardcoded values scattered across the codebase
 * - Keeps token configuration consistent and easy to manage
 *
 * Security notes:
 * - SECRET is hardcoded for development only
 * - MUST be moved to environment variables or a secrets manager in production
 * - Never commit real secrets to version control
 *
 * Token details:
 * - EXPIRATION_TIME defines access token lifespan
 * - TOKEN_PREFIX is used for Authorization header parsing
 * - HEADER_STRING identifies where the token is read from
 *
 * Design intent:
 * - Simple constants-only utility class
 * - No logic, no state, no instantiation
 */

public class JwtConstants {
    public static final String SECRET = "c3VwZXItc2VjdXJlLWtleS10aGF0LWlzLWF0LWxlYXN0LTMyLWNoYXJzLWFuZC1iYXNlNjR1cmwtc2FmZQ==";
    public static final long EXPIRATION_TIME = 864_000_000; // 10 days in milliseconds
    public static final String TOKEN_PREFIX = "Bearer";
    public static final String HEADER_STRING = "Authorization";
}