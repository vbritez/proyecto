package com.tigo.cs.facade;

import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.tigo.cs.api.facade.ClientGoalAPI;
import com.tigo.cs.api.interfaces.FacadeContainer;

@Stateless
@DependsOn("EJBFacadeContainer")
public class ClientGoalFacade extends ClientGoalAPI {
    
    @EJB
    protected FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }
    
	public ClientGoalFacade() {		
	}
	
	
}