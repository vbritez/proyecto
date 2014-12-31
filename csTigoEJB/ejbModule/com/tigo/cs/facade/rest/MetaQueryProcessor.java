package com.tigo.cs.facade.rest;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.tigo.cs.api.entities.MetadataCrudService;
import com.tigo.cs.api.enumerate.ServiceIdentifier;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.api.service.MetadataCrudServiceAPI;

@Stateless
@ServiceIdentifier(id = ServiceIdentifier.Id.DATOS)
public class MetaQueryProcessor extends MetadataCrudServiceAPI<MetadataCrudService> {

    @EJB
    private FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    @Override
    protected void assignEvent() {

        String discriminator = getEntity().getEvent();
        if (discriminator.equals(MetaCrudEvent.CREATE.getDescription())) {
            getReturnEntity().setEvent(MetaCrudEvent.CREATE.getDescription());
            tranType = MetaCrudEvent.CREATE;
        } else if (discriminator.equals(MetaCrudEvent.UPDATE.getDescription())) {
            tranType = MetaCrudEvent.UPDATE;
        } else if (discriminator.equals(MetaCrudEvent.DELETE.getDescription())) {
            tranType = MetaCrudEvent.DELETE;
        } else if (discriminator.equals(MetaCrudEvent.READ.getDescription())) {
            tranType = MetaCrudEvent.READ;
            discriminator = getEntity().getCode();
            if (discriminator != null && discriminator.equals("C")) {
                getEntity().setEvent("C");
            } else if (discriminator.equals("N")) {
                getEntity().setEvent("N");
            }
        } else if (discriminator.equals(MetaCrudEvent.GEOLOCATION.toString())) {
            getReturnEntity().setEvent(MetaCrudEvent.GEOLOCATION.toString());
            tranType = MetaCrudEvent.GEOLOCATION;
        }

    }

    @Override
    public void convertToBean() {
    }
}
