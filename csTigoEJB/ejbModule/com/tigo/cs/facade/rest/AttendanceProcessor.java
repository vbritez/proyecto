package com.tigo.cs.facade.rest;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.tigo.cs.api.entities.AttendanceService;
import com.tigo.cs.api.enumerate.ServiceIdentifier;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.api.service.AttendanceServiceAPI;

@Stateless
@ServiceIdentifier(id = ServiceIdentifier.Id.ASISTENCIA)
public class AttendanceProcessor extends AttendanceServiceAPI<AttendanceService> {

    @EJB
    private FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    @Override
    protected void assignEvent() {
    }

    @Override
    public void convertToBean() {
    }
}
