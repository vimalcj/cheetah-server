package com.hackaton.cheetah.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackaton.cheetah.converter.ConverterUtil;
import com.hackaton.cheetah.model.Employee;
import com.hackaton.cheetah.model.LoginRequest;
import com.hackaton.cheetah.model.User;
import com.hackaton.cheetah.repository.EmployeeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class LoginControllerTest {

    private MockMvc mockMvc;

    @Mock
    EmployeeRepository employeeRepository;

    @Mock
    ConverterUtil converterUtil;

    ObjectMapper objectMapper;

    @InjectMocks
    LoginController loginController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(loginController)
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void ping() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/auth/ping"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void login() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("U123456");
        loginRequest.setPassword("12345");
        User user = User.builder().userId("U123456").build();
        Mockito.when(employeeRepository.findByUIDAndPassword(Mockito.anyString(), Mockito.anyString())).thenReturn(Optional.of(new Employee()));
        Mockito.when(converterUtil.convertToUser(Mockito.any(Employee.class))).thenReturn(user);
        String contentAsString = mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        Assertions.assertNotNull(contentAsString);
    }

    @Test
    public void resetPassword() throws Exception {

        Mockito.when(employeeRepository.findByUID(Mockito.anyString())).thenReturn(Optional.of(new Employee()));
        Mockito.when(employeeRepository.save(Mockito.any(Employee.class))).thenReturn(new Employee());
        String contentAsString = mockMvc.perform(MockMvcRequestBuilders.post("/auth/resetPassword?userName=U818098")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        Assertions.assertNotNull(contentAsString);
    }
}
