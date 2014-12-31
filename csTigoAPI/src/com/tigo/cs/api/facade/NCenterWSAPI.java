package com.tigo.cs.api.facade;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import com.tigo.core.common.header.request.v1.RequestHeader;
import com.tigo.core.common.header.request.v1.RequestHeader.Consumer;
import com.tigo.core.common.header.request.v1.RequestHeader.Message;
import com.tigo.core.common.header.request.v1.RequestHeader.Service;
import com.tigo.core.common.header.request.v1.RequestHeader.Transport;
import com.tigo.cs.commons.log.Action;
import com.tigo.sendnotification.SendNotification;
import com.tigo.sendnotification.SendNotificationDirectBinding11QSService;
import com.tigo.sendnotification.v1.ParamType;
import com.tigo.sendnotificationrequest.v1.SendNotificationRequest;
import com.tigo.sendnotificationrequest.v1.SendNotificationRequest.RequestBody;
import com.tigo.sendnotificationrequest.v1.SendNotificationRequest.RequestBody.Attachments;
import com.tigo.sendnotificationrequest.v1.SendNotificationRequest.RequestBody.Parameters;
import com.tigo.sendnotificationresponse.v1.SendNotificationResponse;

public abstract class NCenterWSAPI extends AbstractAPI<String> implements Serializable {

    private static final long serialVersionUID = -2221175954191416280L;
    protected static SendNotification nCenterNotificationService;
    private static String NCENTER_WSDL;

    public NCenterWSAPI() {
        super(String.class);
    }

    @PostConstruct
    public void postConstruct() {
        try {

            NCENTER_WSDL = getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "ncenter.ws.wsdl");
            URL url = new URL(NCENTER_WSDL);

            if(nCenterNotificationService == null) {
                SendNotificationDirectBinding11QSService service = new SendNotificationDirectBinding11QSService(url);
                nCenterNotificationService = service.getSendNotificationDirectBinding11QSPort();
            }

        } catch (Exception ex) {
            getFacadeContainer().getNotifier().log(getClass(), null,
                    Action.ERROR, ex.getMessage(), ex);
        }
    }

    public SendNotification getNCenterNotificationService() {
        if (nCenterNotificationService == null) {
            postConstruct();
        }
        return nCenterNotificationService;
    }


    private Boolean sendNotification(String idPlataforma, String idProces, String destino, List<ParamType> parametros, Attachments adjuntos) {

        try {

            if(getNCenterNotificationService() != null) {
                SendNotificationRequest request = new SendNotificationRequest();
                request.setRequestHeader(new RequestHeader());
                request.getRequestHeader().setConsumer(new Consumer());
                request.getRequestHeader().setTransport(new Transport());
                request.getRequestHeader().setService(new Service());
                request.getRequestHeader().setMessage(new Message());
                request.getRequestHeader().getMessage().setMessageId(0l);
                request.getRequestHeader().getMessage().setMessageIdCorrelation(0l);
                request.getRequestHeader().getMessage().setConversationId(0l);

                request.setRequestBody(new RequestBody());
                request.getRequestBody().setDestiny(destino);
                request.getRequestBody().setIdPlatform(idPlataforma);
                request.getRequestBody().setIdProcess(idProces);

                request.getRequestBody().setParameters(new Parameters());
                request.getRequestBody().setAttachments(adjuntos);

                for(ParamType pt : parametros) {
                    request.getRequestBody().getParameters().getParamType().add(pt);
                }

                SendNotificationResponse response = nCenterNotificationService.process(request);
                return response.getResponseBody().isSuccess();
            }else{
                getFacadeContainer().getNotifier().log(getClass(), null,
                        Action.ERROR,
                        String.format("No se ha inicializado el NCenterNotificationService. URL: %s",NCENTER_WSDL),
                        null);
                return false;
            }

        } catch (Exception ex) {
            getFacadeContainer().getNotifier().log(getClass(), null,
                    Action.ERROR,
                    ex.getMessage(),
                    ex);
            return false;
        }
    }


    public Boolean sendSMSNotification(String idPlataforma, String idProces, String destino, String mensaje) {

        ParamType parametroType = new ParamType();
        parametroType.setName("mensaje");
        parametroType.setValue(mensaje);

        List<ParamType> list = new ArrayList<ParamType>();
        list.add(parametroType);

        return sendNotification(idPlataforma, idProces, destino, list,
                new Attachments());

    }

}