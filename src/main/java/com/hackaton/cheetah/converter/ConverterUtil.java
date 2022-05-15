package com.hackaton.cheetah.converter;

import com.hackaton.cheetah.model.Employee;
import com.hackaton.cheetah.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class ConverterUtil {

    public static User convertToUser(Employee employee) {
        return User.builder().userId(employee.getUID())
                .empId(employee.getEmpId())
                .name(employee.getEmpName())
                .admin(employee.getIsAdmin())
                .email(employee.getEmail())
                .imageUrl(employee.getImageUrl())
                .recordUrl(employee.getRecordUrl())
                .createdTs(employee.getCreatedDate())
                .modifiedTs(employee.getUpdatedDate())
                .build();
    }

    public static List<User> convertToUser(List<Employee> employees) {
        return employees.stream().map(ConverterUtil::convertToUser).collect(Collectors.toList());
    }


}
