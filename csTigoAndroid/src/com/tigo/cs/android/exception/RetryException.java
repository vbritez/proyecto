package com.tigo.cs.android.exception;

public class RetryException extends Exception {

    private static final long serialVersionUID = -3299920546268676316L;

    public RetryException(String message) {
        super(message);
    }

}
