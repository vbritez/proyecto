package com.tigo.cs.ws.facade;

/**
 * 
 * @author Miguel Zorrilla
 */
public class AuthenticationException extends Exception {

    private static final long serialVersionUID = -7801311286424855731L;

    /**
     * Constructs an instance of <code>AuthenticationException</code> with the
     * specified detail message.
     * 
     * @param msg
     *            the detail message.
     */
    public AuthenticationException(String msg) {
        super(msg);
    }
}
