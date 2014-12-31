package com.tigo.cs.api.facade.ws;

import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.rmi.RemoteException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import ws.tigomoney.tigo.com.TigoMoneyWS;
import ws.tigomoney.tigo.com.TigoMoneyWSService;

import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.tigomoney.ws.ExistsCI;
import com.tigo.tigomoney.ws.ExistsCIResponse;
import com.tigo.tigomoney.ws.RegisterCI;
import com.tigo.tigomoney.ws.RegisterCIResponse;
import com.tigo.tigomoney.ws.UpdateCI;
import com.tigo.tigomoney.ws.UpdateCIResponse;

public class TigoMoneyWSServiceAPI extends AbstractWSClientServiceAPI<TigoMoneyWS> implements Serializable {

    private static final long serialVersionUID = -6175337473294621406L;

    public TigoMoneyWSServiceAPI() {
        super(TigoMoneyWS.class);
    }

    @Override
    protected int retryCount() {
        return 1;
    }

    @Override
    protected URL getWSDL() {
        try {
            return new URL(getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "tigomoney.ws.wsdl"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected String getStringWSDL() {
        try {
            return getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "tigomoney.ws.wsdl");
        } catch (GenericFacadeException e) {
            return null;
        }

    }

    @Override
    protected boolean checkIfURLExists(String targetUrl, int timeout) {
        if (targetUrl == null) {

            getFacadeContainer().getNotifier().error(
                    getClass(),
                    null,
                    MessageFormat.format("{0} - Error verifying URL. Is null.",
                            getPlatformName()), null);
            return false;
        }
        HttpURLConnection httpUrlConn;
        try {
            httpUrlConn = (HttpURLConnection) new URL(targetUrl).openConnection();
            // httpUrlConn.setHostnameVerifier(new HostnameVerifier() {
            //
            // @Override
            // public boolean verify(String hostname, SSLSession session) {
            // return true;
            // }
            // });

            httpUrlConn.setRequestMethod("GET");

            httpUrlConn.setConnectTimeout(timeout);
            httpUrlConn.setReadTimeout(timeout);

            Integer responseCode = httpUrlConn.getResponseCode();
            String responseMessage = httpUrlConn.getResponseMessage();

//            getFacadeContainer().getNotifier().debug(
//                    getClass(),
//                    null,
//                    MessageFormat.format(
//                            "{0} - Verifying WSDL URL: Response code: {1}",
//                            getPlatformName(), responseCode));

//            getFacadeContainer().getNotifier().debug(
//                    getClass(),
//                    null,
//                    MessageFormat.format(
//                            "{0} - Verifying WSDL URL: Response message: {1}",
//                            getPlatformName(), responseMessage));

            return (responseCode == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(
                    getClass(),
                    null,
                    MessageFormat.format("{0} - Error verifying URL",
                            getPlatformName()), e);
            return false;
        }
    }

    @Override
    protected String getPlatformName() {
        return "TigoMoney";
    }

    @Override
    protected int getTimeout() {
        try {
            return Integer.parseInt(getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "tigomoney.ws.timeout"));
        } catch (GenericFacadeException e) {
        }
        return 1000;
    }

    @Override
    protected QName getQname() {
//        return new QName("http://com.tigo.tigomoney.ws/", "TigoMoneyWSServiceService");
    	return new QName("http://com.tigo.tigomoney.ws/", "TigoMoneyWSService");
    }

    @Override
    public TigoMoneyWS createClient() {
        TigoMoneyWSService locator = new TigoMoneyWSService(getWSDL(), getQname());
        return locator.getTigoMoneyWSPort();   	

//        TigoMoneyWSServiceInternalService locator = new TigoMoneyWSServiceInternalService();
//        try {
//			return locator.getTigoMoneyWSServiceInternalPort(getWSDL());
//		} catch (ServiceException e) {
//			e.printStackTrace();
//			return null;
//		}
    }

    public boolean register(String ci, Date birthDate, String address, String city, byte[] frontPhoto, byte[] backPhoto, String pdvAccount, String registrationChannel) throws InvalidOperationException, RemoteException {

        XMLGregorianCalendar xmlBirthDay = null;
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(birthDate);
        try {
            xmlBirthDay = DatatypeFactory.newInstance().newXMLGregorianCalendar(
                    gc);
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        
    	String consumerID = null;
    	String consumerPWD = null;
		try {
			consumerID = getFacadeContainer().getGlobalParameterAPI().findByCode("tigomoney.ws.consumerid");
			consumerPWD = getFacadeContainer().getGlobalParameterAPI().findByCode("tigomoney.ws.consumerpwd");
		} catch (GenericFacadeException e) {
			e.printStackTrace();
		}
    	
        RegisterCI request = new com.tigo.tigomoney.ws.RegisterCI();
        request.setConsumerID(consumerID);
        request.setConsumerPWD(consumerPWD);
        request.setTransactionID("-1");
        request.setCi(ci);
        request.setBirthDate(xmlBirthDay);
        request.setAddress(address);
        request.setCity(city);
        request.setFrontPhoto(frontPhoto);
        request.setBackPhoto(backPhoto);
        request.setSource(pdvAccount);
        
        RegisterCIResponse response = getClient().registerCI(request);
        
        if (response != null && response.getCode().equals("0")){
        	return true;        	
        }else{
        	return false;
        }
        
//        return getClient().register(ci, xmlBirthDay.toGregorianCalendar(), address, city, frontPhoto,
//                backPhoto, pdvAccount, registrationChannel);
    }

    public boolean existsCI(String ci, String source) throws Exception {
    	
    	String consumerID = null;
    	String consumerPWD = null;
		try {
			consumerID = getFacadeContainer().getGlobalParameterAPI().findByCode("tigomoney.ws.consumerid");
			consumerPWD = getFacadeContainer().getGlobalParameterAPI().findByCode("tigomoney.ws.consumerpwd");
		} catch (GenericFacadeException e) {
			e.printStackTrace();
		}
    	
    	ExistsCI request = new ExistsCI();
    	request.setCi(ci);
    	request.setConsumerID(consumerID);
    	request.setConsumerPWD(consumerPWD);
    	request.setSource(source);
    	request.setTransactionID("-1");
    	
    	ExistsCIResponse response = getClient().existsCI(request);
    	if (response != null && response.getCode().equals("0")){
    		return true;
    	}else if(response != null && response.getCode().equals("1")){
    		return false;
    	}else{
    		throw new Exception("Ocurrio un error al recuperar la CI");
    	}
    	
//        return getClient().existsCI(ci);
    }
    
    public boolean update(String ci, Date birthDate, String address, String city, byte[] frontPhoto, byte[] backPhoto, String pdvAccount, String registrationChannel) throws InvalidOperationException, RemoteException {

        XMLGregorianCalendar xmlBirthDay = null;
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(birthDate);
        try {
            xmlBirthDay = DatatypeFactory.newInstance().newXMLGregorianCalendar(
                    gc);
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        
    	String consumerID = null;
    	String consumerPWD = null;
		try {
			consumerID = getFacadeContainer().getGlobalParameterAPI().findByCode("tigomoney.ws.consumerid");
			consumerPWD = getFacadeContainer().getGlobalParameterAPI().findByCode("tigomoney.ws.consumerpwd");
		} catch (GenericFacadeException e) {
			e.printStackTrace();
		}
    	
        UpdateCI request = new UpdateCI();
        request.setConsumerID(consumerID);
        request.setConsumerPWD(consumerPWD);
        request.setTransactionID("-1");
        request.setCi(ci);
        request.setBirthDate(xmlBirthDay);
        request.setAddress(address);
        request.setCity(city);
        request.setFrontPhoto(frontPhoto);
        request.setBackPhoto(backPhoto);
        request.setSource(pdvAccount);
        
        UpdateCIResponse response = getClient().updateCI(request);
        
        if (response != null && response.getCode().equals("0")){
        	return true;        	
        }else{
        	return false;
        }
        
//        return getClient().register(ci, xmlBirthDay.toGregorianCalendar(), address, city, frontPhoto,
//                backPhoto, pdvAccount, registrationChannel);
    }
}
