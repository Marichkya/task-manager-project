package com.gmail.taskmanager.controllers;

import com.gmail.taskmanager.dto.TasklistDTO;
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
import java.util.List;

@RestController
@RequestMapping("tasklist")
public class TasklistController {
    private static final int PAGE_TASK_SIZE = 5;

    @Autowired
    private GeneralService generalService;

    @GetMapping("details")
    public TasklistDTO getTasklistDetails(@RequestParam Long id) {
        return generalService.getTasklist(id);
    }

    @GetMapping("all")
    public List<TasklistDTO> getTasklists(@RequestParam(required = false, defaultValue = "0") Integer page,
                                          Principal principal) {
        return generalService.getTasklists(principal.getName(),
                PageRequest.of(
                        page,
                        PAGE_TASK_SIZE,
                        Sort.Direction.DESC,
                        "id"
                )
        );
    }

    @PostMapping("add")
    public ResponseEntity<ResultDTO> addTasklist(@RequestBody TasklistDTO taskListDTO, Principal principal) {
        generalService.addTasklist(principal.getName(), taskListDTO);
        return new ResponseEntity<>(new SuccessResult(), HttpStatus.OK);
    }

    @PostMapping("delete")
    public ResponseEntity<ResultDTO> deleteTaskList(@RequestParam Long id) {
        generalService.deleteTasklist(id);
        return new ResponseEntity<>(new SuccessResult(), HttpStatus.OK);
    }

    @PostMapping("change")
    public ResponseEntity<ResultDTO> changeTasklist(@RequestBody TasklistDTO taskListDTO) {
        generalService.changeTasklist(taskListDTO);
        return new ResponseEntity<>(new SuccessResult(), HttpStatus.OK);
    }

    @GetMapping("for-select")
    public List<TasklistDTO> getTasklists(Principal principal) {
        return generalService.getTasklists(principal.getName());
    }
}
