package com.the_olujare.fortis.service;

import com.the_olujare.fortis.dto.task.TaskRequest;
import com.the_olujare.fortis.dto.task.TaskResponse;
import com.the_olujare.fortis.entity.Task;
import com.the_olujare.fortis.entity.FortisUser;
import com.the_olujare.fortis.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Task domain service scoped strictly to the authenticated user.
 *
 * Core responsibilities:
 * - Create tasks and bind ownership to the logged-in user
 * - Fetch only tasks owned by the current user
 * - Update and delete tasks with enforced ownership checks
 * - Toggle task completion without exposing direct state mutation
 *
 * Security model:
 * - User identity is resolved exclusively from SecurityContext
 * - Every task access passes through getTaskOrThrow()
 * - Cross-user access is explicitly blocked at the service layer
 *
 * Design decisions:
 * - No task operation accepts a userId from the client
 * - Controllers remain thin and delegation-focused
 * - Entity-to-DTO mapping is centralized for consistency
 *
 * Result:
 * - Zero task leakage
 * - Predictable authorization behavior
 * - Clear separation between API and business logic
 */

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    private FortisUser getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return (FortisUser) authentication.getPrincipal();
    }

    public TaskResponse createTask(TaskRequest taskRequest) {
        FortisUser currentUser = getCurrentUser();

        // Convert string priority to enum
        Task.Priority taskPriority = Task.Priority.MEDIUM; // default
        if (taskRequest.getPriority() != null) {
            try {
                taskPriority = Task.Priority.valueOf(taskRequest.getPriority().toUpperCase());
            } catch (IllegalArgumentException illegalArgumentException) {

            }
        }

        Task task = Task.builder()
                .title(taskRequest.getTitle())
                .description(taskRequest.getDescription())
                .dueDate(taskRequest.getDueDate())
                .priority(taskPriority)
                .fortisUser(currentUser)
                .completed(false)
                .build();

        taskRepository.save(task);
        return mapToResponse(task);
    }

//    public TaskResponse createTask(TaskRequest taskRequest) {
//        FortisUser fortisUser = getCurrentUser();
//
//        Task task = Task.builder()
//                .title(taskRequest.getTitle())
//                .description(taskRequest.getDescription())
//                .dueDate(taskRequest.getDueDate())
//                .priority(taskRequest.getPriority() != null ? taskRequest.getPriority() : Task.Priority.MEDIUM)
//                .completed(false)
//                .fortisUser(fortisUser)
//                .build();
//
//        taskRepository.save(task);
//        return mapToResponse(task);
//    }

    public List<TaskResponse> getTasksForCurrentUser() {
        FortisUser fortisUser = getCurrentUser();
        return taskRepository.findAllByFortisUser(fortisUser).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public TaskResponse getTaskById(Long id) {
        Task task = getTaskOrThrow(id);
        return mapToResponse(task);
    }

    public TaskResponse updateTask(Long id, TaskRequest taskRequest) {
        Task task = getTaskOrThrow(id);

        // Convert string priority to enum
        Task.Priority taskPriority = task.getPriority(); // Keep existing if not provided
        if (taskRequest.getPriority() != null) {
            try {
                taskPriority = Task.Priority.valueOf(taskRequest.getPriority().toUpperCase());
            } catch (IllegalArgumentException e) {
                // Invalid priority, keep existing
            }
        }


        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setDueDate(taskRequest.getDueDate());  // ADD THIS
        task.setPriority(taskPriority);

        taskRepository.save(task);
        return mapToResponse(task);
    }

    public void deleteTask(Long id) {
        Task task = getTaskOrThrow(id);
        taskRepository.delete(task);
    }

    public void toggleComplete(Long id) {
        Task task = getTaskOrThrow(id);
        task.setCompleted(!task.isCompleted());
        taskRepository.save(task);
    }

    private Task getTaskOrThrow(Long id) {
        FortisUser fortisUser = getCurrentUser();
        return taskRepository.findById(id)
                .filter(task -> task.getFortisUser().getId().equals(fortisUser.getId()))
                .orElseThrow(() -> new RuntimeException("Task not found or access denied"));
    }

    private TaskResponse mapToResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .completed(task.isCompleted())
                .userId(task.getFortisUser().getId())
                .username(task.getFortisUser().getUsername())
                .dueDate(task.getDueDate())
                .priority(task.getPriority())
                .build();
    }
}
