package com.the_olujare.fortis.service;

import com.the_olujare.fortis.repository.FortisUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Spring Security adapter responsible for loading application users during authentication.
 *
 * Key responsibilities:
 * - Bridges Spring Security with the User domain model
 * - Resolves users by email (used as the authentication principal)
 * - Throws UsernameNotFoundException to signal failed authentication attempts
 *
 * Security notes:
 * - Used internally by AuthenticationManager
 * - Never exposed directly to controllers
 * - Ensures authentication fails fast for unknown users
 *
 * Design intent:
 * - Keeps authentication logic out of controllers and services
 * - Treats email as the unique login identifier
 * - Relies on User implementing UserDetails
 *
 * Result:
 * - Predictable authentication flow
 * - Clean separation between security infrastructure and business logic
 */

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final FortisUserRepository fortisUserRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return fortisUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
}