package com.gmail.taskmanager.controllers;

import com.gmail.taskmanager.dto.UserDTO;
import com.gmail.taskmanager.dto.UserNotificationsDTO;
import com.gmail.taskmanager.dto.results.BadRequestResult;
import com.gmail.taskmanager.dto.results.ResultDTO;
import com.gmail.taskmanager.dto.results.SuccessResult;
import com.gmail.taskmanager.services.GeneralService;
import com.gmail.taskmanager.services.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("settings")
public class SettingsController {
    @Autowired
    private GeneralService generalService;

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("")
    public ResponseEntity<Void> settings() {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("http://localhost:8080/settings.html"));
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    }

    @GetMapping("allowed-notifications")
    public Map<String, Boolean> getAllowedNotifications(Principal principal) {
        Map<String, Boolean> map = new HashMap<>();
        UserNotificationsDTO userNotificationsDTO = generalService.getUserNotifications(principal.getName());
        map.put("email", userNotificationsDTO.isEmailNotifications());
        map.put("sms", userNotificationsDTO.isSmsNotifications());
        return map;
    }

    @PostMapping("change-personal-data")
    public ResponseEntity<ResultDTO> changePersonalData(@RequestBody Map<String, String> data, Principal principal) {
        String name = data.get("name");
        String phone = data.get("phone");
        try {
            settingsService.changePersonalData(principal.getName(), name, phone);
            return new ResponseEntity<>(new SuccessResult(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new BadRequestResult("Щось пішло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

    @PostMapping("change-security-data")
    public ResponseEntity<ResultDTO> changeSecurityData(@RequestBody Map<String, String> data, Principal principal) {
        String oldPasswordHash = passwordEncoder.encode(data.get("oldPassword"));
        UserDTO userDTO = generalService.getUser(principal.getName());
        if (oldPasswordHash.equals(userDTO.getPassword())) {
            String newPasswordHash = passwordEncoder.encode(data.get("newPassword"));
            settingsService.changeUserSecurityData(principal.getName(), newPasswordHash);
            return new ResponseEntity<>(new SuccessResult(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new BadRequestResult("Неправильно введений старий пароль"), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("change-notifications")
    public ResponseEntity<ResultDTO> changeNotifications(@RequestBody UserNotificationsDTO userNotificationsDTO, Principal principal) {
        try {
            settingsService.changeUserNotifications(principal.getName(), userNotificationsDTO);
            return new ResponseEntity<>(new SuccessResult(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new BadRequestResult("Щось пішло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
