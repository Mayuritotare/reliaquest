package com.reliaquest.api.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmployeeServiceExecutionException.class)
    public ResponseEntity<Object> EmployeeServiceExecutionException(EmployeeServiceExecutionException e) {
        ApiError er = new ApiError();
        log.info("Inside EmployeeServiceExecutionException");
        er.setCode(500);
        er.setMessage(e.getMessage());
        er.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(er, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoDataToDisplayException.class)
    public ResponseEntity<Object> NoDataToDisplayException(NoDataToDisplayException e) {
        ApiError er = new ApiError();
        log.info("Inside NoDataToDisplayException");
        er.setCode(404);
        er.setMessage(e.getMessage());
        er.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(er, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<Object> InternalServerException(InternalServerException e) {
        ApiError er = new ApiError();
        log.info("Inside InternalServerException");
        er.setCode(550);
        er.setMessage(e.getMessage());
        er.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(er, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}

