package ru.mamba.test.mambatest.api.exception;

public class ErrorCodeException extends Exception {

    public final static int EC_USER_NOT_EXISTS = 4;

    private int errorCode;

    public ErrorCodeException(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
