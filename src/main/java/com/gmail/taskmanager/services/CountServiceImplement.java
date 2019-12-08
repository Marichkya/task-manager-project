package com.gmail.taskmanager.services;

import com.gmail.taskmanager.models.Tasklist;
import com.gmail.taskmanager.repositories.CommentRepository;
import com.gmail.taskmanager.repositories.TaskRepository;
import com.gmail.taskmanager.repositories.TasklistRepository;
import com.gmail.taskmanager.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CountServiceImplement implements CountService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TasklistRepository tasklistRepository;

    @Transactional(readOnly = true)
    @Override
    public Long countTasks(String username) {
        return taskRepository.countByAuthorUsername(username);
    }

    @Transactional(readOnly = true)
    @Override
    public Long countComments(Long taskId) {
        return commentRepository.countByTaskId(taskId);
    }

    @Transactional(readOnly = true)
    @Override
    public Long countTasksFriends(String username) {
        return userRepository.sizeFriendlyTasks(username);
    }

    @Transactional(readOnly = true)
    @Override
    public Long countTasklists(String username) {
        return tasklistRepository.countByAuthorUsername(username);
    }

    @Transactional(readOnly = true)
    @Override
    public Long countTasksForTasklist(Long id) {
        Tasklist tasklist = tasklistRepository.getOne(id);
        return taskRepository.countByTasklists(tasklist);
    }

    @Transactional(readOnly = true)
    @Override
    public Long countUserTasksHighPriority(String username) {
        return taskRepository.countByAuthorUsernameAndPriority(username, "Високий");
    }
}
