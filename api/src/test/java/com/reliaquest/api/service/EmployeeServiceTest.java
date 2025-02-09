package com.reliaquest.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.config.MockEmployeeClient;
import com.reliaquest.api.exception.EmployeeServiceExecutionException;
import com.reliaquest.api.exception.InternalServerException;
import com.reliaquest.api.exception.NoDataToDisplayException;
import com.reliaquest.api.model.CreateMockEmployeeInput;
import com.reliaquest.api.model.DeleteMockEmployeeInput;
import com.reliaquest.api.model.MockEmployee;
import com.reliaquest.api.model.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    MockEmployeeClient mockEmployeeClient;

    @InjectMocks
    EmployeeService employeeService;

    @Mock
    private ObjectMapper objectMapper;

    @Test
    void testGetAllEmployees_success() {
        MockEmployee employee1 = new MockEmployee(
                UUID.randomUUID(), "Priya Biswas", 60000, 30, "Software Engineer", "priyab@example.com");
        MockEmployee employee2 = new MockEmployee(
                UUID.randomUUID(), "Vidya Sharma", 65000, 28, "Data Scientist", "vidyasharma@example.com");

        List<MockEmployee> mockEmployeeList = Arrays.asList(employee1, employee2);
        Response<List<MockEmployee>> mockResponse = new Response<>(mockEmployeeList, Response.Status.HANDLED, null);
        when(mockEmployeeClient.getEmployees()).thenReturn(mockResponse);

        List<MockEmployee> employees = employeeService.getAllEmployees();

        assertNotNull(employees);
        assertEquals(2, employees.size());
        assertEquals("Priya Biswas", employees.get(0).getName());
        assertEquals("Vidya Sharma", employees.get(1).getName());
    }

    @Test
    void testGetAllEmployees_noDataToDisplay() {
        Response<List<MockEmployee>> mockResponse = new Response<>(Collections.emptyList(), Response.Status.HANDLED, null);
        when(mockEmployeeClient.getEmployees()).thenReturn(mockResponse);

        NoDataToDisplayException exception = assertThrows(NoDataToDisplayException.class, () -> {
            employeeService.getAllEmployees();
        });

        assertEquals("No Employee to display", exception.getMessage());
    }

    @Test
    void testGetAllEmployees_getEmployeesThrowsException() {
        when(mockEmployeeClient.getEmployees()).thenThrow(new RuntimeException("Unexpected error"));

        EmployeeServiceExecutionException exception = assertThrows(EmployeeServiceExecutionException.class, () -> {
            employeeService.getAllEmployees();
        });

        assertEquals("Unexpected error", exception.getMessage());
    }

    @Test
    void testGetEmployeesByNameSearch_Success() throws Exception {
        MockEmployee employee1 = new MockEmployee(
                UUID.randomUUID(), "Priya Biswas", 60000, 30, "Software Engineer", "priyab@example.com");
        MockEmployee employee2 = new MockEmployee(
                UUID.randomUUID(), "Vidya Sharma", 65000, 28, "Data Scientist", "vidyasharma@example.com");

        List<MockEmployee> mockEmployeeList = Arrays.asList(employee1, employee2);
        Response<List<MockEmployee>> mockResponse = new Response<>(mockEmployeeList, Response.Status.HANDLED, null);
        when(mockEmployeeClient.getEmployees()).thenReturn(mockResponse);

        List<MockEmployee> result = employeeService.getEmployeesByNameSearch("Priya");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Priya Biswas", result.get(0).getName());
    }

    @Test
    void testGetEmployeesByNameSearch_NoMatchingEmployees() {
        MockEmployee employee1 = new MockEmployee(
                UUID.randomUUID(), "Priya Biswas", 60000, 30, "Software Engineer", "priyab@example.com");
        MockEmployee employee2 = new MockEmployee(
                UUID.randomUUID(), "Vidya Sharma", 65000, 28, "Data Scientist", "vidyasharma@example.com");

        List<MockEmployee> mockEmployeeList = Arrays.asList(employee1, employee2);
        Response<List<MockEmployee>> mockResponse = new Response<>(mockEmployeeList, Response.Status.HANDLED, null);
        when(mockEmployeeClient.getEmployees()).thenReturn(mockResponse);

        NoDataToDisplayException exception = assertThrows(
                NoDataToDisplayException.class,
                () -> employeeService.getEmployeesByNameSearch("David")
        );

        assertEquals("No Employee found whose name contains  'David'", exception.getMessage());
    }

    @Test
    void testGetEmployeesByNameSearch_InternalServerException() {
        when(mockEmployeeClient.getEmployees()).thenThrow(new InternalServerException("API Failure"));

        EmployeeServiceExecutionException exception = assertThrows(
                EmployeeServiceExecutionException.class,
                () -> employeeService.getEmployeesByNameSearch("Alice")
        );

        assertEquals("API Failure", exception.getMessage());
    }

    @Test
    void testGetEmployeeById_Success() {
        MockEmployee mockEmployee = new MockEmployee(
                UUID.randomUUID(), "Priya Biswas", 60000, 30, "Software Engineer", "priyab@example.com");
        UUID mockUuid = UUID.randomUUID();
        Response<MockEmployee> mockResponse = new Response<>(mockEmployee, Response.Status.HANDLED, null);
        ResponseEntity<Response<MockEmployee>> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
        when(mockEmployeeClient.getEmployee(mockUuid)).thenReturn(responseEntity);

        MockEmployee result = employeeService.getEmployeeById(mockUuid.toString());

        assertNotNull(result);
        assertEquals("Priya Biswas", result.getName());
    }

    @Test
    void testGetEmployeeById_EmployeeNotFound() {
        UUID mockUuid = UUID.randomUUID();
        ResponseEntity<Response<MockEmployee>> responseEntity = new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        when(mockEmployeeClient.getEmployee(mockUuid)).thenReturn(responseEntity);

        NoDataToDisplayException exception = assertThrows(
                NoDataToDisplayException.class,
                () -> employeeService.getEmployeeById(mockUuid.toString())
        );

        assertEquals("No employee with given id Exists", exception.getMessage());
    }

    @Test
    void testGetEmployeeById_NullDataField() {
        UUID mockUuid = UUID.randomUUID();
        Response<MockEmployee> mockResponse = new Response<>(null, Response.Status.HANDLED, null);
        ResponseEntity<Response<MockEmployee>> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
        when(mockEmployeeClient.getEmployee(mockUuid)).thenReturn(responseEntity);

        NoDataToDisplayException exception = assertThrows(
                NoDataToDisplayException.class,
                () -> employeeService.getEmployeeById(mockUuid.toString())
        );

        assertEquals("No employee with given id Exists", exception.getMessage());
    }

    @Test
    void testGetEmployeeById_ApiFailure() {
        UUID mockUuid = UUID.randomUUID();
        when(mockEmployeeClient.getEmployee(mockUuid)).thenThrow(new RuntimeException("API Failure"));

        EmployeeServiceExecutionException exception = assertThrows(
                EmployeeServiceExecutionException.class,
                () -> employeeService.getEmployeeById(mockUuid.toString())
        );

        assertEquals("API Failure", exception.getMessage());
    }

    @Test
    void testGetEmployeeById_InvalidUUIDFormat() {
        assertThrows(
                IllegalArgumentException.class,
                () -> employeeService.getEmployeeById("invalid-uuid")
        );
    }

    @Test
    void testGetHighestSalaryOfEmployees_Success() {
        MockEmployee employee1 = new MockEmployee(
                UUID.randomUUID(), "Priya Biswas", 60000, 30, "Software Engineer", "priyab@example.com");
        MockEmployee employee2 = new MockEmployee(
                UUID.randomUUID(), "Vidya Sharma", 65000, 28, "Data Scientist", "vidyasharma@example.com");
        MockEmployee employee3 = new MockEmployee(
                UUID.randomUUID(), "Amruta Patil", 68000, 25, "Analyst", "amruta@example.com");

        List<MockEmployee> employeeList = Arrays.asList(employee1, employee2, employee3);
        Response<List<MockEmployee>> mockResponse = new Response<>(employeeList, Response.Status.HANDLED, null);
        when(mockEmployeeClient.getEmployees()).thenReturn(mockResponse);

        Integer highestSalary = employeeService.getHighestSalaryOfEmployees();

        assertEquals(68000, highestSalary);
    }

    @Test
    void testGetHighestSalaryOfEmployees_EmptyList() {
        Response<List<MockEmployee>> mockResponse = new Response<>(Collections.emptyList(), Response.Status.HANDLED, null);
        when(mockEmployeeClient.getEmployees()).thenReturn(mockResponse);

        NoDataToDisplayException exception = assertThrows(
                NoDataToDisplayException.class,
                () -> employeeService.getHighestSalaryOfEmployees()
        );

        assertEquals("No employee exists ", exception.getMessage());
    }

    @Test
    void testGetHighestSalaryOfEmployees_NullSalaryValues() {
        MockEmployee employee1 = new MockEmployee(
                UUID.randomUUID(), "Priya Biswas", null, 30, "Software Engineer", "priyab@example.com");
        MockEmployee employee2 = new MockEmployee(
                UUID.randomUUID(), "Vidya Sharma", null, 28, "Data Scientist", "vidyasharma@example.com");
        MockEmployee employee3 = new MockEmployee(
                UUID.randomUUID(), "Amruta Patil", null, 25, "Analyst", "amruta@example.com");

        List<MockEmployee> employeeList = Arrays.asList(employee1, employee2, employee3);
        Response<List<MockEmployee>> mockResponse = new Response<>(employeeList, Response.Status.HANDLED, null);
        when(mockEmployeeClient.getEmployees()).thenReturn(mockResponse);

        EmployeeServiceExecutionException exception = assertThrows(
                EmployeeServiceExecutionException.class,
                () -> employeeService.getHighestSalaryOfEmployees()
        );
    }

    @Test
    void testGetHighestSalaryOfEmployees_ApiFailure() {
        when(mockEmployeeClient.getEmployees()).thenThrow(new EmployeeServiceExecutionException("API Failure"));

        EmployeeServiceExecutionException exception = assertThrows(
                EmployeeServiceExecutionException.class,
                () -> employeeService.getHighestSalaryOfEmployees()
        );

        assertEquals("API Failure", exception.getMessage());
    }

    @Test
    void testGetTopTenHighestEarningEmployeeNames_Success() {

        MockEmployee employee1 = new MockEmployee(
                UUID.randomUUID(), "Rajesh Verma", 61000, 35, "Project Manager", "rajesh.verma@example.com");
        MockEmployee employee2 = new MockEmployee(
                UUID.randomUUID(), "Sanya Iyer", 62000, 27, "Software Developer", "sanya.iyer@example.com");
        MockEmployee employee3 = new MockEmployee(
                UUID.randomUUID(), "Anil Kumar", 63000, 40, "Senior Architect", "anil.kumar@example.com");
        MockEmployee employee4 = new MockEmployee(
                UUID.randomUUID(), "Neha Agarwal", 64000, 32, "UX Designer", "neha.agarwal@example.com");
        MockEmployee employee5 = new MockEmployee(
                UUID.randomUUID(), "Vikram Singh", 65000, 29, "DevOps Engineer", "vikram.singh@example.com");
        MockEmployee employee6 = new MockEmployee(
                UUID.randomUUID(), "Pooja Nair", 66000, 26, "Cybersecurity Analyst", "pooja.nair@example.com");
        MockEmployee employee7 = new MockEmployee(
                UUID.randomUUID(), "Rohan Joshi", 67000, 31, "Database Administrator", "rohan.joshi@example.com");
        MockEmployee employee8 = new MockEmployee(
                UUID.randomUUID(), "Sneha Kulkarni", 68000, 34, "Cloud Engineer", "sneha.kulkarni@example.com");
        MockEmployee employee9 = new MockEmployee(
                UUID.randomUUID(), "Tarun Mehta", 69000, 28, "AI Engineer", "tarun.mehta@example.com");
        MockEmployee employee10 = new MockEmployee(
                UUID.randomUUID(), "Swati Choudhary", 70000, 30, "Product Manager", "swati.choudhary@example.com");
        MockEmployee employee11 = new MockEmployee(
                UUID.randomUUID(), "Arjun Deshmukh", 80000, 33, "QA Engineer", "arjun.deshmukh@example.com");

        List<MockEmployee> employeeList = Arrays.asList(employee1, employee2, employee3, employee4, employee5, employee6, employee7, employee8, employee9, employee10, employee11);
        Response<List<MockEmployee>> mockResponse = new Response<>(employeeList, Response.Status.HANDLED, null);
        when(mockEmployeeClient.getEmployees()).thenReturn(mockResponse);

        List<String> topEmployees = employeeService.getTopTenHighestEarningEmployeeNames();

        List<String> expectedNames = Arrays.asList("Arjun Deshmukh", "Swati Choudhary", "Tarun Mehta", "Sneha Kulkarni", "Rohan Joshi", "Pooja Nair", "Vikram Singh", "Neha Agarwal", "Anil Kumar", "Sanya Iyer");
        assertEquals(expectedNames, topEmployees);
        assertEquals(10, topEmployees.size());
    }

    @Test
    void testGetTopTenHighestEarningEmployeeNames_LessThanTenEmployees() {
        MockEmployee employee1 = new MockEmployee(
                UUID.randomUUID(), "Rajesh Verma", 61000, 35, "Project Manager", "rajesh.verma@example.com");
        MockEmployee employee2 = new MockEmployee(
                UUID.randomUUID(), "Sanya Iyer", 62000, 27, "Software Developer", "sanya.iyer@example.com");
        MockEmployee employee3 = new MockEmployee(
                UUID.randomUUID(), "Anil Kumar", 63000, 40, "Senior Architect", "anil.kumar@example.com");
        MockEmployee employee4 = new MockEmployee(
                UUID.randomUUID(), "Neha Agarwal", 64000, 32, "UX Designer", "neha.agarwal@example.com");
        MockEmployee employee5 = new MockEmployee(
                UUID.randomUUID(), "Vikram Singh", 65000, 29, "DevOps Engineer", "vikram.singh@example.com");

        List<MockEmployee> employeeList = Arrays.asList(employee1, employee2, employee3, employee4, employee5);
        Response<List<MockEmployee>> mockResponse = new Response<>(employeeList, Response.Status.HANDLED, null);
        when(mockEmployeeClient.getEmployees()).thenReturn(mockResponse);

        List<String> topEmployees = employeeService.getTopTenHighestEarningEmployeeNames();

        List<String> expectedNames = Arrays.asList("Vikram Singh", "Neha Agarwal", "Anil Kumar", "Sanya Iyer", "Rajesh Verma");
        assertEquals(expectedNames, topEmployees);
        assertEquals(5, topEmployees.size());
    }

    @Test
    void testGetTopTenHighestEarningEmployeeNames_EmptyList() {
        Response<List<MockEmployee>> mockResponse = new Response<>(Collections.emptyList(), Response.Status.HANDLED, null);
        when(mockEmployeeClient.getEmployees()).thenReturn(mockResponse);

        NoDataToDisplayException exception = assertThrows(
                NoDataToDisplayException.class,
                () -> employeeService.getTopTenHighestEarningEmployeeNames()
        );

        assertEquals("No employee exists ", exception.getMessage());
    }

    @Test
    void testGetTopTenHighestEarningEmployeeNames_ApiFailure() {
        when(mockEmployeeClient.getEmployees()).thenThrow(new RuntimeException("API Failure"));

        EmployeeServiceExecutionException exception = assertThrows(
                EmployeeServiceExecutionException.class,
                () -> employeeService.getTopTenHighestEarningEmployeeNames()
        );

        assertEquals("API Failure", exception.getMessage());
    }

    @Test
    void createEmployee_Success() {
        CreateMockEmployeeInput input = new CreateMockEmployeeInput("Shirish Bhole", 30000, 30, "Software Engineer");
        when(objectMapper.convertValue(any(), eq(CreateMockEmployeeInput.class))).thenReturn(input);

        MockEmployee mockEmployee = new MockEmployee(UUID.randomUUID(),"Rajesh Verma", 61000, 35, "Project Manager", "rajesh.verma@example.com");
        Response<MockEmployee> mockResponse = new Response<>(mockEmployee, Response.Status.HANDLED, null);
        when(mockEmployeeClient.createEmployee(any(CreateMockEmployeeInput.class))).thenReturn(mockResponse);

        MockEmployee result = employeeService.createEmployee(input);

        assertNotNull(result);
        assertEquals("Rajesh Verma", result.getName());
        verify(mockEmployeeClient, times(1)).createEmployee(any(CreateMockEmployeeInput.class));
    }

//    @Test
//    void createEmployee_InvalidInput_ThrowsException() {
//        EmployeeServiceExecutionException exception = assertThrows(EmployeeServiceExecutionException.class, () ->
//                employeeService.createEmployee("InvalidInput")
//        );
//        assertEquals("Invalid input type. Expected CreateMockEmployeeInput.", exception.getMessage());
//    }

    @Test
    void testCreateEmployee_NullInput_ShouldThrowException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> employeeService.createEmployee(null));
        assertEquals("Invalid input type. Expected CreateMockEmployeeInput.", exception.getMessage());
    }

    @Test
    void createEmployee_ApiFailure_ThrowsException() {
        CreateMockEmployeeInput input = new CreateMockEmployeeInput("Shirish Bhole", 30000, 30, "Software Engineer");

        MockEmployee mockEmployee = new MockEmployee(UUID.randomUUID(),"Rajesh Verma", 61000, 35, "Project Manager", "rajesh.verma@example.com");
        Response<MockEmployee> mockResponse = new Response<>(mockEmployee, Response.Status.HANDLED, null);
        when(objectMapper.convertValue(any(), eq(CreateMockEmployeeInput.class))).thenReturn(input);

        // Mock API failure
        when(mockEmployeeClient.createEmployee(any(CreateMockEmployeeInput.class)))
                .thenThrow(new RuntimeException("API Error"));

        // Act & Assert
        EmployeeServiceExecutionException exception = assertThrows(EmployeeServiceExecutionException.class, () ->
                employeeService.createEmployee(input)
        );
        assertEquals("API Error", exception.getMessage());
    }

    @Test
    void deleteEmployeeById_Success() {
        EmployeeService employeeServiceSpy = spy(employeeService);
        MockEmployee mockEmployee = new MockEmployee(UUID.randomUUID(),"Rajesh Verma", 61000, 35, "Project Manager", "rajesh.verma@example.com");
        doReturn(mockEmployee).when(employeeServiceSpy).getEmployeeById("59b9797a-d58a-4542-8ba1-812fa44c7654");

        Response<Boolean> mockResponse = new Response<>(true, Response.Status.HANDLED, null);
        when(mockEmployeeClient.deleteEmployee(any(DeleteMockEmployeeInput.class))).thenReturn(mockResponse);

        String result = employeeServiceSpy.deleteEmployeeById("59b9797a-d58a-4542-8ba1-812fa44c7654");

        assertEquals("Employee deleted successfully.", result);
        verify(mockEmployeeClient, times(1)).deleteEmployee(any(DeleteMockEmployeeInput.class));
    }

    @Test
    void deleteEmployeeById_NoEmployeeFound_ThrowsException() {

        EmployeeService employeeServiceSpy = spy(employeeService);
        doThrow(new NoDataToDisplayException("No employee found")).when(employeeServiceSpy).getEmployeeById("59b9797a-d58a-4542-8ba1-812fa44c7654");

        NoDataToDisplayException exception = assertThrows(NoDataToDisplayException.class, () ->
                employeeServiceSpy.deleteEmployeeById("59b9797a-d58a-4542-8ba1-812fa44c7654")
        );
        assertEquals("No employee found", exception.getMessage());
    }

    @Test
    void deleteEmployeeById_ApiFailure_ThrowsException() {
        MockEmployee mockEmployee = new MockEmployee(UUID.randomUUID(),"Rajesh Verma", 61000, 35, "Project Manager", "rajesh.verma@example.com");
        EmployeeService employeeServiceSpy = spy(employeeService);
        doReturn(mockEmployee).when(employeeServiceSpy).getEmployeeById("59b9797a-d58a-4542-8ba1-812fa44c7654");

        when(mockEmployeeClient.deleteEmployee(any(DeleteMockEmployeeInput.class)))
                .thenThrow(new RuntimeException("API Error"));

        EmployeeServiceExecutionException exception = assertThrows(EmployeeServiceExecutionException.class, () ->
                employeeServiceSpy.deleteEmployeeById("59b9797a-d58a-4542-8ba1-812fa44c7654")
        );
        assertEquals("API Error", exception.getMessage());
    }
}









