package com.tigo.cs.api.util;

import com.tigo.cs.api.enumerate.CommonTransaction;


public class ServiceProps {

    private String description;
    private String succesMessage;
    private String noMatchMessage;

    public ServiceProps(CommonTransaction ct) {
        this(ct.description(), ct.succesMessage(), ct.noMatchMessage());
    }

    public ServiceProps(String description, String succesMessage,
            String noMatchMessage) {
        this.description = description;
        this.succesMessage = succesMessage;
        this.noMatchMessage = noMatchMessage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNoMatchMessage() {
        return noMatchMessage;
    }

    public void setNoMatchMessage(String noMatchMessage) {
        this.noMatchMessage = noMatchMessage;
    }

    public String getSuccesMessage() {
        return succesMessage;
    }

    public void setSuccesMessage(String succesMessage) {
        this.succesMessage = succesMessage;
    }
}
