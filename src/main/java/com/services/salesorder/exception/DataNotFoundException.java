package com.services.salesorder.exception;

/**
 * Gunakan exception ini jika data tidak tersedia di database.
 *
 * @author Hariyogi
 * @since 2 Sep 2020
 */
public class DataNotFoundException extends ServiceBaseException {
    private static final long serialVersionUID = 5558238161985653783L;

    public DataNotFoundException(ExceptionCode code) {
        super(code);
    }

    public DataNotFoundException(ExceptionCode code, String parameter) {
        super(code, parameter);
    }

}
