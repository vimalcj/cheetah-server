package com.hackaton.cheetah.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Date;

@Data
@Getter
@Setter
@Table(name = "Employee_Pronouncation")
@Entity
public class Employee {

    @Id
    @Column(name="emp_id")
    private Long empId;

    @Column(name="emp_name")
    private String empName;

    @Column(name="is_admin")
    private Boolean isAdmin;

    @Column(name="is_active")
    private Boolean isActive;

    @Column(name="record_url")
    private String recordUrl;

    @Column(name="image_url")
    private String imageUrl;

    @Column(name="password")
    private String password;

    @Column(name="email")
    private String email;

    @Column(name="country_lang")
    private String country;

    @Column(name="created_date")
    private Timestamp createdDate;

    @Column(name="updated_date")
    private Timestamp updatedDate;

    public Employee() {

    }



    public Employee(Long empId, String empName, Boolean isAdmin, Boolean isActive, String recordUrl) {
        this.empId = empId;
        this.empName = empName;
        this.isAdmin = isAdmin;
        this.isActive = isActive;
        this.recordUrl = recordUrl;
    }

}
