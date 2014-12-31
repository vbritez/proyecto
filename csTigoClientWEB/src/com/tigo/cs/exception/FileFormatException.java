package com.tigo.cs.exception;

/**
 *
 * @author Miguel Zorrilla
 * @version $Id$
 */
public class FileFormatException extends Exception {

    /**
     * Constructs an instance of <code>FileFormatException</code> with the specified detail message.
     * @param msg the detail message.
     * @param t the exception cause
     */
    public FileFormatException(String msg, Throwable t) {
        super(msg, t);
    }

    /**
     * Constructs an instance of <code>FileFormatException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public FileFormatException(String msg) {
        super(msg);
    }
}
