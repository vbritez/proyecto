package ws.tigomoney.tigo.com;

import java.text.MessageFormat;
import java.util.GregorianCalendar;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.datatype.XMLGregorianCalendar;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.log.Action;
import com.tigo.cs.domain.WsClient;
import com.tigo.cs.facade.GlobalParameterFacade;
import com.tigo.cs.facade.SubscriberFacade;
import com.tigo.cs.facade.WsClientFacade;
import com.tigo.cs.security.Notifier;

@Stateless
public class RegisterCIWSService extends AbstractTigoMoneyWSService {

    @EJB
    private SubscriberFacade subscriberFacade;
    @EJB
    private WsClientFacade wsClientFacade;

    @EJB
    private Notifier notifier;
    @EJB
    private GlobalParameterFacade globalParameterFacade;

    protected enum RegisterCIResponseEvent {
        OK("0"), CI_ALREADY_REGISTERED("1");

        protected final String value;

        private RegisterCIResponseEvent(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    @Override
    public BaseResponse process(BaseRequest request) {
        RegisterCI registerCIRequest = (RegisterCI) request;
        RegisterCIResponse responseWS = new RegisterCIResponse();
        try {
            XMLGregorianCalendar birthDate = registerCIRequest.getBirthDate();
            GregorianCalendar cal = birthDate.toGregorianCalendar();

            WsClient wsclient = wsClientFacade.findByConsumerID(request.consumerID.toUpperCase());

            boolean ciRegistered = subscriberFacade.register(wsclient,
                    registerCIRequest.getCi(), cal.getTime(),
                    registerCIRequest.getAddress(),
                    registerCIRequest.getCity(),
                    registerCIRequest.getFrontPhoto().getValue(),
                    registerCIRequest.getBackPhoto().getValue(),
                    registerCIRequest.getSource(), "1");

            if (ciRegistered) {
                responseWS.setCode(RegisterCIResponseEvent.OK.getValue());
                responseWS.setTransactionID(registerCIRequest.getTransactionID());
                responseWS.setDescription(MessageFormat.format(
                        I18nBean.iValue("tigomoney.message.CISuccessfullyRegistered"),
                        registerCIRequest.getCi()));
                log(registerCIRequest.getConsumerID(),
                        getClass(),
                        registerCIRequest.getSource(),
                        Action.INFO,
                        getOperation(),
                        registerCIRequest.toString(),
                        MessageFormat.format(
                                I18nBean.iValue("tigomoney.message.CISuccessfullyRegistered"),
                                registerCIRequest.getCi()),
                        RegisterCIResponseEvent.OK.getValue(),
                        MessageFormat.format(
                                I18nBean.iValue("tigomoney.message.CISuccessfullyRegistered"),
                                registerCIRequest.getCi()));
            } else {
                responseWS.setCode(RegisterCIResponseEvent.CI_ALREADY_REGISTERED.getValue());
                responseWS.setTransactionID(registerCIRequest.getTransactionID());
                responseWS.setDescription(MessageFormat.format(
                        I18nBean.iValue("tigomoney.message.CIAlreadyExists"),
                        registerCIRequest.getCi()));
                log(registerCIRequest.getConsumerID(),
                        getClass(),
                        registerCIRequest.getSource(),
                        Action.WARNING,
                        getOperation(),
                        registerCIRequest.toString(),
                        MessageFormat.format(
                                I18nBean.iValue("tigomoney.message.CIAlreadyExists"),
                                registerCIRequest.getCi()),
                        RegisterCIResponseEvent.CI_ALREADY_REGISTERED.getValue(),
                        MessageFormat.format(
                                I18nBean.iValue("tigomoney.message.CIAlreadyExists"),
                                registerCIRequest.getCi()));
            }
            return responseWS;

        } catch (GenericFacadeException e) {
            responseWS.setCode(CommonsEvent.INTERNAL_ERROR.getValue());
            responseWS.setTransactionID(registerCIRequest.getTransactionID());
            responseWS.setDescription(MessageFormat.format(
                    I18nBean.iValue("tigomoney.message.ErrorHasOcurred"),
                    registerCIRequest.getCi()));
            notifier.error(
                    registerCIRequest.getConsumerID(),
                    getClass(),
                    registerCIRequest.getSource(),
                    MessageFormat.format(
                            I18nBean.iValue("tigomoney.message.ErrorHasOcurredLog"),
                            getOperation().value, registerCIRequest.toString()),
                    e);
            return responseWS;
        } catch (Exception e) {
            responseWS.setCode(CommonsEvent.INTERNAL_ERROR.getValue());
            responseWS.setTransactionID(registerCIRequest.getTransactionID());
            responseWS.setDescription(MessageFormat.format(
                    I18nBean.iValue("tigomoney.message.ErrorHasOcurred"),
                    registerCIRequest.getCi()));
            notifier.error(
                    registerCIRequest.getConsumerID(),
                    getClass(),
                    registerCIRequest.getSource(),
                    MessageFormat.format(
                            I18nBean.iValue("tigomoney.message.ErrorHasOcurredLog"),
                            getOperation().value, registerCIRequest.toString()),
                    e);
            return responseWS;
        }
    }

    @Override
    protected boolean parametersValidated(BaseRequest request) {
        RegisterCI registerCIRequest = (RegisterCI) request;
        if (registerCIRequest == null || registerCIRequest.getCi() == null
            || registerCIRequest.getCi().isEmpty()
            || registerCIRequest.getBirthDate() == null
            || registerCIRequest.getAddress() == null
            || registerCIRequest.getAddress().isEmpty()
            || registerCIRequest.getCity() == null
            || registerCIRequest.getCity().isEmpty()
            || registerCIRequest.getSource() == null
            || registerCIRequest.getSource().isEmpty()
            || registerCIRequest.getConsumerID() == null
            || registerCIRequest.getConsumerID().isEmpty()
            || registerCIRequest.getConsumerPWD() == null
            || registerCIRequest.getConsumerPWD().isEmpty()
            || registerCIRequest.getTransactionID() == null
            || registerCIRequest.getTransactionID().isEmpty()) {
            return false;
        }

        if (!isBMAANDROIDUser(registerCIRequest.getConsumerID())) {
            if (registerCIRequest.getFrontPhoto() == null
                || registerCIRequest.getFrontPhoto().isNil()
                || registerCIRequest.getFrontPhoto().getValue().length == 0
                || registerCIRequest.getBackPhoto() == null
                || registerCIRequest.getBackPhoto().isNil()
                || registerCIRequest.getBackPhoto().getValue().length == 0) {
                return false;
            }
        }

        return true;
    }

    private boolean isBMAANDROIDUser(String consumerID) {
        String bmaUser = null;
        try {
            bmaUser = globalParameterFacade.findByCode("bma.android.consumerid");
        } catch (GenericFacadeException e) {
            bmaUser = "BMAANDROID";
        }

        if (consumerID.equals(bmaUser)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected OperationEvent getOperation() {
        return OperationEvent.REGISTER_CI;
    }

}
