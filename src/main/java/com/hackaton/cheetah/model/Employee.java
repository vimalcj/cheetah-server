package com.hackaton.cheetah.model;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Builder
@Table(name = "Employee_Pronounce")
@Entity
public class Employee {
    public Employee(String empName, String empId, String recordUrl) {
        this.empName = empName;
        this.empId = empId;
        this.recordUrl = recordUrl;
    }

    private String empName;
    @Id
    private String empId;
    private String recordUrl;

    public Employee() {

    }
}
