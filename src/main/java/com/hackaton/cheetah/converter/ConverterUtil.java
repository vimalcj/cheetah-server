package com.hackaton.cheetah.converter;

import com.hackaton.cheetah.model.Employee;
import com.hackaton.cheetah.model.User;

public class ConverterUtil {

    public static User convertToUser(Employee employee) {
        return User.builder().userId(employee.getUID())
                .empId(employee.getEmpId())
                .name(employee.getEmpName())
                .admin(employee.getIsAdmin())
                .email(employee.getEmail())
                .imageUrl(employee.getImageUrl())
                .recordUrl(employee.getRecordUrl()).build();
    }

}
