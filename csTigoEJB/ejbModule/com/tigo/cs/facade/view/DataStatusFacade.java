package com.tigo.cs.facade.view;

import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.tigo.cs.api.facade.DataStatusAPI;
import com.tigo.cs.api.interfaces.FacadeContainer;

/**
 *
 * @author Miguel Zorrilla
 */
@DependsOn("EJBFacadeContainer")
@Stateless
public class DataStatusFacade extends DataStatusAPI {

    public DataStatusFacade() {
    }

    @EJB
    protected FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }
}
