package com.tigo.cs.api.facade.ws;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.xmlns.account.service.retrievepolicebaseinfoservice.v1.RetrievePoliceBaseInfoService;
import com.tigo.xmlns.account.service.retrievepolicebaseinfoservice.v1.RetrievePoliceBaseInfoServiceEp;

public class RetrievePoliceBaseInfoAPI extends AbstractWSClientServiceAPI<RetrievePoliceBaseInfoService> {

    /**
     * 
     */
    private static final long serialVersionUID = 2373212705161732568L;

    public RetrievePoliceBaseInfoAPI() {
        super(RetrievePoliceBaseInfoService.class);
    }

    @Override
    protected int retryCount() {
        return 1;
    }

    @Override
    protected URL getWSDL() {
        try {
            return new URL(getStringWSDL());
        } catch (MalformedURLException e) {
            getFacadeContainer().getNotifier().error(getClass(), null,
                    e.getMessage(), e);
        }
        return null;
    }

    @Override
    protected String getStringWSDL() {
        try {
            return getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "retrieve.police.baseinfo.ws.wsdl");
        } catch (GenericFacadeException e) {
            getFacadeContainer().getNotifier().error(getClass(), null,
                    e.getMessage(), e);
            return null;
        }
    }

    @Override
    protected String getPlatformName() {
        return "RETRIEVE-POLICE-BASEINFO";
    }

    @Override
    protected int getTimeout() {
        try {
            return Integer.parseInt(getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "retrieve.police.baseinfo.ws.timeout"));
        } catch (GenericFacadeException e) {
            getFacadeContainer().getNotifier().error(getClass(), null,
                    e.getMessage(), e);
        }
        return 100;
    }

    @Override
    protected QName getQname() {
        return null;
    }

    @Override
    protected RetrievePoliceBaseInfoService createClient() {
        return new RetrievePoliceBaseInfoServiceEp(getWSDL()).getRetrievePoliceBaseInfoServicePt();
    }

}
