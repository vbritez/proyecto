package com.tigo.cs.facade;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;

import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.commons.ejb.SMSTransmitterException;
import com.tigo.cs.commons.log.Action;
import com.tigo.cs.commons.util.CellPhoneNumberUtil;
import com.tigo.cs.commons.util.Cypher;
import com.tigo.cs.domain.Application;
import com.tigo.cs.domain.Userphone;
import com.tigo.cs.security.Notifier;

@Stateless
public class SendMessageFacade {

    private static final Long COD_SERVICE_FEATURE_ELEMENT = 20L;
    private static final Long COD_FUNCTIONALITY_ANDROID = 5L;

    @Resource
    TimerService timerService;
    @EJB
    private Notifier notifier;
    @EJB
    private UserphoneFacade userphoneFacade;
    @EJB
    private FacadeContainer facadeContainer;
    @EJB
    private ApplicationFacade applicationFacade;
    @EJB
    private GlobalParameterFacade globalParameterFacade;

    public SendMessageFacade() {
    }

    @Asynchronous
    public Future<Integer> sendMessageForUserphonesWithDynamicFormAndroid(Long codFeatureElement) {
        Calendar initialExpiration = Calendar.getInstance();
        timerService.createTimer(initialExpiration.getTime(), 1000,
                codFeatureElement);
        return new AsyncResult<Integer>(0);
    }

    /**
     * método callback que se invocará al terminar el intervalo definido
     */
    @Timeout
    public void execute(Timer timer) {
        try {
            Long codFeatureElement = Long.parseLong(timer.getInfo().toString());
            List<Userphone> list = userphoneFacade.findByFeatureServiceFunctionality(
                    codFeatureElement, COD_SERVICE_FEATURE_ELEMENT,
                    COD_FUNCTIONALITY_ANDROID);
            String applicationCode = globalParameterFacade.findByCode("bma.applicationCode");
            Application application = applicationFacade.find(Long.parseLong(applicationCode));

            if (list != null) {
                for (Userphone userphone : list) {
                    try {
                        String key = userphone.getCellphoneNum().toString();
                        String message = "20%*UPDATE";
                        message = Cypher.encrypt(key, message);

                        facadeContainer.getSmsQueueAPI().sendToJMS(
                                CellPhoneNumberUtil.correctMsisdnToLocalFormat(String.valueOf(userphone.getCellphoneNum())),
                                application, message);

                    } catch (SMSTransmitterException e) {
                        e.printStackTrace();
                        notifier.error(getClass(),
                                userphone.getCellphoneNum().toString(),
                                "Error al tratar de enviar un mensaje de actualización a la línea "
                                    + userphone.getCellphoneNum().toString(), e);
                    } catch (Exception e) {
                        e.printStackTrace();
                        notifier.error(getClass(),
                                userphone.getCellphoneNum().toString(),
                                "Error al tratar de enviar un mensaje de actualización a la línea "
                                    + userphone.getCellphoneNum().toString(), e);
                    }

                }
            }
            timer.cancel();

        } catch (Exception ex) {
            ex.printStackTrace();
            notifier.signal(getClass(), Action.ERROR, ex.getMessage(), ex);
            timer.cancel();
        }

    }
}
