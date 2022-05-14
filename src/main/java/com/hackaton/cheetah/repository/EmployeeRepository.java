package com.hackaton.cheetah.repository;

import com.hackaton.cheetah.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>{
    List<Employee> findByEmpName(String name);
    Optional<Employee> findByUIDAndPassword(String uid, String password);
}
