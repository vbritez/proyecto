package com.tigo.cs.api.exception;

public class MalformedSMSException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -2377748315002159391L;

    public MalformedSMSException(String message, Throwable cause) {
        super(message, cause);
    }
}
