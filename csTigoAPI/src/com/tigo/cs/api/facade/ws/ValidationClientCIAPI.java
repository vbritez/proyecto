package com.tigo.cs.api.facade.ws;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import com.tigo.cs.api.entities.ValidationClientCIBean;
import com.tigo.cs.api.facade.AbstractAPI;
import com.tigo.xmlns.account.retrievepolicebaseinforequest.v1.schema.RetrievePoliceBaseInfoRequest;
import com.tigo.xmlns.account.retrievepolicebaseinforesponse.v1.schema.RetrievePoliceBaseInfoResponse;
import com.tigo.xmlns.account.service.retrievepolicebaseinfoservice.v1.RetrievePoliceBaseInfoService;
import com.tigo.xmlns.mobile.getclientbirthday.v1.schema.GetClientBirthdayRequest;
import com.tigo.xmlns.mobile.getclientbirthday.v1.schema.GetClientBirthdayResponse;
import com.tigo.xmlns.mobile.service.getclientbirthday.v1.GetClientBirthday;
import com.tigo.xmlns.requestheader.v3.CountryContentType;
import com.tigo.xmlns.requestheader.v3.GeneralConsumerInfoType;
import com.tigo.xmlns.requestheader.v3.RequestHeader;

public class ValidationClientCIAPI extends AbstractAPI<String> {

    private static String TIGOMONEY_ID;
    private static String CODE_SUCCESS_RETRIEVE;
    private static String DOCUMENT_TYPE;
    private RequestHeader request;
    private com.tigo.xmlns.responseheader.v3.ObjectFactory objResponse;
    private SimpleDateFormat sdf;
    private static String PATTERN = "ddMMyyyy";

    protected ValidationClientCIAPI() {
        super(String.class);
    }

    private RequestHeader getRequestHeader() {
        if (request == null) {

            if (TIGOMONEY_ID == null || TIGOMONEY_ID.equals("")) {
                init();
            }

            request = new RequestHeader();
            request.setGeneralConsumerInformation(new GeneralConsumerInfoType());
            com.tigo.xmlns.requestheader.v3.ObjectFactory objectRequest = new com.tigo.xmlns.requestheader.v3.ObjectFactory();
            request.getGeneralConsumerInformation().setConsumerID(
                    objectRequest.createGeneralConsumerInfoTypeConsumerID(TIGOMONEY_ID));
            request.getGeneralConsumerInformation().setTransactionID(
                    objectRequest.createGeneralConsumerInfoTypeTransactionID(TIGOMONEY_ID));
            request.getGeneralConsumerInformation().setCountry(
                    objectRequest.createGeneralConsumerInfoTypeCountry(CountryContentType.PRY));
            request.getGeneralConsumerInformation().setCorrelationID(
                    TIGOMONEY_ID);
        }
        return request;
    }

    private com.tigo.xmlns.responseheader.v3.ObjectFactory getObjResponse() {
        if (objResponse == null) {
            objResponse = new com.tigo.xmlns.responseheader.v3.ObjectFactory();
        }

        return objResponse;
    }

    public void init() {
        try {
            TIGOMONEY_ID = getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "tigo.money.consumer.id");
            CODE_SUCCESS_RETRIEVE = getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "retrieve.police.code.success");

            DOCUMENT_TYPE = getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "getclient.birthday.document.type");
            PATTERN = getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "tigo.money.pattern.date");
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(getClass(), null,
                    e.getMessage(), e);
        }
    }

    private SimpleDateFormat getSimpleDateFormat() {
        if (sdf == null) {
            sdf = new SimpleDateFormat(PATTERN);
        }
        return sdf;
    }

    public ValidationClientCIBean retrievePoliceBaseInfo(String msisdn, String ci, Date date) {
        Date fechaInicio = new Date();
        RetrievePoliceBaseInfoService retrievePoliceService = null;
        String dateBirthReponse = null;
        Long time = null;
        ValidationClientCIBean responseBean = new ValidationClientCIBean();
        try {
            RetrievePoliceBaseInfoRequest request = new RetrievePoliceBaseInfoRequest();
            request.setRequestHeader(getRequestHeader());
            request.setRequestBody(new com.tigo.xmlns.account.retrievepolicebaseinforequest.v1.schema.RetrievePoliceBaseInfoRequest.RequestBody());
            request.getRequestBody().setCi(ci.toUpperCase());

            retrievePoliceService = getFacadeContainer().getRetrievePoliceBaseInfoAPI().getClient();

            RetrievePoliceBaseInfoResponse response = retrievePoliceService.retrievePoliceBaseInfo(request);

            if (response != null
                && response.getResponseHeader() != null
                && response.getResponseHeader().getGeneralResponse().getCode().getValue().equals(
                        getObjResponse().createGeneralResponseTypeCode(
                                CODE_SUCCESS_RETRIEVE).getValue())) {

                String dateBirth = getSimpleDateFormat().format(date);
                Date aux = toDate(response.getResponseBody().getBirthDate());
                dateBirthReponse = aux != null ? getSimpleDateFormat().format(
                        aux) : "";

                if (dateBirth.equals(dateBirthReponse)) {
                    responseBean.setValidCI(true);
                    responseBean.setResponseCode(response.getResponseHeader().getGeneralResponse().getCode().getValue());
                    responseBean.setMessage(response.getResponseHeader().getGeneralResponse().getDescription().getValue());
                    responseBean.setBirthday(dateBirthReponse);
                    responseBean.setName(response.getResponseBody().getFirstName());
                    responseBean.setLastname(response.getResponseBody().getLastName());
                } else {
                    responseBean.setValidCI(false);
                    responseBean.setResponseCode("-1");
                    responseBean.setMessage("El número de cédula no concuerda con la fecha de nacimiento ingresada");
                    responseBean.setBirthday(dateBirthReponse);
                    responseBean.setName(response.getResponseBody().getFirstName());
                    responseBean.setLastname(response.getResponseBody().getLastName());
                }

            } else {
                responseBean.setValidCI(false);
                responseBean.setResponseCode(response.getResponseHeader().getGeneralResponse().getCode().getValue());
                responseBean.setMessage(response.getResponseHeader().getGeneralResponse().getDescription().getValue());
            }

        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(getClass(), msisdn,
                    e.getMessage(), e);
            responseBean.setValidCI(false);
            responseBean.setResponseCode("-2");
            responseBean.setMessage(e.getMessage());
        }

        Date fechaFin = new Date();
        time = fechaFin.getTime() - fechaInicio.getTime();
        getFacadeContainer().getNotifier().info(
                getClass(),
                msisdn,
                MessageFormat.format(
                        getFacadeContainer().getI18nAPI().iValue(
                                "tigomoney.message.retrievepolive.response"),
                        ci,
                        responseBean.getResponseCode().equals("-2") ? "ERROR" : "OK",
                        dateBirthReponse != null ? dateBirthReponse : "",
                        time != null ? time.toString() : ""));

        return responseBean;
    }

    public ValidationClientCIBean getClientBirthday(String msisdn, String ci, Date date) {
        Date fechaInicio = new Date();
        GetClientBirthday clientBirthdayService = null;
        String dateBirthReponse = null;
        Long time = null;
        ValidationClientCIBean responseBean = new ValidationClientCIBean();
        try {
            GetClientBirthdayRequest request = new GetClientBirthdayRequest();
            request.setRequestHeader(getRequestHeader());
            request.setRequestBody(new com.tigo.xmlns.mobile.getclientbirthday.v1.schema.GetClientBirthdayRequest.RequestBody());
            request.getRequestBody().setDocumentNumber(ci.toUpperCase());
            request.getRequestBody().setDocumentType(DOCUMENT_TYPE);
            clientBirthdayService = getFacadeContainer().getGetClientBirthdayAPI().getClient();

            GetClientBirthdayResponse response = clientBirthdayService.getClientBirthday(request);

            if (response != null
                && response.getResponseBody() != null
                && response.getResponseBody().getBirhDay() != null
                && (response.getResponseBody().getBusinessName() != null && !response.getResponseBody().getBusinessName().equals(
                        ""))) {
                String[] names = response.getResponseBody().getBusinessName().split(
                        ",");

                String dateBirth = getSimpleDateFormat().format(date);
                Date aux = toDate(response.getResponseBody().getBirhDay());
                dateBirthReponse = aux != null ? getSimpleDateFormat().format(
                        aux) : "";

                if (dateBirth.equals(dateBirthReponse)) {

                    responseBean.setValidCI(true);
                    responseBean.setResponseCode("0");
                    responseBean.setMessage("Se recupero exitosamente la informacion de la BD del BCCS");
                    responseBean.setBirthday(dateBirthReponse);
                    responseBean.setName(names[1]);
                    responseBean.setLastname(names[0]);

                } else {
                    responseBean.setValidCI(false);
                    responseBean.setResponseCode("-1");
                    responseBean.setMessage("El número de cédula no concuerda con la fecha de nacimiento ingresada");
                    responseBean.setBirthday(dateBirthReponse);
                    responseBean.setName(names[1]);
                    responseBean.setLastname(names[0]);

                }

            } else {
                responseBean.setValidCI(false);
                responseBean.setResponseCode("-3");
                responseBean.setMessage("La cédula ingresada no existe en la BD del BCCS");
            }
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(getClass(), msisdn,
                    e.getMessage(), e);
            responseBean.setValidCI(false);
            responseBean.setResponseCode("-2");
            responseBean.setMessage(e.getMessage());

        }

        Date fechaFin = new Date();
        time = fechaFin.getTime() - fechaInicio.getTime();
        getFacadeContainer().getNotifier().info(
                getClass(),
                msisdn,
                MessageFormat.format(
                        getFacadeContainer().getI18nAPI().iValue(
                                "tigomoney.message.getclientbirthday.response"),
                        ci,
                        responseBean.getResponseCode().equals("-2") ? "ERROR" : "OK",
                        dateBirthReponse != null ? dateBirthReponse : "",
                        time != null ? time.toString() : ""));
        return responseBean;
    }

    private Date toDate(XMLGregorianCalendar calendar) {
        if (calendar == null) {
            return null;
        }
        return calendar.toGregorianCalendar().getTime();
    }

}