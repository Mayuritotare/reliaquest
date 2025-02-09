package com.reliaquest.api.controller;
import com.reliaquest.api.model.MockEmployee;
import com.reliaquest.api.service.EmployeeService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
@Slf4j
@RestController()
@RequestMapping("v1/api/iemployee")
public class EmployeeControllerImpl implements IEmployeeController{
    @Autowired
    EmployeeService employeeService;


    @Override
    public ResponseEntity<List> getAllEmployees() {
        log.info("Request received to fetch all employees");
        List<MockEmployee> allEmployees = employeeService.getAllEmployees();
        log.info("Fetched all employees successfully {}", allEmployees);
        return ResponseEntity.ok(allEmployees);
    }


    @Override
    public ResponseEntity<List> getEmployeesByNameSearch(@PathVariable String searchString) {
        log.info("Request received to fetch all employees based on their names");
        List<MockEmployee> employeesByNameSearch = employeeService.getEmployeesByNameSearch(searchString);
        log.info("Fetched all employees based on their names successfully {}", employeesByNameSearch);

        return new ResponseEntity<>(employeesByNameSearch, HttpStatus.OK);
    }

    @Override
    public ResponseEntity getEmployeeById(@PathVariable String id) {
        log.info("Request received to find employee by ID {}", id);
        MockEmployee employeeById = employeeService.getEmployeeById(id);
        log.info("Fetched employee successfully {}", employeeById);
        return new ResponseEntity<>(employeeById, HttpStatus.OK);
    }


    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        log.info("Request received to fetch highest salary of employees");
        Integer highestSalaryOfEmployees = employeeService.getHighestSalaryOfEmployees();
        log.info("Highest salary of employee is {}", highestSalaryOfEmployees);
        return new ResponseEntity<>(highestSalaryOfEmployees, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        log.info("Request received to fetch top ten highest Earning  employees");
        List<String> topTenHighestEarningEmployeeNames = employeeService.getTopTenHighestEarningEmployeeNames();
        log.info("Top ten highest Earning  employee is {}", topTenHighestEarningEmployeeNames);
        return new ResponseEntity<>(topTenHighestEarningEmployeeNames, HttpStatus.OK);
    }

    @Override
    public ResponseEntity createEmployee(Object employeeInput) {
        log.info("Request received to create employee with input {}", employeeInput);
        MockEmployee employee = employeeService.createEmployee(employeeInput);
        log.info("Created employee successfully {}", employee);
        return new ResponseEntity<>(employee, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(@PathVariable String id) {
        log.info("Request received to  delete employee by id {}", id);
        String deletedEmployee = employeeService.deleteEmployeeById(id);
        log.info("Deleted employee successfully {}", deletedEmployee);
        return new ResponseEntity<>(deletedEmployee, HttpStatus.OK);
    }


}
