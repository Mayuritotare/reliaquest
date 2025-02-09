package com.reliaquest.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.config.MockEmployeeClient;
import com.reliaquest.api.exception.EmployeeServiceExecutionException;
import com.reliaquest.api.exception.NoDataToDisplayException;
import com.reliaquest.api.model.CreateMockEmployeeInput;
import com.reliaquest.api.model.DeleteMockEmployeeInput;
import com.reliaquest.api.model.MockEmployee;
import com.reliaquest.api.model.Response;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    MockEmployeeClient mockEmployeeClient;


    private ObjectMapper objectMapper;

    public EmployeeServiceImpl() {
        this.objectMapper = new ObjectMapper();
    }
    @Retry(name = "mockEmployeeApiRetry", fallbackMethod = "getAllEmployeesFallback")
    @CircuitBreaker(name = "mockEmployeeApiCircuitBreaker")
    public List<MockEmployee> getAllEmployees() {
        try {
            log.info("Fetching all employees by calling getAllEmployees from feignClient");
            Response<List<MockEmployee>> employees = mockEmployeeClient.getEmployees();
            if (employees == null || employees.data() == null || employees.data().isEmpty()) {
                log.info("No employees found");
                throw new NoDataToDisplayException("No Employee to display");
            }
            return employees.data();
        } catch (NoDataToDisplayException e) {
            log.error("NoDataToDisplayException exception occurred while fetching all employees from feignClient {}", e.getMessage());
            throw new NoDataToDisplayException(e.getMessage());
        } catch (Exception e) {
            log.error("Exception occurred while fetching all employees from feignClient {}", e.getMessage());
            throw new EmployeeServiceExecutionException(e.getMessage());
        }

    }

    @Retry(name = "mockEmployeeApiRetry", fallbackMethod = "getEmployeesByNameSearchFallbackResponse")
    @CircuitBreaker(name = "mockEmployeeApiCircuitBreaker")
    public List<MockEmployee> getEmployeesByNameSearch(String searchString) {
        try {
            log.info("Fetching all employees by calling getAllEmployees ");
            Response<List<MockEmployee>> employees = mockEmployeeClient.getEmployees();
            List<MockEmployee> collect = employees.data().stream().
                    filter(employee -> employee.getName().contains(searchString)).
                    collect(Collectors.toList());
            log.info("Fetched all employees based on their names {}", collect);

            if (collect.isEmpty()) {
                log.info("No Employee found whose name contains  '" + searchString + "'");
                throw new NoDataToDisplayException("No Employee found whose name contains  '" + searchString + "'");
            }
            return collect;
        } catch (NoDataToDisplayException e) {
            log.error("Exception occurred while searching employees based on their names {}", e.getMessage());
            throw new NoDataToDisplayException(e.getMessage());
        } catch (Exception e) {
            log.error("Unknown exception occurred while searching employees based on their names {}", e.getMessage());
            throw new EmployeeServiceExecutionException(e.getMessage());
        }
    }

    @Retry(name = "mockEmployeeApiRetry", fallbackMethod = "getEmployeeByIdFallback")
    @CircuitBreaker(name = "mockEmployeeApiCircuitBreaker")
    public MockEmployee getEmployeeById(String id) {
        UUID uuid = UUID.fromString(id);
        try {
            log.info("Fetching  employee by Id by calling getEmployee from feignClient");
            ResponseEntity<Response<MockEmployee>> employeResponseEntity = mockEmployeeClient.getEmployee(uuid);
            log.info("Response received is {}", employeResponseEntity);
            if (employeResponseEntity.getStatusCode() == HttpStatus.NOT_FOUND || employeResponseEntity.getBody().data() == null) {
                log.info("No employee with given id {} ", uuid + " Exists");
                throw new NoDataToDisplayException("No employee with given id Exists");
            }
            return employeResponseEntity.getBody().data();
        } catch (NoDataToDisplayException e) {
            log.error("Exception occurred while searching employee by id {}", e.getMessage());
            throw new NoDataToDisplayException(e.getMessage());
        } catch (Exception e) {
            log.error("Unknown exception occurred while searching employee by id {} ", e.getMessage());
            throw new EmployeeServiceExecutionException(e.getMessage());
        }
    }

    @Retry(name = "mockEmployeeApiRetry", fallbackMethod = "getHighestSalaryOfEmployeesFallback")
    @CircuitBreaker(name = "mockEmployeeApiCircuitBreaker")
    public Integer getHighestSalaryOfEmployees() {
        try {
            log.info("Fetching highest salary of employees from feignClient");
            Response<List<MockEmployee>> employees = mockEmployeeClient.getEmployees();
            Integer highestSalary = employees.data().stream().max(Comparator.comparingInt(MockEmployee::getSalary)).map(MockEmployee::getSalary).orElse(0);
            log.info("Fetched highest salary of employee {}", highestSalary);
            if (highestSalary == 0) {
                log.error("Exception occurred while fetching highest salary of employees");
                throw new NoDataToDisplayException("No employee exists ");
            }
            return highestSalary;
        } catch (NoDataToDisplayException e) {
            log.error("NoDataToDisplayException Exception occurred while fetching highest salary of employees {}", e.getMessage());
            throw new NoDataToDisplayException(e.getMessage());
        } catch (Exception e) {
            log.error("Unknown exception occurred while searching highest salary of employees {}", e.getMessage());
            throw new EmployeeServiceExecutionException(e.getMessage());
        }
    }

    @Retry(name = "mockEmployeeApiRetry", fallbackMethod = "getTopTenHighestEarningEmployeeNamesFallback")
    @CircuitBreaker(name = "mockEmployeeApiCircuitBreaker")
    public List<String> getTopTenHighestEarningEmployeeNames() {
        try {
            log.info("Fetching top ten highest earning employees from feignClient");
            Response<List<MockEmployee>> employees = mockEmployeeClient.getEmployees();
            List<String> topTenHighestEarningEmployeeNames = employees.data().stream().sorted(Comparator.comparingInt(MockEmployee::getSalary).reversed()).limit(10).map(MockEmployee::getName).collect(Collectors.toList());
            log.info("Top ten highest earning employees are  {}", topTenHighestEarningEmployeeNames);
            if (topTenHighestEarningEmployeeNames.isEmpty() || topTenHighestEarningEmployeeNames.size() == 0) {
                log.error("No employee found");
                throw new NoDataToDisplayException("No employee exists ");
            }
            return topTenHighestEarningEmployeeNames;
        } catch (NoDataToDisplayException e) {
            log.error("NoDataToDisplayException exception occurred when fetching top ten highest earning employees {}", e.getMessage());
            throw new NoDataToDisplayException(e.getMessage());
        } catch (Exception e) {
            log.error("Unknown exception occurred when fetching top ten highest earning employees {}", e.getMessage());
            throw new EmployeeServiceExecutionException(e.getMessage());
        }
    }

    @Retry(name = "mockEmployeeApiRetry", fallbackMethod = "createEmployeeFallback")
    @CircuitBreaker(name = "mockEmployeeApiCircuitBreaker")
    public MockEmployee createEmployee(Object employeeInput) {
        try {
            log.info("Creating employee from feignClient");
            if (employeeInput == null) {
                log.error("Input is null");
                throw new IllegalArgumentException("Invalid input type. Expected CreateMockEmployeeInput.");
            }
            Response<MockEmployee> response = mockEmployeeClient.createEmployee(objectMapper.convertValue(employeeInput, CreateMockEmployeeInput.class));

            return response.data();
        } catch (IllegalArgumentException e) {
            log.error("IllegalArgumentException occurred while creating employee {}", e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
        catch (Exception e) {
            log.error("Exception occurred while creating employee {}", e.getMessage());
            throw new EmployeeServiceExecutionException(e.getMessage());
        }
    }

    @Retry(name = "mockEmployeeApiRetry", fallbackMethod = "deleteEmployeeFallback")
    @CircuitBreaker(name = "mockEmployeeApiCircuitBreaker")
    public String deleteEmployeeById(String id) {
        log.info("Deleting employee from with id {}", id);
        var name = getEmployeeById(id).getName();
        log.info("Fetched name of the employee corresponding to given id  {}", name);
        DeleteMockEmployeeInput input = new DeleteMockEmployeeInput(name);
        try {
            Response<Boolean> response = mockEmployeeClient.deleteEmployee(input);
            log.info("Response from downstream is  {}", response);
            if (response.data() != null && response.data()) {
                log.info("Successfully deleted employee {}", name);
                return "Employee deleted successfully.";
            } else {
                log.info("Failed to delete employee {}", name);
                throw new NoDataToDisplayException("No employee with given id Exists");
            }
        } catch (NoDataToDisplayException e) {
            log.error("Exception occurred while deleting employee {}", e.getMessage());
            throw new NoDataToDisplayException(e.getMessage());
        } catch (Exception e) {
            log.error("Unknown exception occurred while deleting employee {}", e.getMessage());
            throw new EmployeeServiceExecutionException(e.getMessage());
        }
    }

    public List<MockEmployee> getEmployeesByNameSearchFallbackResponse(String str, Throwable ex) {
        System.out.println(ex.getMessage());
        log.warn("Fallback method executed");
        return new ArrayList<MockEmployee>();
    }

    public List<MockEmployee> getAllEmployeesFallback(Exception e) {
        log.warn("Fallback method triggered due to: {}", e.getMessage());
        return Collections.emptyList();
    }

    public MockEmployee getEmployeeByIdFallback(String id, Exception e) {
        log.error("Fallback method triggered for getEmployeeById due to: {}", e.getMessage());
        return new MockEmployee();
    }

    public Integer getHighestSalaryOfEmployeesFallback(Exception e) {
        log.error("Fallback method triggered for getHighestSalaryOfEmployees due to: {}", e.getMessage());
        return 0;
    }

    public List<String> getTopTenHighestEarningEmployeeNamesFallback(Exception e) {
        log.error("Fallback method triggered for getTopTenHighestEarningEmployeeNames: {}", e.getMessage());
        return Collections.emptyList();
    }

    public MockEmployee fallbackCreateEmployee(Object employeeInput, Throwable t) {
        log.error("Fallback method triggered due to: {}", t.getMessage());
        return new MockEmployee();
    }

    public String deleteEmployeeFallback(String id, Throwable throwable) {
        log.error("Fallback triggered for deleteEmployeeById. Error: {}", throwable.getMessage());
        return "Employee deletion failed. Please try again later.";
    }

}

