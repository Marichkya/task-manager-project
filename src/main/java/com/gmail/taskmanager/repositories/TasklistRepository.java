package com.gmail.taskmanager.repositories;

import com.gmail.taskmanager.models.Task;
import com.gmail.taskmanager.models.Tasklist;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TasklistRepository extends JpaRepository<Tasklist, Long> {

    List<Tasklist> findByAuthorUsername(String username);

    List<Tasklist> findByAuthorUsername(String username, Pageable pageable);

    Long countByAuthorUsername(String username);

    @Query("SELECT t.tasks FROM Tasklist t WHERE t.id = :id")
    List<Task> findTaskForTasksList(Long id, Pageable pageable);
}
