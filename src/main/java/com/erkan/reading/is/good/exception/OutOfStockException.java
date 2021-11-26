package com.erkan.reading.is.good.exception;

public class OutOfStockException extends BusinessException {
    private static final String errorCode = "000003";

    public OutOfStockException(String errorMessage) {
        super(errorCode, errorMessage);
    }
}
