package com.tigo.cs.api.exception;

import com.tigo.cs.api.enumerate.Restriction;

public class OperationNotAllowedException extends RuntimeException {

    private static final long serialVersionUID = -1629809167004891104L;

    public OperationNotAllowedException(Restriction or) {
        super(or.getMessage());
    }

    public OperationNotAllowedException(String restrictionMessage) {
        super(restrictionMessage);
    }
}
