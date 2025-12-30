package com.the_olujare.fortis.dto.task;

import lombok.*;

/**
 * Carries task data sent from the client when creating or updating a task.
 *
 * title
 *  - Short summary of the task.
 *
 * description
 *  - Optional detailed information about the task.
 *
 * userId
 *  - Included for flexibility, but not trusted for ownership.
 *  - The actual task owner is resolved from the authenticated user.
 *
 * This DTO is intentionally simple.
 * Authorization and ownership checks are enforced in the service layer.
 */

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class TaskRequest {
    private String title;
    private String description;
    private Long userId;
}