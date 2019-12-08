package com.gmail.taskmanager.controllers;

import com.gmail.taskmanager.dto.CommentDTO;
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
@RequestMapping("comment")
public class CommentController {
    private static final int PAGE_COMMENT_SIZE = 5;

    @Autowired
    private GeneralService generalService;

    @PostMapping("add")
    public ResponseEntity<ResultDTO> addComment(Principal principal, @RequestBody CommentDTO commentDTO) {
        generalService.addComment(principal.getName(), commentDTO);
        return new ResponseEntity<>(new SuccessResult(), HttpStatus.OK);
    }

    @GetMapping("all")
    public List<CommentDTO> getComments(@RequestParam(required = false, defaultValue = "0") Integer page,
                                        @RequestParam Long taskId) {
        return generalService.getComments(taskId,
                PageRequest.of(
                        page,
                        PAGE_COMMENT_SIZE,
                        Sort.Direction.DESC,
                        "id"
                )
        );
    }
}
