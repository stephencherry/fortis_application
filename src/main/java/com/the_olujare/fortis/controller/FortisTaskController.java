package com.the_olujare.fortis.controller;

import com.the_olujare.fortis.dto.task.TaskRequest;
import com.the_olujare.fortis.dto.task.TaskResponse;
import com.the_olujare.fortis.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Defines the REST API endpoints for Fortis task management.
 * Every route here is secured by JWT authentication, so only logged-in users can access them.
 * The controller forwards all operations to TaskService, keeping request handling separate from business logic.
 *
 * 1. POST /api/tasks
 *   - Creates a new task tied to the authenticated user.
 *   - Returns the task details mapped into a clean response object.
 *
 * 2. GET /api/tasks
 *   - Fetches all tasks owned by the current user.
 *   - Ensures no access to tasks belonging to other users.
 *
 * 3. GET /api/tasks/{id}
 *   - Retrieves a single task by ID.
 *   - Ownership enforcement happens in the service layer.
 *
 * 4. PUT /api/tasks/{id}
 *   - Updates the title and description of an existing task.
 *   - Does not allow users to modify tasks they don’t own.
 *
 * 5. DELETE /api/tasks/{id}
 *   - Deletes the specified task if the current user owns it.
 *   - Returns HTTP 204 with no content.
 *
 * 6. PATCH /api/tasks/{id}/toggle
 *   - Quickly switches a task’s completion state.
 *   - Returns the updated task for convenience.
 *
 * Take Note: ResponseEntity ensures proper HTTP response codes and payload formatting.
 * This controller’s responsibility is routing and response handling—nothing more.
 */

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class FortisTaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody TaskRequest taskRequest) {
        return ResponseEntity.ok(taskService.createTask(taskRequest));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        return ResponseEntity.ok(taskService.getTasksForCurrentUser());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTask(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id, @RequestBody TaskRequest taskRequest) {
        return ResponseEntity.ok(taskService.updateTask(id, taskRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<TaskResponse> toggleComplete(@PathVariable Long id) {
        taskService.toggleComplete(id);
        return ResponseEntity.ok(taskService.getTaskById(id));
    }
}