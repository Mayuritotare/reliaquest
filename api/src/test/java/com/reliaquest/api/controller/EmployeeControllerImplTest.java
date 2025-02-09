package com.reliaquest.api.controller;

import com.reliaquest.api.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmployeeControllerImplTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeControllerImpl employeeController;

    @Test
    void getAllEmployeesTest() {
        when(employeeService.getAllEmployees()).thenReturn(new ArrayList<>());
        var result = employeeController.getAllEmployees();
        assertNotNull(result);
    }

    @Test
    void getEmployeesByNameSearchTest(){
        String searchString = "Priyanka Test";
        when(employeeService.getEmployeesByNameSearch(searchString)).thenReturn(new ArrayList<>());
        var result = employeeController.getEmployeesByNameSearch(searchString);
       assertNotNull(result);
    }

    @Test
    void getEmployeeByIdTest(){
        String id = "1e0ff223-e4e7-4738-a769-54651f4a498c1e0ff223-e4e7-4738-a769-54651f4a498c";
        when(employeeService.getEmployeeById(id)).thenReturn(null);
        var result = employeeController.getEmployeeById(id);
//        assertNotNull(result);
    }

    @Test
    void getHighestSalaryOfEmployeesTest(){
        when(employeeService.getHighestSalaryOfEmployees()).thenReturn(null);
        var result = employeeController.getHighestSalaryOfEmployees();
    }

    @Test
    void getTopTenHighestEarningEmployeeNamesTest(){
        when(employeeService.getTopTenHighestEarningEmployeeNames()).thenReturn(null);
        var result = employeeController.getTopTenHighestEarningEmployeeNames();
    }

    @Test
    void createEmployeeTest(){
        when(employeeService.createEmployee(any())).thenReturn(null);
        var result = employeeController.createEmployee(null);
    }

    @Test
    public void deleteEmployeeByIdTest(){
        when(employeeService.deleteEmployeeById(any())).thenReturn(null);
        var result = employeeController.deleteEmployeeById(null);
    }
}
