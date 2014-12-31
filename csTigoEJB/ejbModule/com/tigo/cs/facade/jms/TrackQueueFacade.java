package com.tigo.cs.facade.jms;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.tigo.cs.api.facade.jms.TrackQueueAPI;
import com.tigo.cs.api.interfaces.FacadeContainer;

@Stateless
public class TrackQueueFacade extends TrackQueueAPI {

    private static final long serialVersionUID = -1531881729171113989L;
    @EJB
    protected FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

}
