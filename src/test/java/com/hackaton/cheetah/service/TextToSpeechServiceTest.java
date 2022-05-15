package com.hackaton.cheetah.service;

import com.hackaton.cheetah.model.Employee;
import com.hackaton.cheetah.repository.EmployeeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@ExtendWith(SpringExtension.class)
public class TextToSpeechServiceTest {

    @Mock
    EmployeeRepository employeeRepository;

    @InjectMocks
    TextToSpeechService textToSpeechService;

    @Test
    public void synthesisToMp3FileAsyncTest() throws Exception {
        Employee employee = new Employee();
        employee.setUID("U818098");
        employee.setCountry("IN");
        employee.setEmail("test@gmail.com");
        employee.setEmpId(1L);
        employee.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        employee.setRecordUrl(null);
        employee.setIsActive(true);
        employee.setIsAdmin(true);
        Mockito.when(employeeRepository.save(Mockito.any())).thenReturn(employee);

        Employee employee1 = textToSpeechService.synthesisToMp3FileAsync(employee);
        Assertions.assertNotNull(employee1.getRecordUrl());
    }

    @Test
    public void updateExistingVoiceFileTest() {
        Employee employee = new Employee();
        employee.setUID("U818098");
        employee.setCountry("IN");
        employee.setEmail("test@gmail.com");
        employee.setEmpId(1L);
        employee.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        employee.setRecordUrl(null);
        employee.setIsActive(true);
        employee.setIsAdmin(true);
        Mockito.when(employeeRepository.save(Mockito.any())).thenReturn(employee);
        byte[] str = "somedata".getBytes(StandardCharsets.UTF_8);
        Employee employee1 = textToSpeechService.updateExistingVoiceFile(str, employee, "mp3");
        Assertions.assertNotNull(employee1.getRecordUrl());
    }

}
