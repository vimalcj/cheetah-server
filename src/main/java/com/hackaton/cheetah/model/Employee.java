package com.hackaton.cheetah.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Getter
@Setter
@Table(name = "Employee_Pronounce_test")
@Entity
public class Employee {

    @Id
    private Long empId;

    private String empName;

    private String recordUrl;

    public Employee() {

    }
    public Employee(String empName, Long empId, String recordUrl) {
        this.empName = empName;
        this.empId = empId;
        this.recordUrl = recordUrl;
    }

}
