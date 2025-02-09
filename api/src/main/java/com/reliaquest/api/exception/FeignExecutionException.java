package com.reliaquest.api.exception;

import feign.FeignException;

public class FeignExecutionException extends FeignException {
    public FeignExecutionException(int status, String message) {
        super(status, message);
    }
}
