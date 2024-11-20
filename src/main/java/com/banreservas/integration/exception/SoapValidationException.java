package com.banreservas.integration.exception;

public class SoapValidationException extends RuntimeException {
    private final String errorCode;
    private final String namespace;
    private final int statusCode;

    public SoapValidationException(String message, String errorCode, String namespace, int statusCode) {
        super(message);
        this.errorCode = errorCode;
        this.namespace = namespace;
        this.statusCode = statusCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getNamespace() {
        return namespace;
    }

    public int getStatusCode() {
        return statusCode;
    }
}