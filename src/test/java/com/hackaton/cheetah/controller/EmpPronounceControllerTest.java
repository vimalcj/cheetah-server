package com.hackaton.cheetah.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackaton.cheetah.converter.ConverterUtil;
import com.hackaton.cheetah.model.Employee;
import com.hackaton.cheetah.repository.EmployeeRepository;
import com.hackaton.cheetah.service.ExcelHelperService;
import com.hackaton.cheetah.service.ExcelService;
import com.hackaton.cheetah.service.TextToSpeechService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class EmpPronounceControllerTest {

    private MockMvc mockMvc;

    @Mock
    EmployeeRepository employeeRepository;

    @Mock
    ConverterUtil converterUtil;

    @Mock
    TextToSpeechService textToSpeechService;

    @Mock
    ExcelService excelFileService;

    ObjectMapper objectMapper;

    @Autowired
    private ResourceLoader resourceLoader;

    @InjectMocks
    EmpPronounceController empPronounceController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(empPronounceController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void getAllEmployees() throws Exception {
        Employee employee = new Employee();
        employee.setUID("U818098");
        employee.setImageUrl("url");
        employee.setCountry("IN");
        employee.setEmail("test@gmail.com");
        employee.setEmpId(1L);
        employee.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        employee.setImageUrl("url");
        employee.setIsActive(true);
        employee.setIsAdmin(true);

        Mockito.when(converterUtil.convertToUser(Mockito.anyList())).thenCallRealMethod();

        Mockito.when(employeeRepository.findAll()).thenReturn(Collections.singletonList(employee));
        mockMvc.perform(MockMvcRequestBuilders.get("/cheetah/getAllEmployees")).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void postVoiceRecord() throws Exception {
        Employee employee = new Employee();
        employee.setUID("U818098");
        employee.setImageUrl("url");
        employee.setCountry("IN");
        employee.setEmail("test@gmail.com");
        employee.setEmpId(1L);
        employee.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        employee.setImageUrl("url");
        employee.setIsActive(true);
        employee.setIsAdmin(true);

        Mockito.when(converterUtil.convertToUser(Mockito.anyList())).thenCallRealMethod();
        Mockito.when(employeeRepository.findByUID(Mockito.anyString())).thenReturn(Optional.of(employee));
        Mockito.when(textToSpeechService.synthesisToMp3FileAsync(Mockito.any(Employee.class))).thenReturn(employee);
        mockMvc.perform(MockMvcRequestBuilders.post("/cheetah/standard/record?userName=U123456").contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void uploadFile() throws Exception {
        Employee employee = new Employee();
        employee.setUID("U818098");
        employee.setImageUrl("url");
        employee.setCountry("IN");
        employee.setEmail("test@gmail.com");
        employee.setEmpId(1L);
        employee.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        employee.setImageUrl("url");
        employee.setIsActive(true);
        employee.setIsAdmin(true);
        MockMultipartFile firstFile = new MockMultipartFile("file", "filename.xlsx", ExcelHelperService.TYPE, resourceLoader.getResource("classpath:Employees-Test.xlsx").getInputStream().readAllBytes());

        Mockito.when(converterUtil.convertToUser(Mockito.anyList())).thenCallRealMethod();
        Mockito.when(employeeRepository.findByUID(Mockito.anyString())).thenReturn(Optional.of(employee));
        Mockito.when(textToSpeechService.synthesisToMp3FileAsync(Mockito.any(Employee.class))).thenReturn(employee);
        Mockito.when(excelFileService.readFile(Mockito.any())).thenReturn(Collections.singletonList(employee));
        mockMvc.perform(MockMvcRequestBuilders.multipart("/cheetah/admin/upload")
                        .file(firstFile))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void uploadFilePartialSuccess() throws Exception {
        Employee employee = new Employee();
        employee.setUID("U818098");
        employee.setImageUrl("url");
        employee.setCountry("IN");
        employee.setEmail("test@gmail.com");
        employee.setEmpId(1L);
        employee.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        employee.setImageUrl("url");
        employee.setIsActive(true);
        employee.setIsAdmin(true);
        MockMultipartFile firstFile = new MockMultipartFile("file", "filename.xlsx", ExcelHelperService.TYPE, resourceLoader.getResource("classpath:Employees-Test.xlsx").getInputStream().readAllBytes());

        Mockito.when(converterUtil.convertToUser(Mockito.anyList())).thenCallRealMethod();
        Mockito.when(employeeRepository.findByUID(Mockito.anyString())).thenReturn(Optional.of(employee));
        Mockito.when(textToSpeechService.synthesisToMp3FileAsync(Mockito.any(Employee.class))).thenThrow(new DataIntegrityViolationException(""));
        Mockito.when(excelFileService.readFile(Mockito.any())).thenReturn(Collections.singletonList(employee));
        mockMvc.perform(MockMvcRequestBuilders.multipart("/cheetah/admin/upload")
                        .file(firstFile))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void uploadFileException() throws Exception {
        Employee employee = new Employee();
        employee.setUID("U818098");
        employee.setImageUrl("url");
        employee.setCountry("IN");
        employee.setEmail("test@gmail.com");
        employee.setEmpId(1L);
        employee.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        employee.setImageUrl("url");
        employee.setIsActive(true);
        employee.setIsAdmin(true);
        MockMultipartFile firstFile = new MockMultipartFile("file", "filename.xlsx", ExcelHelperService.TYPE, resourceLoader.getResource("classpath:Employees-Test.xlsx").getInputStream().readAllBytes());

        Mockito.when(excelFileService.readFile(Mockito.any())).thenThrow(new NullPointerException(""));
        mockMvc.perform(MockMvcRequestBuilders.multipart("/cheetah/admin/upload")
                        .file(firstFile))
                .andExpect(MockMvcResultMatchers.status().isExpectationFailed());
    }

    @Test
    public void uploadFileInvalidFileType() throws Exception {
        Employee employee = new Employee();
        employee.setUID("U818098");
        employee.setImageUrl("url");
        employee.setCountry("IN");
        employee.setEmail("test@gmail.com");
        employee.setEmpId(1L);
        employee.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        employee.setImageUrl("url");
        employee.setIsActive(true);
        employee.setIsAdmin(true);
        MockMultipartFile firstFile = new MockMultipartFile("file", "filename.xlsx", "text/plain", resourceLoader.getResource("classpath:Employees-Test.xlsx").getInputStream().readAllBytes());
        mockMvc.perform(MockMvcRequestBuilders.multipart("/cheetah/admin/upload")
                        .file(firstFile))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void updateVoiceRecord() throws Exception {
        Employee employee = new Employee();
        employee.setUID("U818098");
        employee.setImageUrl("url");
        employee.setCountry("IN");
        employee.setEmail("test@gmail.com");
        employee.setEmpId(1L);
        employee.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        employee.setImageUrl("url");
        employee.setIsActive(true);
        employee.setIsAdmin(true);
        MockMultipartFile firstFile = new MockMultipartFile("file", "filename.xlsx", ExcelHelperService.TYPE, resourceLoader.getResource("classpath:Employees-Test.xlsx").getInputStream().readAllBytes());

        Mockito.when(employeeRepository.findByUID(Mockito.anyString())).thenReturn(Optional.of(employee));
        Mockito.when(textToSpeechService.updateExistingVoiceFile(Mockito.any(), Mockito.any(Employee.class), Mockito.anyString())).thenReturn(employee);
        Mockito.when(converterUtil.convertToUser(Mockito.anyList())).thenCallRealMethod();
        mockMvc.perform(MockMvcRequestBuilders.multipart("/cheetah/record")
                        .file(firstFile).param("userName", "U123456"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void searchEmployee() throws Exception {
        Employee employee = new Employee();
        employee.setUID("U818098");
        employee.setImageUrl("url");
        employee.setCountry("IN");
        employee.setEmail("test@gmail.com");
        employee.setEmpId(1L);
        employee.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        employee.setImageUrl("url");
        employee.setIsActive(true);
        employee.setIsAdmin(true);
        Mockito.when(employeeRepository.employeeGenericSearch(Mockito.anyString())).thenReturn(Collections.singletonList(employee));
        Mockito.when(converterUtil.convertToUser(Mockito.anyList())).thenCallRealMethod();
        mockMvc.perform(MockMvcRequestBuilders.get("/cheetah/genericSearch/employee/U123"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

}
