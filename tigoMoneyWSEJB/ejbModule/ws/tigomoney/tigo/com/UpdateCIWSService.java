package ws.tigomoney.tigo.com;

import java.text.MessageFormat;
import java.util.GregorianCalendar;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.datatype.XMLGregorianCalendar;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.log.Action;
import com.tigo.cs.domain.WsClient;
import com.tigo.cs.facade.SubscriberFacade;
import com.tigo.cs.facade.WsClientFacade;
import com.tigo.cs.security.Notifier;

@Stateless
public class UpdateCIWSService extends AbstractTigoMoneyWSService {

    @EJB
    private SubscriberFacade subscriberFacade;
    @EJB
    private WsClientFacade wsClientFacade;
    @EJB
    private Notifier notifier;

    protected enum UpdateCIResponseEvent {
        OK("0"), CI_NOT_REGISTERED("1");

        protected final String value;

        private UpdateCIResponseEvent(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    @Override
    public BaseResponse process(BaseRequest request) {
        UpdateCI updateCIRequest = (UpdateCI) request;
        UpdateCIResponse responseWS = new UpdateCIResponse();
        try {
            XMLGregorianCalendar birthDate = updateCIRequest.getBirthDate();
            GregorianCalendar cal = birthDate.toGregorianCalendar();
            WsClient wsclient = wsClientFacade.findByConsumerID(request.consumerID.toUpperCase());
            boolean ciUpdated = subscriberFacade.update(wsclient,
                    updateCIRequest.getCi(), cal.getTime(),
                    updateCIRequest.getAddress(), updateCIRequest.getCity(),
                    updateCIRequest.getFrontPhoto().getValue(),
                    updateCIRequest.getBackPhoto().getValue(),
                    updateCIRequest.getSource(), "1");

            if (ciUpdated) {
                responseWS.setCode(UpdateCIResponseEvent.OK.getValue());
                responseWS.setTransactionID(updateCIRequest.getTransactionID());
                responseWS.setDescription(MessageFormat.format(
                        I18nBean.iValue("tigomoney.message.CISuccessfullyUpdated"),
                        updateCIRequest.getCi()));
                log(updateCIRequest.getConsumerID(),
                        getClass(),
                        updateCIRequest.getSource(),
                        Action.INFO,
                        getOperation(),
                        updateCIRequest.toString(),
                        MessageFormat.format(
                                I18nBean.iValue("tigomoney.message.CISuccessfullyUpdated"),
                                updateCIRequest.getCi()),
                        UpdateCIResponseEvent.OK.getValue(),
                        MessageFormat.format(
                                I18nBean.iValue("tigomoney.message.CISuccessfullyUpdated"),
                                updateCIRequest.getCi()));
            } else {
                responseWS.setCode(UpdateCIResponseEvent.CI_NOT_REGISTERED.getValue());
                responseWS.setTransactionID(updateCIRequest.getTransactionID());
                responseWS.setDescription(MessageFormat.format(
                        I18nBean.iValue("tigomoney.message.CINotRegistered"),
                        updateCIRequest.getCi()));
                log(updateCIRequest.getConsumerID(),
                        getClass(),
                        updateCIRequest.getSource(),
                        Action.WARNING,
                        getOperation(),
                        updateCIRequest.toString(),
                        MessageFormat.format(
                                I18nBean.iValue("tigomoney.message.CINotRegistered"),
                                updateCIRequest.getCi()),
                        UpdateCIResponseEvent.CI_NOT_REGISTERED.getValue(),
                        MessageFormat.format(
                                I18nBean.iValue("tigomoney.message.CINotRegistered"),
                                updateCIRequest.getCi()));
            }
            return responseWS;

        } catch (GenericFacadeException e) {
            responseWS.setCode(CommonsEvent.INTERNAL_ERROR.getValue());
            responseWS.setTransactionID(updateCIRequest.getTransactionID());
            responseWS.setDescription(MessageFormat.format(
                    I18nBean.iValue("tigomoney.message.ErrorHasOcurredOnUpdating"),
                    updateCIRequest.getCi()));
            notifier.error(
                    updateCIRequest.getConsumerID(),
                    getClass(),
                    updateCIRequest.getSource(),
                    MessageFormat.format(
                            I18nBean.iValue("tigomoney.message.ErrorHasOcurredLog"),
                            getOperation().value, updateCIRequest.toString()),
                    e);
            return responseWS;
        } catch (Exception e) {
            responseWS.setCode(CommonsEvent.INTERNAL_ERROR.getValue());
            responseWS.setTransactionID(updateCIRequest.getTransactionID());
            responseWS.setDescription(MessageFormat.format(
                    I18nBean.iValue("tigomoney.message.ErrorHasOcurredOnUpdating"),
                    updateCIRequest.getCi()));
            notifier.error(
                    updateCIRequest.getConsumerID(),
                    getClass(),
                    updateCIRequest.getSource(),
                    MessageFormat.format(
                            I18nBean.iValue("tigomoney.message.ErrorHasOcurredLog"),
                            getOperation().value, updateCIRequest.toString()),
                    e);
            return responseWS;
        }
    }

    @Override
    protected boolean parametersValidated(BaseRequest request) {
        UpdateCI updateCIRequest = (UpdateCI) request;
        if (updateCIRequest == null || updateCIRequest.getCi() == null
            || updateCIRequest.getCi().isEmpty()
            || updateCIRequest.getBirthDate() == null
            || updateCIRequest.getAddress() == null
            || updateCIRequest.getAddress().isEmpty()
            || updateCIRequest.getCity() == null
            || updateCIRequest.getCity().isEmpty()
            || updateCIRequest.getFrontPhoto() == null
            || updateCIRequest.getFrontPhoto().isNil()
            || updateCIRequest.getFrontPhoto().getValue().length == 0
            || updateCIRequest.getBackPhoto() == null
            || updateCIRequest.getBackPhoto().isNil()
            || updateCIRequest.getBackPhoto().getValue().length == 0
            || updateCIRequest.getSource() == null
            || updateCIRequest.getSource().isEmpty()
            || updateCIRequest.getConsumerID() == null
            || updateCIRequest.getConsumerID().isEmpty()
            || updateCIRequest.getConsumerPWD() == null
            || updateCIRequest.getConsumerPWD().isEmpty()
            || updateCIRequest.getTransactionID() == null
            || updateCIRequest.getTransactionID().isEmpty()) {
            return false;
        }

        return true;
    }

    @Override
    protected OperationEvent getOperation() {
        return OperationEvent.UPDATE_CI;
    }
}
