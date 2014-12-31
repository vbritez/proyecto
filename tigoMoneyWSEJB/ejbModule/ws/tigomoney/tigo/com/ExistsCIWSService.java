package ws.tigomoney.tigo.com;

import java.text.MessageFormat;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.log.Action;
import com.tigo.cs.domain.Subscriber;
import com.tigo.cs.facade.SubscriberFacade;
import com.tigo.cs.security.Notifier;

@Stateless
public class ExistsCIWSService extends AbstractTigoMoneyWSService{
	
	@EJB
	private SubscriberFacade subscriberFacade;
	@EJB
	private Notifier notifier;
	
	protected enum ExistsCIResponseEvent {
        CI_REGISTERED("0"),
        CI_NOT_REGISTERED("1");
        
        protected final String value;
        
        private ExistsCIResponseEvent(String value){
        	this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

	@Override
	public BaseResponse process(BaseRequest request) {
		ExistsCI existsCIRequest = (ExistsCI)request;
		ExistsCIResponse responseWS = new ExistsCIResponse();
		Subscriber subscriber = null;
		try{
			subscriber = subscriberFacade.findByCi(existsCIRequest.getCi());
			if (subscriber == null){ /*la cedula no existe*/
				responseWS.setCode(ExistsCIResponseEvent.CI_NOT_REGISTERED.getValue());
				responseWS.setDescription(MessageFormat.format(I18nBean.iValue("tigomoney.message.CIDoesNotExists"), existsCIRequest.getCi()));
				responseWS.setTransactionID(existsCIRequest.getTransactionID());
				log(existsCIRequest.getConsumerID(), getClass(), existsCIRequest.getSource(), Action.INFO, getOperation(), existsCIRequest.toString(), MessageFormat.format(I18nBean.iValue("tigomoney.message.CIDoesNotExists"), 
						existsCIRequest.getCi()), ExistsCIResponseEvent.CI_NOT_REGISTERED.getValue(), MessageFormat.format(I18nBean.iValue("tigomoney.message.CIDoesNotExists"), existsCIRequest.getCi()));
				return responseWS;
			}else{/*la cedula ya existe*/
				responseWS.setCode(ExistsCIResponseEvent.CI_REGISTERED.getValue());
				responseWS.setDescription(MessageFormat.format(I18nBean.iValue("tigomoney.message.CIAlreadyExists"), existsCIRequest.getCi()));
				responseWS.setTransactionID(existsCIRequest.getTransactionID());
				log(existsCIRequest.getConsumerID(),getClass(), existsCIRequest.getSource(), Action.INFO, getOperation(), existsCIRequest.toString(), MessageFormat.format(I18nBean.iValue("tigomoney.message.CIAlreadyExists"), 
						existsCIRequest.getCi()), ExistsCIResponseEvent.CI_REGISTERED.getValue(), MessageFormat.format(I18nBean.iValue("tigomoney.message.CIAlreadyExists"), existsCIRequest.getCi()));
				return responseWS;
			}
			
		} catch (GenericFacadeException e) {
			responseWS.setCode(CommonsEvent.INTERNAL_ERROR.getValue());
			responseWS.setDescription(MessageFormat.format(I18nBean.iValue("tigomoney.message.ErrorHasOcurredWhenRecoverCI"), existsCIRequest.getCi()));
			responseWS.setTransactionID(existsCIRequest.getTransactionID());
			notifier.error(existsCIRequest.getConsumerID(),getClass(), existsCIRequest.getSource(), MessageFormat.format(I18nBean.iValue("tigomoney.message.ErrorHasOcurredLog"), getOperation().value, existsCIRequest.toString()), e);
			return responseWS;
		}catch (Exception e) {
			responseWS.setCode(CommonsEvent.INTERNAL_ERROR.getValue());
			responseWS.setTransactionID(existsCIRequest.getTransactionID());
			responseWS.setDescription(MessageFormat.format(I18nBean.iValue("tigomoney.message.ErrorHasOcurredWhenRecoverCI"), existsCIRequest.getCi()));
			notifier.error(existsCIRequest.getConsumerID(),getClass(), existsCIRequest.getSource(), MessageFormat.format(I18nBean.iValue("tigomoney.message.ErrorHasOcurredLog"), getOperation().value, existsCIRequest.toString()), e);
			return responseWS;
		}
	}

	@Override
	protected boolean parametersValidated(BaseRequest request) {
		ExistsCI existsCIRequest = (ExistsCI)request;
		if (existsCIRequest == null || existsCIRequest.getCi() == null || existsCIRequest.getCi().isEmpty()
				|| existsCIRequest.getConsumerID() == null ||  existsCIRequest.getConsumerID().isEmpty()
				|| existsCIRequest.getConsumerPWD() == null || existsCIRequest.getConsumerPWD().isEmpty()
				|| existsCIRequest.getSource() == null || existsCIRequest.getSource().isEmpty()
				|| existsCIRequest.getTransactionID() == null || existsCIRequest.getTransactionID().isEmpty()){
			return false;
		}
		return true;
	}

	@Override
	protected OperationEvent getOperation() {
		return OperationEvent.EXISTS_CI;
	}	

}
