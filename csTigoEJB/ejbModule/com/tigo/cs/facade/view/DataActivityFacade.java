package com.tigo.cs.facade.view;

import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.tigo.cs.api.facade.DataActivityAPI;
import com.tigo.cs.api.interfaces.FacadeContainer;

/**
 * 
 * @author Miguel Zorrilla
 */
@DependsOn("EJBFacadeContainer")
@Stateless
public class DataActivityFacade extends DataActivityAPI {

    public DataActivityFacade() {
    }

    @EJB
    protected FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }
}
