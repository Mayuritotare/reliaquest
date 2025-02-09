package com.reliaquest.api.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class CustomFeignErrorDecoder implements ErrorDecoder {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ErrorDecoder defaultErrorDecoder = new ErrorDecoder.Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        log.info("In decode");

        int status = response.status();
        log.info("In decode status {}", status);
        Map<String, Object> errorDetails;
        try {
            String errorBody = feign.Util.toString(response.body().asReader());

            errorDetails = objectMapper.readValue(errorBody, Map.class);
        } catch (Exception e) {
            return new Exception("Generic error");

        }

        switch (status) {
            case 404:
                return new NoDataToDisplayException("Resource not found");
            case 400:
                return new FeignExecutionException(status, "Bad Request");
            case 405:
                return new FeignExecutionException(status, "Method Not Allowed");
            case 500:
                return new InternalServerException(errorDetails.get("message").toString());

            default:
                return defaultErrorDecoder.decode(methodKey, response);
        }
    }
}
