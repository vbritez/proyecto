package com.tigo.cs.facade.ussddriver;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import py.com.lothar.ussddriverinterfaces.driver.DriverInterface;

import com.tigo.cs.api.entities.OtService;
import com.tigo.cs.api.enumerate.ServiceIdentifier;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.api.service.OtServiceAPI;


@Stateless
@ServiceIdentifier(id = ServiceIdentifier.Id.OT)
public class OtDriver extends OtServiceAPI<OtService> implements DriverInterface {

    public static final String SC_OT = "SC-OT";
    public static final String OT_ACTUAL = "OT-ACTUAL";
    public static final String ACTIVIDAD_ACTUAL = "ACTIVIDAD_ACTUAL";
    public static final String CONSULTAS = "CONSULTAS";
    public static final String OT_INICIO = "OT-INICIO";
    public static final String OT_SUSPENDER = "OT-SUSPENDER";
    public static final String OT_POSTERGAR = "OT-POSTERGAR";
    public static final String OT_TERMINAR = "OT-TERMINAR";
    public static final String OT_CONSULTA_ACTUAL = "OT-CONSULTA-ACTUAL";
    public static final String OT_CONSULTA_DIA = "OT-CONSULTA-DIA";
    public static final String OBSERVACION = "OBSERVACION";

    @EJB
    private FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    @Override
    public void convertToBean() {
        getEntity().setObservation(getNotNullNodeValueFrom(OBSERVACION));
    }

    @Override
    protected void assignEvent() {
    	String event = getNotNullNodeValueFrom(SC_OT);
    	if (event.equalsIgnoreCase(OT_ACTUAL))
    		getEntity().setEvent(getNotNullNodeValueFrom(OT_ACTUAL));  
    	else
    		getEntity().setEvent(getNotNullNodeValueFrom(CONSULTAS)); 
    }

}
