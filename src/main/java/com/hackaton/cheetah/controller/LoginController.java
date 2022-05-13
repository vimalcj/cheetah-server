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

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cheetah")
@Slf4j
public class LoginController {

    @PostMapping("/ping")
    public ResponseEntity<String> ping(@RequestBody LoginRequest loginRequest) {
        return new ResponseEntity<>("Cheetah server is up and running", HttpStatus.OK);
    }
    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody LoginRequest loginRequest) {
        //log.info("Login called {}", loginRequest);
        User user = User.builder().userId("admin").name("ADMIN").build();
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/employees")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        try {
            List<Employee> employeeList = new ArrayList<Employee>();
            employeeList= employeeRepository.findAll();
            if (employeeList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(employeeList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Autowired
    EmployeeRepository employeeRepository;

    @PostMapping("/employees")
    public ResponseEntity<Employee> postEmployees(@RequestBody Employee employee) {
        employeeRepository.save(employee);
        return new ResponseEntity<Employee>(employee, HttpStatus.OK);
    }
}
