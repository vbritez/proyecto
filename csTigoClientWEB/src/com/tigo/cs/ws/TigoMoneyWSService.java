package com.tigo.cs.ws;

import java.util.Date;

import javax.ejb.EJB;
import javax.jws.WebMethod;
import javax.jws.WebParam;

import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.Subscriber;
import com.tigo.cs.domain.WsClient;

//@WebService()
public class TigoMoneyWSService {

    @EJB
    private FacadeContainer facadeContainer;

    // public TigoMoneyWSService(String host, String port, String path) {
    // String address = "http://%s:%s/%s";
    // Endpoint.publish(String.format(address, host, port, path), this);
    // }

    @WebMethod(operationName = "registerCI")
    public boolean register(@WebParam(name = "ci") String ci, @WebParam(
            name = "birthDate") Date birthDate, @WebParam(name = "address") String address, @WebParam(
            name = "city") String city, @WebParam(name = "frontPhoto") byte[] frontPhoto, @WebParam(
            name = "backPhoto") byte[] backPhoto, @WebParam(name = "pdvAccount") String pdvAccount, @WebParam(
            name = "registrationChannel") String registrationChannel) {
        try {
        	String consumerID = facadeContainer.getGlobalParameterAPI().findByCode("tigomoney.ws.consumerid");
    		
        	WsClient wsclient = facadeContainer.getWsClientAPI().findByConsumerID(consumerID);
        	
            return facadeContainer.getSubscriberAPI().register(wsclient,ci, birthDate,
                    address, city, frontPhoto, backPhoto, pdvAccount,
                    registrationChannel);
        } catch (GenericFacadeException e) {
            facadeContainer.getNotifier().error(TigoMoneyWSService.class, null,
                    e.getMessage(), e);
            return false;
        }

    }

    @WebMethod(operationName = "existsCI")
    public boolean existsCI(@WebParam(name = "ci") String ci) {
        Subscriber subscriber;
        try {
            subscriber = facadeContainer.getSubscriberAPI().findByCi(ci);
            if (subscriber == null)
                return false;
            else
                return true;
        } catch (GenericFacadeException e) {
            facadeContainer.getNotifier().error(TigoMoneyWSService.class, null,
                    e.getMessage(), e);
            return false;
        }

    }
}
