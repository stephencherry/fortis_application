package com.the_olujare.fortis.config;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implements simple request rate limiting using Spring AOP.
 * Intercepts methods annotated with @RateLimited before execution.
 *
 * Uses the client IP address as the rate-limit key.
 * Tracks:
 *  - The time of the first request in a window.
 *  - The number of requests made within that window.
 *
 * Configuration:
 *  - MAX_REQUESTS → maximum allowed requests per IP.
 *  - TIME_WINDOW  → time window in milliseconds.
 *
 * Behavior:
 *  - First request from an IP starts a new time window.
 *  - Requests within the window increment a counter.
 *  - If the counter exceeds MAX_REQUESTS, the request is rejected.
 *  - When the window expires, the counter resets automatically.
 *
 * Uses ConcurrentHashMap to remain thread-safe under concurrent requests.
 * Throws a runtime exception when the rate limit is exceeded.
 *
 * This approach is lightweight and suitable for basic protection.
 * It is not intended to replace distributed or production-grade rate limiting solutions.
 */


@Aspect
@Component
public class RateLimitAspect {

    private final Map<String, Integer> requestCounts = new ConcurrentHashMap<>();
    private final Map<String, Long> firstRequestTime = new ConcurrentHashMap<>();
    private static final int MAX_REQUESTS = 5;
    private static final long TIME_WINDOW = 60_000;

    @Before("@annotation(rateLimited)")
    public void rateLimit() {
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String deviceIp = httpServletRequest.getRemoteAddr();

        long currentTime = System.currentTimeMillis();

        if (!firstRequestTime.containsKey(deviceIp)) {
            firstRequestTime.put(deviceIp, currentTime);
            requestCounts.put(deviceIp, 1);
            return;
        }

        long firstTime = firstRequestTime.get(deviceIp);
        if (currentTime - firstTime > TIME_WINDOW) {
            firstRequestTime.put(deviceIp, currentTime);
            requestCounts.put(deviceIp, 1);
            return;
        }

        int count = requestCounts.get(deviceIp);
        if (count >= MAX_REQUESTS) {
            throw new RuntimeException("Too many requests. Kindly try again later.");
        }

        requestCounts.put(deviceIp, count + 1);
    }
}