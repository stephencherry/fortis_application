package com.the_olujare.fortis.dto.task;

import lombok.*;

/**
 * Represents task data returned to the client.
 * Used for task creation, retrieval, and updates.
 *
 * id
 *  - Unique identifier of the task.
 *
 * userId
 *  - Identifies the owner of the task.
 *
 * username
 *  - Convenience field for display purposes.
 *
 * title
 *  - Short task summary.
 *
 * description
 *  - Detailed task information.
 *
 * completed
 *  - Indicates whether the task has been marked as done.
 *
 * This DTO exposes only client-safe fields.
 * It acts as a clean boundary between persistence and presentation.
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskResponse {
    private Long id;
    private Long userId;
    private String title;
    private String username;
    private String description;
    private boolean completed;
}