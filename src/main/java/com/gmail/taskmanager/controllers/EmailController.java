package com.gmail.taskmanager.controllers;

import com.gmail.taskmanager.dto.FriendDTO;
import com.gmail.taskmanager.dto.TaskDTO;
import com.gmail.taskmanager.dto.UserDTO;
import com.gmail.taskmanager.dto.results.ResultDTO;
import com.gmail.taskmanager.dto.results.SuccessResult;
import com.gmail.taskmanager.mail.EmailSender;
import com.gmail.taskmanager.services.GeneralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("send")
public class EmailController {
    @Autowired
    private EmailSender emailSender;

    @Autowired
    private GeneralService generalService;

    @GetMapping("comment-notification")
    public ResponseEntity<ResultDTO> sendCommentNotification(@RequestParam Long id, Principal principal) {
        UserDTO authorTask = generalService.getAuthorTask(id);
        TaskDTO task = generalService.getTask(id, principal.getName());
        emailSender.sendEmailAboutComment(authorTask, task);
        return new ResponseEntity<>(new SuccessResult(), HttpStatus.OK);
    }

    @PostMapping("shared-task-notification")
    public ResponseEntity<ResultDTO> sendSharedTaskNotification(@RequestParam Long id, @RequestBody List<FriendDTO> friends) {
        UserDTO authorTask = generalService.getAuthorTask(id);
        emailSender.sendEmailAboutSharedTask(authorTask, friends);
        return new ResponseEntity<>(new SuccessResult(), HttpStatus.OK);
    }

    @GetMapping("comment-notification-for-friends")
    public ResponseEntity<ResultDTO> sendCommentNotificationToFriends(@RequestParam Long id, Principal principal) {
        UserDTO user = generalService.getAuthorTask(id);
        TaskDTO task = generalService.getTask(id, principal.getName());
        List<FriendDTO> friends = generalService.getFriendsOnTask(id);
        emailSender.sendEmailAboutComment(user, friends, task);
        return new ResponseEntity<>(new SuccessResult(), HttpStatus.OK);
    }
}
