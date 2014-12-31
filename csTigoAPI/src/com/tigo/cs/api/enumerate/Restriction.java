package com.tigo.cs.api.enumerate;

public enum Restriction {

    GENERIC(0, "restriction.GenericError"),
    PHONE_NOT_REGISTERED(1, "restriction.PhoneNotRegistered"),
    PHONE_WITH_SERVICE_NOT_REGISTERED(2, "restriction.PhoneWithServiceNotRegistered"),
    PHONE_WITH_SERVICE_NOT_ALLOWED(3, "restriction.PhoneWithServiceNotAllowed"),
    CLIENT_WITH_SERVICE_NOT_ALLOWED(4, "restriction.ClientWithServiceNotAllowed"),
    SERVICE_DOES_NOT_EXIST(5, "restriction.ServiceDoesNotExist"),
    GUARD_UNTIMELY_MARKING(6, "restriction.MarkingOutOfTime"),
    GUARD_RE_MARKING_NOT_ALLOWEB(7, "restriction.MarkingAlreadyExist"),
    ATTENDANCE_NOT_FINISHED(8, "restriction.AttendanceNotFinished"),
    CLIENT_WITH_META_NOT_ALLOWED(9, "restriction.metadata.NotEnabledMeta"),
    PHONE_WITH_META_NOT_ALLOWED(10, "restriction.PhoneWithMetaNotAllowed"),
    META_DOES_NOT_EXIST(11, "restriction.MetaDoesNotExist");

    private Restriction(int value, String message) {
        this.value = value;
        this.message = message;
    }

    protected final int value;
    protected final String message;

    public int getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }
}
