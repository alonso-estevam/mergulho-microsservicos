package com.algaworks.algadelivery.delivery.tracking.domain.exception;

public class BusinessException extends RuntimeException {

    public BusinessException() {
    }

    public BusinessException(String message) {

        super(message);
    }

    public BusinessException(Throwable cause) {

        super(cause);
    }

}
