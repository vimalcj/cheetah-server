package com.hackaton.cheetah.controller;

import com.hackaton.cheetah.model.Employee;
import com.hackaton.cheetah.model.LoginRequest;
import com.hackaton.cheetah.model.User;
import com.hackaton.cheetah.repository.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin
@Slf4j
public class LoginController {

    @Autowired
    EmployeeRepository employeeRepository;

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return new ResponseEntity<>("Cheetah server is up and running", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody @Valid LoginRequest loginRequest) {
        Optional<Employee> byEmpNameAndPassword = employeeRepository.findByUIDAndPassword(loginRequest.getUsername(), loginRequest.getPassword());

        log.info("Login called {}", loginRequest);
        if (byEmpNameAndPassword.isEmpty()) {
            log.info("employee  not found");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Employee employee = byEmpNameAndPassword.get();
        User user = User.builder().userId(employee.getEmpId())
                .name(employee.getEmpName())
                .admin(employee.getIsAdmin())
                .email(employee.getEmail())
                .imageUrl(employee.getImageUrl())
                .recordUrl(employee.getRecordUrl()).build();
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
