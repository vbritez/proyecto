package com.tigo.cs.android.service;

public interface ServiceResponseEvent {

    public String getEvent();

    public Integer getTitle();

    public Integer getErrorMessage();

    public Integer getSuccessMessage();
}
