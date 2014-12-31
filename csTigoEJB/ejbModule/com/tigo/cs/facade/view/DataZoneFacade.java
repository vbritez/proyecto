package com.tigo.cs.facade.view;

import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.tigo.cs.api.facade.DataZoneAPI;
import com.tigo.cs.api.interfaces.FacadeContainer;

/**
 *
 * @author Tania Nuñez
 */
@DependsOn("EJBFacadeContainer")
@Stateless
public class DataZoneFacade extends DataZoneAPI{

    public DataZoneFacade() {
    }

    @EJB
    protected FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }
}
