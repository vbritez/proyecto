package com.tigo.cs.api.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import com.tigo.cs.api.entities.InformconfService;
import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.exception.MalformedSMSException;
import com.tigo.cs.api.exception.OperationNotAllowedException;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;

public abstract class InformconfServiceAPI<T extends InformconfService> extends AbstractServiceAPI<InformconfService> {

    protected static String INFORMCONF_URL = null;
    private String informconfPerson = "";

    public InformconfServiceAPI() {
    }

    @Override
    public InformconfService getEntity() {
        if (super.getEntity() == null) {
            setEntity(new InformconfService());
        }
        return super.getEntity();
    }

    @Override
    public InformconfService getReturnEntity() {
        if (super.getReturnEntity() == null) {
            setReturnEntity(new InformconfService());
        }
        return super.getReturnEntity();
    }

    @Override
    public String processService() throws GenericFacadeException, InvalidOperationException, OperationNotAllowedException {
        try {
            if (INFORMCONF_URL == null) {
                INFORMCONF_URL = getFacadeContainer().getGlobalParameterAPI().findByCode(
                        "external.url.informconf-service");
            }
            String phoneNumStr = "0"
                + getUserphone().getCellphoneNum().toString().substring(3);
            String response = requestToInformconf(getEntity().getCedula(),
                    phoneNumStr);
            if (response == null) {
                throw new InvalidOperationException("No se pudo obtener información desde informconf.");
            }

            treatHeader();
            returnMessage = response;
        } catch (MalformedSMSException ex) {
            return ex.getMessage();
        } catch (InvalidOperationException ex) {
            return ex.getMessage();
        } catch (Exception e) {
            return CANNOT_PROCESS_MSG;
        }
        return returnMessage;
    }

    @Override
    protected ServiceValue treatHeader() {
        ServiceValue serviceValue = getFacadeContainer().getServiceValueAPI().getCurrentServiceValue(
                getUserphone(), getService());
        if (serviceValue == null) {
            serviceValue = new ServiceValue();
            serviceValue.setService(getService());
            serviceValue.setUserphone(getUserphone());
            serviceValue.setMessage(getMessage());
            serviceValue.setRecorddateDat(validateDate());
            serviceValue.setColumn1Chr(getEntity().getCedula());
            serviceValue.setColumn2Chr(informconfPerson);
            serviceValue = getFacadeContainer().getServiceValueAPI().create(
                    serviceValue);
        }
        return serviceValue;
    }

    @Override
    protected List<ServiceValueDetail> treatDetails(ServiceValue serviceValue) {
        return null;
    }

    public String requestToInformconf(String nrodocumento, String phoneNumber) throws InvalidOperationException, Exception {

        String response = null;

        URL url = new URL(INFORMCONF_URL + "?documento='" + nrodocumento
            + "'&telefono='" + phoneNumber + "'");
        HttpURLConnection conn = getHttpConnectionToInformconf(url);

        InputStream in = null;
        try {
            in = conn.getInputStream();
        } catch (FileNotFoundException fnf) {
            getFacadeContainer().getNotifier().error(
                    InformconfServiceAPI.class,
                    getCellphoneNumber().toString(),
                    "Error al hacer el request a Informconf.", fnf);
            throw fnf;

        }
        if (in != null) {
            response = parseResponseSuccess(new BufferedReader(new InputStreamReader(in)));
        }

        conn.disconnect();
        return response;
    }

    private HttpURLConnection getHttpConnectionToInformconf(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "text/html");
        conn.setRequestProperty("Accept", "text/html");
        conn.setDoInput(true);
        return conn;
    }

    private String parseResponseSuccess(BufferedReader reader) throws InvalidOperationException, Exception {
        String line = reader.readLine();
        String response = "";
        while (line != null) {
            response = response + line;
            line = reader.readLine();
        }
        if (!response.contains("|")) {
            throw new InvalidOperationException(response);
        }
        response = response.replaceAll("=", ": ");
        String[] resp = response.split("\\|");

        informconfPerson = resp[1];
        StringBuilder returnString = new StringBuilder(getFacadeContainer().getI18nAPI().iValue(
                "informconf.name.SuccessMessage"));
        returnString.append("\n".concat(
                getFacadeContainer().getI18nAPI().iValue("informconf.Ci")).concat(
                ": ").concat(resp[0]));
        returnString.append("\n".concat(
                getFacadeContainer().getI18nAPI().iValue("informconf.Name")).concat(
                ": ").concat(resp[1]));
        for (int i = 2; i < resp.length - 1; i++) {
            // Ej.: resp[i]="De=0"
            String key = "\n"
                + getFacadeContainer().getI18nAPI().iValue(
                        "informconf." + resp[i].substring(0, 2));
            returnString.append(key.concat(resp[i].substring(2)));
        }
        String key = "\n"
            + getFacadeContainer().getI18nAPI().iValue("informconf.Cs");
        returnString.append(key.concat(": ").concat(resp[resp.length - 1]));
        return returnString.toString();
    }

    @Override
    protected void assignServiceEvent() {
    }
}
