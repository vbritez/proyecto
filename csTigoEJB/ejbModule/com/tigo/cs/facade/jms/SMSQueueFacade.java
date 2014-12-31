package com.tigo.cs.facade.jms;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.tigo.cs.api.facade.jms.SMSQueueAPI;
import com.tigo.cs.api.interfaces.FacadeContainer;

@Stateless
public class SMSQueueFacade extends SMSQueueAPI {

    private static final long serialVersionUID = -959753612706184047L;

    @EJB
    protected FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }
}
