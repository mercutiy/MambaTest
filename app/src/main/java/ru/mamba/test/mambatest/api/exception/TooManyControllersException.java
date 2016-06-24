package ru.mamba.test.mambatest.api.exception;

public class TooManyControllersException extends Exception {

    public TooManyControllersException(String detailMessage) {
        super(detailMessage);
    }
}
