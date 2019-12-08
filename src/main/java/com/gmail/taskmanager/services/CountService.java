package com.gmail.taskmanager.services;

public interface CountService {
    Long countTasks(String username);

    Long countComments(Long taskId);

    Long countTasksFriends(String username);

    Long countTasklists(String username);

    Long countTasksForTasklist(Long id);

    Long countUserTasksHighPriority(String username);
}
