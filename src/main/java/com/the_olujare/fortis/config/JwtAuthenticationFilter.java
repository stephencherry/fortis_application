package com.the_olujare.fortis.config;

import com.the_olujare.fortis.util.JwtConstants;
import com.the_olujare.fortis.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Intercepts every incoming HTTP request to handle JWT-based authentication.
 * Extends OncePerRequestFilter to guarantee execution only once per request.
 *
 * Reads the Authorization header and checks for a Bearer token.
 * Extracts the JWT and pulls the user’s email from it.
 *
 * Loads UserDetails using the extracted email.
 * Validates the token against the user’s details.
 *
 * If the token is valid:
 *  - Builds an authenticated UsernamePasswordAuthenticationToken.
 *  - Attaches request-specific details.
 *  - Stores the authentication in SecurityContextHolder.
 *
 * If the header is missing or the token is invalid:
 *  - Skips authentication and continues the filter chain.
 *
 * This filter enables stateless authentication.
 * No session is created. Each request carries its own proof of identity.
 */


@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = httpServletRequest.getHeader(JwtConstants.HEADER_STRING);
        final String token;
        final String email;

        if (authHeader == null || !authHeader.startsWith(JwtConstants.TOKEN_PREFIX)) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        token = authHeader.substring(7);
        email = jwtUtil.extractEmail(token);

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            if (jwtUtil.isTokenValid(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}