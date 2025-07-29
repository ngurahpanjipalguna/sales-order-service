package com.services.salesorder.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.io.Serial;
import java.util.Set;
import java.util.stream.Collectors;

public class FormatException extends ServiceBaseException {

    @Serial
    private static final long serialVersionUID = 8990164132805617586L;

    public static <C> void validateObject(C object, Validator validator) {
        var cve = validator.validate(object);
        if(!cve.isEmpty()) {
            throw  fromConstraintViolationException(cve);
        }
    }

    public static <T> FormatException fromConstraintViolationException(Set<ConstraintViolation<T>> cve) {
        var violation = cve.stream()
                .map(c -> {
                    String field = null;
                    for(var node : c.getPropertyPath()) {
                        field = node.getName();
                    }
                    var message = c.getMessage();
                    return field + " " + message;
                })
                .collect(Collectors.joining(","));
        return new FormatException(ExceptionCode.F_NV, violation);
    }

    public FormatException(String message) {
        super(ExceptionCode.F_NV, message);
    }

    public FormatException(ExceptionCode code, String message) {
        super(code, message);
    }

    public FormatException(ExceptionCode code) {
        super(code);
    }
}
