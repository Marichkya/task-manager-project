package com.gmail.taskmanager.controllers;

import com.gmail.taskmanager.Exceptions.ExistsTaskAtListException;
import com.gmail.taskmanager.dto.FriendDTO;
import com.gmail.taskmanager.dto.TaskDTO;
import com.gmail.taskmanager.dto.results.BadRequestResult;
import com.gmail.taskmanager.dto.results.ResultDTO;
import com.gmail.taskmanager.dto.results.SuccessResult;
import com.gmail.taskmanager.services.GeneralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("task")
public class TaskController {
    private static final int PAGE_TASK_SIZE = 5;

    @Autowired
    private GeneralService generalService;

    @GetMapping("all")
    public List<TaskDTO> getTasks(Principal principal,
                                  @RequestParam(required = false, defaultValue = "0") Integer page) {

        return generalService.getTasks(principal.getName(),
                PageRequest.of(
                        page,
                        PAGE_TASK_SIZE,
                        Sort.Direction.DESC,
                        "id"
                )
        );
    }

    @GetMapping("details")
    public Map<String, Object> getTask(@RequestParam Long id, Principal principal) {
        Map<String, Object> map = new HashMap<>();
        map.put("taskDTO", generalService.getTask(id, principal.getName()));
        map.put("authorTask", generalService.getAuthorTask(id));
        map.put("taskFriends", generalService.getFriendsOnTask(id));
        return map;
    }

    @PostMapping("add")
    public ResponseEntity<ResultDTO> addTask(Principal principal, @RequestBody TaskDTO taskDTO) {
        generalService.addTask(principal.getName(), taskDTO);
        return new ResponseEntity<>(new SuccessResult(), HttpStatus.OK);
    }

    @PostMapping("delete")
    public ResponseEntity<ResultDTO> deleteTask(@RequestParam(name = "toDelete[]", required = false) Long[] tasksId) {
        generalService.deleteTask(Arrays.asList(tasksId));
        return new ResponseEntity<>(new SuccessResult(), HttpStatus.OK);
    }

    @PostMapping("close")
    public ResponseEntity<ResultDTO> closeTask(@RequestParam Long id) {
        generalService.closeTask(id);
        return new ResponseEntity<>(new SuccessResult(), HttpStatus.OK);
    }

    @PostMapping("change")
    public ResponseEntity<ResultDTO> changeTask(@RequestBody TaskDTO taskDTO) {
        generalService.changeTask(taskDTO);
        return new ResponseEntity<>(new SuccessResult(), HttpStatus.OK);
    }

    @GetMapping("tasks-friends")
    public List<TaskDTO> getFriendlyTask(@RequestParam(required = false, defaultValue = "0") Integer page,
                                         Principal principal) {
        return generalService.getTasksFriends(principal.getName(), PageRequest.of(
                page,
                PAGE_TASK_SIZE,
                Sort.Direction.DESC,
                "id"
                )
        );
    }

    @PostMapping("share")
    public ResponseEntity<ResultDTO> shareTask(@RequestParam Long id, @RequestBody List<FriendDTO> friendsDTO) {
        generalService.addFriendOnTask(id, friendsDTO);
        return new ResponseEntity<>(new SuccessResult(), HttpStatus.OK);
    }

    @GetMapping("high-priority")
    public List<TaskDTO> getUserTasksHighPriority(Principal principal,
                                                  @RequestParam(required = false, defaultValue = "0") Integer page) {

        return generalService.getUserTasksHighPriority(principal.getName(),
                PageRequest.of(
                        page,
                        PAGE_TASK_SIZE,
                        Sort.Direction.DESC,
                        "id"
                )
        );
    }

    @GetMapping("for-select")
    public List<TaskDTO> getTasksForSelect(Principal principal) {
        return generalService.getTasks(principal.getName());
    }

    @GetMapping("tasklist-tasks")
    public List<TaskDTO> getTasksForTasklist(@RequestParam(required = false, defaultValue = "0") Integer page,
                                             @RequestParam Long id) {
        return generalService.getTasksForTasklist(id,
                PageRequest.of(
                        page,
                        PAGE_TASK_SIZE,
                        Sort.Direction.DESC,
                        "id"
                ));
    }

    @PostMapping("add-to-tasklist")
    public ResponseEntity<ResultDTO> addTaskToTasklist(@RequestParam Long taskId, @RequestBody Long listId, Principal principal) {
        try {
            generalService.addTaskToTasklist(principal.getName(), taskId, listId);
            return new ResponseEntity<>(new SuccessResult(), HttpStatus.OK);
        } catch (ExistsTaskAtListException e) {
            return new ResponseEntity<>(new BadRequestResult("Задача уже добавлена в список"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("delete-from-tasklist")
    public ResponseEntity<ResultDTO> deleteTaskFromTasklist(@RequestParam Long listId,
                                                            @RequestBody List<Long> tasksId) {
        generalService.deleteTaskFromTasklist(listId, tasksId);
        return new ResponseEntity<>(new SuccessResult(), HttpStatus.OK);
    }
}
