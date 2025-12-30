package com.the_olujare.fortis.repository;

import com.the_olujare.fortis.entity.Task;
import com.the_olujare.fortis.entity.FortisUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for managing Task entities.
 * Provides persistence operations for user-owned tasks.
 *
 * Inherits basic CRUD functionality from JpaRepository.
 *
 * findAllByUser()
 *  - Retrieves all tasks that belong to a specific user.
 *  - Used to enforce task ownership at the query level.
 *
 * This method prevents cross-user data access by design.
 */

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByFortisUser(FortisUser fortisUser);
}