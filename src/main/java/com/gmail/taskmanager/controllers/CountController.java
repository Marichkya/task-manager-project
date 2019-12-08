package com.gmail.taskmanager.controllers;

import com.gmail.taskmanager.dto.PageCountDTO;
import com.gmail.taskmanager.services.CountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("count")
public class CountController {
    private static final int PAGE_TASK_SIZE = 5;
    private static final int PAGE_COMMENT_SIZE = 10;

    @Autowired
    private CountService countService;

    @GetMapping("tasks")
    public PageCountDTO countTasks(Principal principal) {
        return PageCountDTO.of(countService.countTasks(principal.getName()), PAGE_TASK_SIZE);
    }

    @GetMapping("comments")
    public PageCountDTO countComments(@RequestParam Long taskId) {
        return PageCountDTO.of(countService.countComments(taskId), PAGE_COMMENT_SIZE);
    }

    @GetMapping("tasks-friends")
    public PageCountDTO countFriendlyTasks(Principal principal) {
        return PageCountDTO.of(countService.countTasksFriends(principal.getName()), PAGE_TASK_SIZE);
    }

    @GetMapping("tasklists")
    public PageCountDTO countTaskslists(Principal principal) {
        return PageCountDTO.of(countService.countTasklists(principal.getName()), PAGE_TASK_SIZE);
    }

    @GetMapping("tasks-for-tasklist")
    public PageCountDTO countTasksForList(@RequestParam Long id) {
        return PageCountDTO.of(countService.countTasksForTasklist(id), PAGE_TASK_SIZE);
    }

    @GetMapping("tasks-high-priority")
    public PageCountDTO countUserTasksHighPriority(Principal principal) {
        return PageCountDTO.of(countService.countUserTasksHighPriority(principal.getName()), PAGE_TASK_SIZE);
    }
}
