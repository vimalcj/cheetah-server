package com.hackaton.cheetah.service;

import com.hackaton.cheetah.model.Employee;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@ExtendWith(SpringExtension.class)
public class ExcelHelperServiceTest {

    @Autowired
    private ResourceLoader resourceLoader;

    @Test
    public void excelToEmployees() throws IOException {
        InputStream inputStream = resourceLoader.getResource("classpath:Employees-Test.xlsx").getInputStream();
        List<Employee> employees = ExcelHelperService.excelToEmployees(inputStream);
        Assertions.assertFalse(employees.isEmpty());
    }

    @Test
    public void hasExcelFormat() {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.getContentType()).thenReturn(ExcelHelperService.TYPE);
        Assertions.assertTrue(ExcelHelperService.hasExcelFormat(file));
    }
}
