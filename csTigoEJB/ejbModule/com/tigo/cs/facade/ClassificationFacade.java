package com.tigo.cs.facade;

import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.tigo.cs.api.facade.ClassificationAPI;
import com.tigo.cs.api.interfaces.FacadeContainer;

@Stateless
@DependsOn("EJBFacadeContainer")
public class ClassificationFacade extends ClassificationAPI {

    @EJB
    protected FacadeContainer ussdFacadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return ussdFacadeContainer;
    }

    public ClassificationFacade() {
    }

}
