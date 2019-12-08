package com.gmail.taskmanager.repositories;

import com.gmail.taskmanager.models.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTaskId(Long taskId, Pageable pageable);
    Long countByTaskId(Long taskId);
}
