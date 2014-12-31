package com.tigo.cs.facade.rest;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.tigo.cs.api.entities.TrackingService;
import com.tigo.cs.api.enumerate.ServiceIdentifier;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.api.service.TrackingServiceAPI;

@Stateless
@ServiceIdentifier(id = ServiceIdentifier.Id.RASTREO)
public class TrackingProcessor extends TrackingServiceAPI<TrackingService> {

    @EJB
    private FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    @Override
    public String execute(TrackingService entity) {
        entity.setArrivedEventDate(new Date().getTime());
        getFacadeContainer().getTrackQueueAPI().sendToJMS(entity);
        setReturnEntity(entity);
        return null;
    }

    @Override
    protected void assignEvent() {
    }

    @Override
    public void convertToBean() {
    }

}
