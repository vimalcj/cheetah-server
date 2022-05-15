package com.hackaton.cheetah.service;

import com.hackaton.cheetah.model.Employee;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@Service
public class ExcelService {

    public List<Employee> readFile(MultipartFile file) {
        try {
            return ExcelHelperService.excelToEmployees(file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("fail to store excel data: " + e.getMessage());
        }
    }

}
