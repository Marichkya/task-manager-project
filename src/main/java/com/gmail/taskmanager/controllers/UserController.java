package com.gmail.taskmanager.controllers;

import com.gmail.taskmanager.dto.FriendDTO;
import com.gmail.taskmanager.dto.UserDTO;
import com.gmail.taskmanager.dto.results.BadRequestResult;
import com.gmail.taskmanager.dto.results.ResultDTO;
import com.gmail.taskmanager.dto.results.SuccessResult;
import com.gmail.taskmanager.services.GeneralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.ListIterator;

@RestController
@RequestMapping("user")
public class UserController {
    @Value("http://localhost:8080/")
    private String url;

    @Autowired
    private GeneralService generalService;

    @PostMapping("add")
    public ResponseEntity<ResultDTO> addUser(@RequestBody UserDTO userDTO) {
        try {
            generalService.addUser(userDTO);
            return new ResponseEntity<>(new SuccessResult(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new BadRequestResult("Користувач з таким email вже існує"), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("activate")
    public ResponseEntity<Void> activateUser(@RequestParam String code) {
        HttpHeaders headers = new HttpHeaders();
        try {
            generalService.activateUser(code);
            headers.setLocation(URI.create(url + "login.html?activate=true"));
            return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
        } catch (Exception e) {
            headers.setLocation(URI.create(url + "login.html?activate=false"));
            return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
        }
    }

    @PostMapping("delete-friend")
    public ResponseEntity<ResultDTO> deleteFriend(@RequestParam Long taskId, @RequestBody Long friendId) {
        generalService.deleteFriend(taskId, friendId);
        return new ResponseEntity<>(new SuccessResult(), HttpStatus.OK);
    }

    @GetMapping("for-friends-list")
    public List<FriendDTO> getUsersForFriendslist(@RequestParam Long id, Principal principal) {
        List<FriendDTO> allUsers = generalService.getAllUsers();
        List<FriendDTO> taskFriends = generalService.getFriendsOnTask(id);

        ListIterator<FriendDTO> iterator = allUsers.listIterator();
        while (iterator.hasNext()) {
            FriendDTO tempUser = iterator.next();
            if (tempUser.getUsername().equals(principal.getName())) {
                iterator.remove();
                continue;
            }
            for (FriendDTO tempFriend : taskFriends) {
                if (tempUser.equals(tempFriend)) {
                    iterator.remove();
                    break;
                }
            }
        }

        return allUsers;
    }

}
