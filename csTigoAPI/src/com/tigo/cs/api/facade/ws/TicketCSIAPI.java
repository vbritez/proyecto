package com.tigo.cs.api.facade.ws;

import java.io.Serializable;
import java.net.URL;
import java.text.MessageFormat;

import javax.xml.namespace.QName;

import com.tigo.core.common.header.request.v1.RequestHeader;
import com.tigo.core.common.header.request.v1.RequestHeader.Consumer.Credentials;
import com.tigo.core.common.header.request.v1.RequestHeader.Consumer.Credentials.User;
import com.tigo.core.common.v1.CommunicationType;
import com.tigo.core.common.v1.TransportCodeType;
import com.tigo.createcsi.v1.CreateCSI;
import com.tigo.createcsirequest.v1.CreateCSIRequest;
import com.tigo.createcsirequest.v1.CreateCSIRequest.RequestBody;
import com.tigo.createcsiresponse.v1.CreateCSIResponse;
import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.facade.AbstractAPI;
import com.tigo.cs.commons.log.Action;

public abstract class TicketCSIAPI extends AbstractAPI<String> implements Serializable {

    private static final long serialVersionUID = 2706930341176827456L;

    private RequestHeader request;

    private static CreateCSI csi;

    public TicketCSIAPI() {
        super(String.class);
    }

    public void postConstruct() {
        try {

            String wsdl = getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "ticketcsi.wsdl");

            QName serviceName = new QName("http://www.tigo.com/CreateCSI/V1", "CreateCSI_ep");
            URL url = new URL(wsdl);

            if (csi == null) {
                csi = javax.xml.ws.Service.create(url, serviceName).getPort(
                        CreateCSI.class);
            }
        } catch (Exception ex) {
            getFacadeContainer().getNotifier().log(getClass(), null,
                    Action.ERROR, "No se pudo conectar al CreateCSI", ex);
        }
    }

    public CreateCSI getCreateCSI() {
        if (csi == null) {
            postConstruct();
        }
        return csi;
    }

    public String createTicket(String user, String password, String category, String group, String summary, String description) {

        try {

            CreateCSIRequest csiRequest = new CreateCSIRequest();
            csiRequest.setRequestHeader(getHeader(user, password));
            csiRequest.setRequestBody(getRequestBody(category, group, summary,
                    description));

            CreateCSIResponse response = getCreateCSI().createCSI(csiRequest);

            if (!response.getResponseBody().isSuccessful()) {
                throw new InvalidOperationException("Problemas creando el Ticket CSI");
            }

            getFacadeContainer().getNotifier().info(
                    getClass(),
                    null,
                    MessageFormat.format("Ticket Creado exitosamente: {0}",
                            response.getResponseBody().getRequestNumber()));

            return response.getResponseBody().getRequestNumber();

        } catch (Exception ex) {
            getFacadeContainer().getNotifier().log(getClass(), null,
                    Action.ERROR, "No se pudo conectar al CreateCSI", ex);
            return null;
        }
    }

    private RequestHeader getHeader(String user, String password) throws Exception {

        RequestHeader request = new RequestHeader();

        request.setConsumer(new RequestHeader.Consumer());
        request.getConsumer().setCode("CSTIGO");
        request.getConsumer().setName("CSTIGO");
        request.getConsumer().setCredentials(new Credentials());
        request.getConsumer().getCredentials().setUser(new User());
        request.getConsumer().getCredentials().getUser().setUserName(user);
        request.getConsumer().getCredentials().getUser().setPassword(password);

        request.setTransport(new RequestHeader.Transport());
        request.getTransport().setCode(TransportCodeType.WS);
        request.getTransport().setCommunicationType(CommunicationType.SYN);

        request.setService(new RequestHeader.Service());
        request.getService().setCode("CSI");
        request.getService().setName("CSI");

        request.setMessage(new RequestHeader.Message());
        request.getMessage().setMessageId(1L);
        request.getMessage().setMessageIdCorrelation(1L);
        request.getMessage().setConversationId(1L);

        request.setCountry(new RequestHeader.Country());
        request.getCountry().setIsoCode("600");
        request.getCountry().setName("PY");

        return request;
    }

    private RequestBody getRequestBody(String category, String group, String summary, String description) throws Exception {

        String asignee = getFacadeContainer().getGlobalParameterAPI().findByCode(
                "ticketcsi.asignee");

        RequestBody body = new RequestBody();

        body.setCategory(category);
        body.setAssignee(asignee);
        body.setGroup(group);
        body.setCustomer(group);
        body.setSummary(summary);
        body.setDescription(description);

        return body;
    }
}
