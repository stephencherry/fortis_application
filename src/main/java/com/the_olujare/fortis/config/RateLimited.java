package com.the_olujare.fortis.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks controller or service methods that should be rate limited.
 * When applied, RateLimitAspect intercepts the method before execution.
 *
 * Designed to be simple and explicit:
 *  - Only annotated methods are rate limited.
 *  - No global side effects on other endpoints.
 *
 * Can be extended later to support:
 *  - Custom request limits.
 *  - Custom time windows.
 *  - Different rate-limit strategies per endpoint.
 */


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimited {
    // You can add parameters later if needed, e.g.:
    // int maxRequests() default 5;
    // long windowMs() default 60_000;
}