package com.gmail.taskmanager.controllers;

import com.gmail.taskmanager.dto.UserDTO;
import com.gmail.taskmanager.services.GeneralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
public class MainController {
    @Value("${host.redirect.url}")
    private String url;

    @Autowired
    private GeneralService generalService;

    @GetMapping("/")
    public ResponseEntity<Void> index() {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(url + "index.html"));
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    }

    @GetMapping("login")
    public ResponseEntity<Void> loginPage() {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(url + "login.html"));
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    }

    @GetMapping("registration")
    public ResponseEntity<Void> registration() {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(url + "registration.html"));
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    }

    @GetMapping("check")
    public Map<String, String> checkLogin(Principal principal) {
        Map<String, String> result = new HashMap<>();
        if (principal != null) {
            UserDTO userDTO = generalService.getUser(principal.getName());
            result.put("id", userDTO.getId().toString());
            result.put("name", userDTO.getName());
            result.put("username", userDTO.getUsername());
            result.put("phone", userDTO.getPhone());
            result.put("activationCode", userDTO.getActivationCode());
            if (((userDTO.getOtherSiteName() == "") || (userDTO.getOtherSiteName() == null))
                    && ((userDTO.getOtherSiteUsername() == "") || (userDTO.getOtherSiteUsername() == null))) {
                result.put("noRegister", "false");
            } else {
                result.put("noRegister", "true");
            }
            return result;
        }
        return result;
    }
}
