package com.hackaton.cheetah.controller;

import com.hackaton.cheetah.model.LoginRequest;
import com.hackaton.cheetah.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cheetah")
@Slf4j
public class LoginController {

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody LoginRequest loginRequest) {
        //log.info("Login called {}", loginRequest);
        User user = User.builder().userId("admin").name("ADMIN").build();
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
