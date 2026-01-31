package com.the_olujare.fortis.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VerificationResult {
    private String message;
    private String email;
    private boolean success;
    private boolean alreadyVerified;
}