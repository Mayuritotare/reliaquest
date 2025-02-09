package com.reliaquest.api.config;

import com.reliaquest.api.exception.FeignClientConfig;
import com.reliaquest.api.model.CreateMockEmployeeInput;
import com.reliaquest.api.model.DeleteMockEmployeeInput;
import com.reliaquest.api.model.MockEmployee;
import com.reliaquest.api.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@FeignClient(name = "mock-employee-api", url = "${employeeService.url}" , configuration = FeignClientConfig.class)
public interface MockEmployeeClient {

    @GetMapping
    Response<List<MockEmployee>> getEmployees();

    @GetMapping("/{id}")
    ResponseEntity<Response<MockEmployee>> getEmployee(@PathVariable("id") UUID uuid);

    @PostMapping
    Response<MockEmployee> createEmployee( @RequestBody CreateMockEmployeeInput input);

    @DeleteMapping()
    Response<Boolean> deleteEmployee(@RequestBody DeleteMockEmployeeInput input);

}
