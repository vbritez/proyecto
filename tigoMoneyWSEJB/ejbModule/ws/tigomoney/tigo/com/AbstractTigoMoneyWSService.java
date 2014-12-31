package ws.tigomoney.tigo.com;

import javax.ejb.EJB;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.log.Action;
import com.tigo.cs.commons.util.NumberUtil;
import com.tigo.cs.domain.WsClient;
import com.tigo.cs.facade.WsClientFacade;
import com.tigo.cs.security.Notifier;

public abstract class AbstractTigoMoneyWSService {
	
	@EJB
	protected WsClientFacade wsClientFacade;	
	@EJB
	protected Notifier notifier;
	
	protected abstract BaseResponse process(BaseRequest request);
	protected abstract boolean parametersValidated(BaseRequest request);
	protected abstract OperationEvent getOperation();	
	
	public enum CommonsEvent {
        PARAMETERS_NOT_VALID("300"),
        OPERATION_NOT_ALLOWED("200"),
        AUTHENTICATION_ERROR("100"),
        INTERNAL_ERROR("-1");
        
        protected final String value;
        
        private CommonsEvent(String value){
        	this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
	
	protected enum OperationEvent {
        REGISTER_CI("registerCI"),
        UPDATE_CI("updateCI"),
        EXISTS_CI("existsCI"),
        GET_CI("getCI");
        
        protected final String value;
        
        private OperationEvent(String value){
        	this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
	
	protected boolean validConsumer(String consumerID, String consumerPWD) {	
		try {
			WsClient client = wsClientFacade.findByConsumerID(consumerID);
			if (client != null){
				return consumerPWD.equals(client.getConsumerPwdChr());
			}
		} catch (GenericFacadeException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean validate(BaseRequest request) throws TigoMoneyValidationException{
		if (!parametersValidated(request)){			
			log(request.getConsumerID(), getClass(), request.getSource(), Action.WARNING, getOperation(), request.toString(), I18nBean.iValue("tigomoney.message.MustInsertAllParameters") , CommonsEvent.PARAMETERS_NOT_VALID.getValue(), I18nBean.iValue("tigomoney.message.MustInsertAllParameters"));
			throw new TigoMoneyValidationException(CommonsEvent.PARAMETERS_NOT_VALID.getValue(), I18nBean.iValue("tigomoney.message.MustInsertAllParameters"), request.getTransactionID());
			
		}	
		
		if (!validConsumer(request.getConsumerID(), request.getConsumerPWD())){			
			log(request.getConsumerID(),getClass(), request.getSource(), Action.WARNING, getOperation(), request.toString(), I18nBean.iValue("tigomoney.message.AuthenticationError"), CommonsEvent.AUTHENTICATION_ERROR.getValue(), I18nBean.iValue("tigomoney.message.AuthenticationError"));	
			throw new TigoMoneyValidationException(CommonsEvent.AUTHENTICATION_ERROR.getValue(), I18nBean.iValue("tigomoney.message.AuthenticationError"), request.getTransactionID());
		}			

		if (!operationAllowed(request.getConsumerID(), getOperation())){				
			log(request.getConsumerID(),getClass(), request.getSource(), Action.WARNING, getOperation(), request.toString() ,I18nBean.iValue("tigomoney.message.OperationNotAllowedException"), CommonsEvent.OPERATION_NOT_ALLOWED.getValue(), I18nBean.iValue("tigomoney.message.OperationNotAllowedException"));	
			throw new TigoMoneyValidationException(CommonsEvent.OPERATION_NOT_ALLOWED.getValue(), I18nBean.iValue("tigomoney.message.OperationNotAllowedException"), request.getTransactionID());
		}
		return true;
	}

//	protected boolean parametersValidatedRegisterCI(RegisterCI registerCIRequest){
//		if (registerCIRequest == null || registerCIRequest.getCi() == null || registerCIRequest.getCi().isEmpty() 
//				|| registerCIRequest.getBirthDate() == null || registerCIRequest.getAddress() == null || registerCIRequest.getAddress().isEmpty()
//				|| registerCIRequest.getCity() == null || registerCIRequest.getCity().isEmpty()
//				|| registerCIRequest.getFrontPhoto() == null || registerCIRequest.getFrontPhoto().isNil()
//				|| registerCIRequest.getBackPhoto() == null || registerCIRequest.getBackPhoto().isNil()
//				|| registerCIRequest.getSource() == null || registerCIRequest.getSource().isEmpty()
//				|| registerCIRequest.getConsumerID() == null || registerCIRequest.getConsumerID().isEmpty()
//				|| registerCIRequest.getConsumerPWD() == null || registerCIRequest.getConsumerPWD().isEmpty()
//				|| registerCIRequest.getTransactionID() == null || registerCIRequest.getTransactionID().isEmpty()) {
//			return false;
//		}
//		return true;
//	}	
	
//	protected boolean parametersValidatedExistsCI(ExistsCI existsCIRequest){
//		if (existsCIRequest == null || existsCIRequest.getCi() == null || existsCIRequest.getCi().isEmpty()
//				|| existsCIRequest.getConsumerID() == null ||  existsCIRequest.getConsumerID().isEmpty()
//				|| existsCIRequest.getConsumerPWD() == null || existsCIRequest.getConsumerPWD().isEmpty()
//				|| existsCIRequest.getSource() == null || existsCIRequest.getSource().isEmpty()
//				|| existsCIRequest.getTransactionID() == null || existsCIRequest.getTransactionID().isEmpty()){
//			return false;
//		}
//		return true;
//	}
	
//	protected boolean parametersValidatedGetCI(GetCI getCIRequest){
//		if (getCIRequest == null || getCIRequest.getCi() == null || getCIRequest.getCi().isEmpty()
//				|| getCIRequest.getConsumerID() == null ||  getCIRequest.getConsumerID().isEmpty()
//				|| getCIRequest.getConsumerPWD() == null || getCIRequest.getConsumerPWD().isEmpty()
//				|| getCIRequest.getSource() == null || getCIRequest.getSource().isEmpty()
//				|| getCIRequest.getTransactionID() == null || getCIRequest.getTransactionID().isEmpty()){
//			return false;
//		}
//		return true;
//	}
	

	
	protected boolean operationAllowed(String consumerID, OperationEvent operation) {
		WsClient wsClient = null;
		try {
			wsClient = wsClientFacade.findByConsumerID(consumerID);
		} catch (GenericFacadeException e) {
			e.printStackTrace();
		}
		
		if (wsClient != null){
			switch (operation) {
			case REGISTER_CI:
				return wsClient.getOpRegisterciAllowedChr();
			case EXISTS_CI:
				return wsClient.getOpExistsciAllowedChr();
			case GET_CI:
				return wsClient.getOpGetciAllowedChr();
			case UPDATE_CI:
				return wsClient.getOpUpdateciAllowedChr();
			default:
				break;
			}
		}
		return false;
	}
	
	protected void log(String consumer, Class clazz, String msisdn, Action action, OperationEvent operation, String request, String response, String code, String description){
		String msg = "OPERATION -> " + operation.value + ", REQUEST-> " + request + ", RESPONSE-> " + "Code: " + code + ". Description: " + description; 
		notifier.signal(consumer, clazz, msisdn,  action, msg);
	}
	
	protected boolean isNumber(String source){
		return NumberUtil.isNumber(source);
	}
}

