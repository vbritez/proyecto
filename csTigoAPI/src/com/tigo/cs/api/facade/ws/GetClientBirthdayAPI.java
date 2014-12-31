package com.tigo.cs.api.facade.ws;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.xmlns.mobile.service.getclientbirthday.v1.GetClientBirthday;
import com.tigo.xmlns.mobile.service.getclientbirthday.v1.GetClientBirthdayEp;

public class GetClientBirthdayAPI extends AbstractWSClientServiceAPI<GetClientBirthday> {

    private static final long serialVersionUID = -1981102784239399303L;

    public GetClientBirthdayAPI() {
        super(GetClientBirthday.class);
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
                    "getclient.birthday.ws.wsdl");
        } catch (GenericFacadeException e) {
            getFacadeContainer().getNotifier().error(getClass(), null,
                    e.getMessage(), e);
            return null;
        }

    }

    @Override
    protected String getPlatformName() {
        return "GETCLIENT-BIRTHDAY";
    }

    @Override
    protected int getTimeout() {
        try {
            return Integer.parseInt(getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "getclient.birthday.ws.timeout"));
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
    protected GetClientBirthday createClient() {
        return new GetClientBirthdayEp(getWSDL()).getGetClientBirthdayPt();
    }

}
