package com.the_olujare.fortis.controller;

import com.the_olujare.fortis.entity.FortisUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Exposes user-related endpoints for authenticated users.
 * Focuses on retrieving information about the currently logged-in user.
 *
 * The current user is extracted from Spring Security’s SecurityContext.
 * Authentication is assumed to be handled earlier by the JWT filter.
 *
 * GET /api/user/profile
 *  - Returns basic profile details of the authenticated user.
 *  - Includes identifiers and account status.
 *  - Avoids exposing sensitive fields such as passwords or tokens.
 *
 * The response is intentionally lightweight and read-only.
 * This endpoint exists to support profile views and session validation on the client.
 */

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private FortisUser getCurrentUser() {
        return (FortisUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile() {
        FortisUser fortisUser = getCurrentUser();

        Map<String, Object> profile = new HashMap<>();
        profile.put("id", fortisUser.getId());
        profile.put("username", fortisUser.getUsername());
        profile.put("email", fortisUser.getEmail());
        profile.put("enabled", fortisUser.isEnabled());
        profile.put("role", fortisUser.getRole());

        return ResponseEntity.ok(profile);
    }
}