package com.the_olujare.fortis.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a task owned by a user.
 * Used to model user-specific to-do items in the system.
 *
 * title
 *  - Short, human-readable summary of the task.
 *
 * description
 *  - Optional detailed information about the task.
 *
 * completed
 *  - Indicates whether the task has been marked as done.
 *  - Defaults to false on creation.
 *
 * user
 *  - The owner of the task.
 *  - Many tasks can belong to a single user.
 *  - Loaded lazily to avoid unnecessary database queries.
 *
 * This entity enforces task ownership at the data level.
 * Access control is further enforced in the service layer.
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @Builder.Default
    private boolean completed = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private FortisUser fortisUser;
}