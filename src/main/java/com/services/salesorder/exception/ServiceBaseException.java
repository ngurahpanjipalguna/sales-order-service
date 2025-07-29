package com.services.salesorder.exception;

import lombok.Getter;

public class ServiceBaseException extends RuntimeException {
    @Getter
    private final ExceptionCode code;
    private final String message;

    public ServiceBaseException(ExceptionCode code, String message) {
        this.code = code;
        this.message = message;
    }

    public ServiceBaseException(ExceptionCode code) {
        this.code = code;
        this.message = code.getMessage();
    }

    @Override
    public String getMessage() {
        return code + " " + message;
    }
}
