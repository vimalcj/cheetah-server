package com.hackaton.cheetah.service;

import com.hackaton.cheetah.model.Employee;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@ExtendWith(SpringExtension.class)
public class ExcelServiceTest {

    @InjectMocks
    ExcelService excelService;

    @Autowired
    private ResourceLoader resourceLoader;

    @Test
    public void readFile() throws IOException {
        InputStream inputStream = resourceLoader.getResource("classpath:Employees-Test.xlsx").getInputStream();

        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.getInputStream()).thenReturn(inputStream);
        List<Employee> employees = excelService.readFile(file);
        Assertions.assertFalse(employees.isEmpty());
    }

    @Test
    public void readFileExceptionCase() throws IOException {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.getInputStream()).thenThrow(new IOException(""));
        Assertions.assertThrows(RuntimeException.class, () -> excelService.readFile(file));
    }
}
