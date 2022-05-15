package com.hackaton.cheetah.repository;

import com.hackaton.cheetah.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByEmpName(String name);

    Optional<Employee> findByUIDAndPassword(String uid, String password);

    Optional<Employee> findByUID(String uid);

    @Query("SELECT e FROM Employee e WHERE e.empName like %?1% OR e.UID LIKE %?1% OR e.email LIKE %?1%")
    List<Employee> employeeGenericSearch(String searchString);
}
