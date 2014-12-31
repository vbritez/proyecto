package com.tigo.cs.security;

import javax.ejb.EJB;
import javax.ejb.Singleton;

import com.tigo.cs.api.facade.NotifierAPI;
import com.tigo.cs.api.interfaces.FacadeContainer;

@Singleton
public class Notifier extends NotifierAPI {
    @EJB
    private FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    public Notifier() {
    }
}
