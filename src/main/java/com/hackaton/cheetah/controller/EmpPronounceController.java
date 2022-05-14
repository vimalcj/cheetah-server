package com.hackaton.cheetah.controller;

import com.hackaton.cheetah.model.Employee;
import com.hackaton.cheetah.repository.EmployeeRepository;
import com.hackaton.cheetah.service.ExcelHelperService;
import com.hackaton.cheetah.service.ExcelService;
import com.hackaton.cheetah.service.TextToSpeechService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/cheetah")
@CrossOrigin
@Slf4j
public class EmpPronounceController {

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    TextToSpeechService textToSpeechService;

    @Autowired
    ExcelService excelFileService;

    @GetMapping("/getAllEmployees")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        try {
            List<Employee> employeeList = employeeRepository.findAll();
            if (employeeList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(employeeList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/standard/record")
    public ResponseEntity<Employee> postVoiceRecord(@RequestBody Employee employee) {
        try {
            textToSpeechService.synthesisToMp3FileAsync(employee);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(employee, HttpStatus.OK);
    }


    @PostMapping(value = "/admin/upload", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String message;
        if (ExcelHelperService.hasExcelFormat(file)) {
            try {
                List<Employee> employeeList = excelFileService.readFile(file);
                if (employeeList.isEmpty()) {
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                } else {
                    for (Employee employee : employeeList) {
                        textToSpeechService.synthesisToMp3FileAsync(employee);
                    }
                }
                message = "Uploaded the file successfully: " + file.getOriginalFilename();
                return ResponseEntity.status(HttpStatus.OK).body(message);
            } catch (Exception e) {
                message = "Could not upload the file: " + file.getOriginalFilename() + "!";
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
            }
        }
        message = "Please upload an excel file!";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }


    @PostMapping(value = "/record", consumes = "multipart/form-data")
    public ResponseEntity<Employee> updateVoiceRecord(@RequestParam("empId") Long empId,
                                                      @RequestParam("file") MultipartFile file) {
        Employee UpdatedEmp = null;
        try {
            Optional<Employee> employee = employeeRepository.findById(empId);
            if (!employee.isEmpty()) {
                UpdatedEmp = textToSpeechService.updateExistingVoiceFile(file.getBytes(), employee.get());
            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(UpdatedEmp, HttpStatus.OK);
    }

    @GetMapping("/search/{empId}")
    public ResponseEntity<Employee> findByEmployeeId(@PathVariable("empId") Long empId) {
        try {
            Optional<Employee> employee = employeeRepository.findById(empId);
            if (employee.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(employee.get(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/search/employee/{empName}")
    public ResponseEntity<List<Employee>> findByEmployeeName(@PathVariable("empName") String empName) {
        try {
            List<Employee> employeeList = employeeRepository.findByEmpName(empName);
            if (employeeList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(employeeList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }


}
