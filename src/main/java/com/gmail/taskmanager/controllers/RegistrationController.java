package com.gmail.taskmanager.controllers;

import com.gmail.taskmanager.dto.UserDTO;
import com.gmail.taskmanager.dto.results.BadRequestResult;
import com.gmail.taskmanager.dto.results.ResultDTO;
import com.gmail.taskmanager.dto.results.SuccessResult;
import com.gmail.taskmanager.models.User;
import com.gmail.taskmanager.models.UserNotifications;
import com.gmail.taskmanager.services.GeneralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class RegistrationController {
    @Autowired
    private GeneralService generalService;

//    @GetMapping("/registration")
//    public ResponseEntity<Void> registration() {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setLocation(URI.create("http://localhost:8080/registration.html"));
//        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
//    }

    @PostMapping("/registration")
    public ResponseEntity<ResultDTO> addUser(@RequestBody UserDTO userDTO) {
        try {
            generalService.addUser(userDTO);
            return new ResponseEntity<>(new SuccessResult(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new BadRequestResult("Користувач з таким email вже існує"), HttpStatus.BAD_REQUEST);
        }
    }
}
