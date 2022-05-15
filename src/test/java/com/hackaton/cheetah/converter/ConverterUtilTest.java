package com.hackaton.cheetah.converter;

import com.hackaton.cheetah.model.Employee;
import com.hackaton.cheetah.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@ExtendWith(SpringExtension.class)
public class ConverterUtilTest {

    @InjectMocks
    ConverterUtil converterUtil;

    @BeforeEach
    public void setUp() {
        converterUtil.setSignaturePolicy("test");
    }

    @Test
    public void convertToUserTest() {
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
        employee.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
        List<User> users = converterUtil.convertToUser(Collections.singletonList(employee));
        Assertions.assertEquals(1, users.size());
    }
}
