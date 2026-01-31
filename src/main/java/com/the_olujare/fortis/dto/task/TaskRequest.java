package com.the_olujare.fortis.dto.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.the_olujare.fortis.entity.Task;
import lombok.*;

import java.time.LocalDate;


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
    private Long userId;
    private String title;
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    private String priority;
}
