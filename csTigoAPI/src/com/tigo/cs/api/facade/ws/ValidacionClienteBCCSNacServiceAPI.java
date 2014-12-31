package com.tigo.cs.api.facade.ws;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;

import py.com.lothar.validacion.ws.ValidacionClienteWSInterface;
import py.com.lothar.validacion.ws.ValidacionClienteWSService;

import com.tigo.cs.commons.jpa.GenericFacadeException;

public class ValidacionClienteBCCSNacServiceAPI extends AbstractWSClientServiceAPI<ValidacionClienteWSInterface> {

    private static final long serialVersionUID = -437472457922990931L;

    public ValidacionClienteBCCSNacServiceAPI() {
        super(ValidacionClienteWSInterface.class);
    }

    @Override
    protected int retryCount() {
        return 1;
    }

    @Override
    protected URL getWSDL() {
        try {
            return new URL(getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "validacionclientebccsnac.ws.wsdl"));
        } catch (GenericFacadeException e) {
        } catch (MalformedURLException e) {
        }
        return null;
    }
    
    protected String getStringWSDL() {
        try {
			return getFacadeContainer().getGlobalParameterAPI().findByCode(
			        "validacionclientebccsnac.ws.wsdl");
		} catch (GenericFacadeException e) {
			return null;
		}
    }

    @Override
    protected String getPlatformName() {
        return "VALIDACION-CLIENTEBCCS-NAC";
    }

    @Override
    protected int getTimeout() {
        try {
            return Integer.parseInt(getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "validacionclientebccsnac.ws.timeout"));
        } catch (GenericFacadeException e) {
        }
        return 100;
    }

    @Override
    protected QName getQname() {
        return new QName("http://ws.validacion.lothar.com.py/", "ValidacionClienteWSService");
    }

    @Override
    protected ValidacionClienteWSInterface createClient() {
        return new ValidacionClienteWSService(getWSDL(), getQname()).getValidacionClienteWSPort();
    }
}
