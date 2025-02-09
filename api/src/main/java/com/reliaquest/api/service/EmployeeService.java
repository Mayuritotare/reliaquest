package com.reliaquest.api.service;

import com.reliaquest.api.model.MockEmployee;

import java.util.List;

public interface EmployeeService {
    List<MockEmployee> getAllEmployees();
    List<MockEmployee> getEmployeesByNameSearch(String searchString);
    MockEmployee getEmployeeById(String id);
    Integer getHighestSalaryOfEmployees();
    List<String> getTopTenHighestEarningEmployeeNames();
    MockEmployee createEmployee(Object employeeInput);
    String deleteEmployeeById(String id);

}
