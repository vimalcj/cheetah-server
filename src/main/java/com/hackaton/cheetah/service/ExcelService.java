package com.hackaton.cheetah.service;

import java.io.IOException;
import java.util.List;

import com.hackaton.cheetah.model.Employee;
import com.hackaton.cheetah.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class ExcelService {
    @Autowired
    EmployeeRepository employeeRepository;
    public List<Employee> readFile(MultipartFile file) {
        try {
            List<Employee> employees = ExcelHelperService.excelToEmployees(file.getInputStream());
            //employeeRepository.saveAll(employees);
            return  employees;
        } catch (IOException e) {
            throw new RuntimeException("fail to store excel data: " + e.getMessage());
        }
    }

}
