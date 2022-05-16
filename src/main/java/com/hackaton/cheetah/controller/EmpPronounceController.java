package com.hackaton.cheetah.controller;

import com.hackaton.cheetah.converter.ConverterUtil;
import com.hackaton.cheetah.model.Employee;
import com.hackaton.cheetah.model.User;
import com.hackaton.cheetah.repository.EmployeeRepository;
import com.hackaton.cheetah.service.ExcelHelperService;
import com.hackaton.cheetah.service.ExcelService;
import com.hackaton.cheetah.service.TextToSpeechService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;
import java.util.Base64;
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

    @Autowired
    ConverterUtil converterUtil;

    @Value("${user.defaults.password}")
    private String defaultPassword;

    @Value("${azure.storage.avatarUrl}")
    private String defaultAvatar;

    @GetMapping("/getAllEmployees")
    public ResponseEntity<List<User>> getAllEmployees() {
        try {
            List<Employee> employeeList = employeeRepository.findAll();
            if (employeeList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(converterUtil.convertToUser(employeeList), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/standard/record")
    public ResponseEntity<User> postVoiceRecord(@RequestParam("userName") String UID) {
        try {
            Optional<Employee> employeeOptional = employeeRepository.findByUID(UID);
            if (employeeOptional.isPresent()) {
                return new ResponseEntity<>(converterUtil.convertToUser(textToSpeechService.synthesisToMp3FileAsync(employeeOptional.get())), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        } catch (Exception ex) {
            log.error("error while uploading standard audio record...", ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping(value = "/admin/upload", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String message;
        int errorCount = 0;
        if (ExcelHelperService.hasExcelFormat(file)) {
            try {
                List<Employee> employeeList = excelFileService.readFile(file);
                if (employeeList.isEmpty()) {
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                } else {
                    for (Employee employee : employeeList) {
                        employee.setPassword(defaultPassword);
                        employee.setImageUrl(defaultAvatar);
                        try {
                            textToSpeechService.synthesisToMp3FileAsync(employee);
                        } catch (DataIntegrityViolationException | SQLException ex) {
                            errorCount++;
                        }
                    }
                }

                if (errorCount == 0) {
                    message = "Uploaded the file successfully: " + file.getOriginalFilename();
                } else {
                    message = String.format("Bulk upload failed for %s out of %s records!!", errorCount, employeeList.size());
                }
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
    public ResponseEntity<User> updateVoiceRecord(@RequestParam("userName") String userName,
                                                  @RequestParam("file") MultipartFile file) {
        try {
            Optional<Employee> employee = employeeRepository.findByUID(userName);
            if (employee.isPresent()) {
                Employee UpdatedEmp = textToSpeechService.updateExistingVoiceFile(file.getBytes(), employee.get(), FilenameUtils.getExtension(file.getOriginalFilename()));
                return new ResponseEntity<>(converterUtil.convertToUser(UpdatedEmp), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            log.error("error while uploading teh audio file", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @GetMapping("/search/{empId}")
//    public ResponseEntity<User> findByEmployeeId(@PathVariable("empId") String UID) {
//        try {
//            Optional<Employee> employee = employeeRepository.findByUID(UID);
//            if (employee.isEmpty()) {
//                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//            }
//            return new ResponseEntity<>(converterUtil.convertToUser(employee.get()), HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//    }
//
//    @GetMapping("/search/employee/{empName}")
//    public ResponseEntity<List<User>> findByEmployeeName(@PathVariable("empName") String empName) {
//        try {
//            List<Employee> employeeList = employeeRepository.findByEmpName(empName);
//            if (employeeList.isEmpty()) {
//                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//            }
//            return new ResponseEntity<>(converterUtil.convertToUser(employeeList), HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
//        }
//    }

    @GetMapping("/genericSearch/employee/{searchString}")
    public ResponseEntity<List<User>> searchEmployees(@PathVariable("searchString") String searchString) {
        return new ResponseEntity<>(converterUtil.convertToUser(employeeRepository.employeeGenericSearch(searchString)), HttpStatus.OK);
    }

    @PostMapping(value = "/v2/record")
    public ResponseEntity<User> stream(@RequestParam("userName") String userName, @RequestParam("file") String base64Audio) {
        try {
            System.out.println("incoming message ...");
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] decodedByte = decoder.decode(base64Audio.split(",")[1]);

            Optional<Employee> employee = employeeRepository.findByUID(userName);
            if (employee.isPresent()) {
                Employee UpdatedEmp = textToSpeechService.updateExistingVoiceFile(decodedByte, employee.get(), "wav");
                return new ResponseEntity<>(converterUtil.convertToUser(UpdatedEmp), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            log.error("error while uploading teh audio file", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
