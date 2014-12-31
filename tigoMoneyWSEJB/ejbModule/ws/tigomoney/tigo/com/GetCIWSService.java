package ws.tigomoney.tigo.com;

import java.text.MessageFormat;
import java.util.GregorianCalendar;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.log.Action;
import com.tigo.cs.domain.Subscriber;
import com.tigo.cs.facade.SubscriberFacade;
import com.tigo.cs.security.Notifier;

@Stateless
public class GetCIWSService extends AbstractTigoMoneyWSService{
	
	@EJB 
	private SubscriberFacade subscriberFacade;
	@EJB 
	private Notifier notifier;
	
	protected enum GetCIResponseEvent {
        CI_REGISTERED("0"),
        CI_NOT_REGISTERED("1");
        
        protected final String value;
        
        private GetCIResponseEvent(String value){
        	this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

	@Override
	public BaseResponse process(BaseRequest request) {
		GetCI getCIRequest = (GetCI)request;
		GetCIResponse responseWS = new GetCIResponse();
		Subscriber subscriber = null;
		try{
			subscriber = subscriberFacade.findByCi(getCIRequest.getCi());
			if (subscriber == null){ 
				responseWS.setCode(GetCIResponseEvent.CI_NOT_REGISTERED.getValue());
				responseWS.setDescription(MessageFormat.format(I18nBean.iValue("tigomoney.message.CIDoesNotExists"), getCIRequest.getCi()));
				responseWS.setTransactionID(getCIRequest.getTransactionID());
				log(getCIRequest.getConsumerID(), getClass(), getCIRequest.getSource(), Action.INFO, getOperation(), getCIRequest.toString(), MessageFormat.format(I18nBean.iValue("tigomoney.message.CIDoesNotExists"), 
						getCIRequest.getCi()), GetCIResponseEvent.CI_NOT_REGISTERED.getValue(), MessageFormat.format(I18nBean.iValue("tigomoney.message.CIDoesNotExists"), getCIRequest.getCi()));
			}else{
				responseWS.setCode(GetCIResponseEvent.CI_REGISTERED.getValue());
				responseWS.setDescription(MessageFormat.format(I18nBean.iValue("tigomoney.message.CISuccessfullyRecovered"), getCIRequest.getCi()));
				responseWS.setTransactionID(getCIRequest.getTransactionID());

				XMLGregorianCalendar xmlBirthDay = null;
				XMLGregorianCalendar xmlInsertedOn = null;
				XMLGregorianCalendar xmlUpdatedOn = null;
				if (subscriber.getBirthdateDat() != null){
			        try {
				        GregorianCalendar gc = new GregorianCalendar();
				        gc.setTime(subscriber.getBirthdateDat());
				        
				        if (subscriber.getInsertedOnDat() != null){
					        GregorianCalendar gcInsertedOn = new GregorianCalendar();
					        gcInsertedOn.setTime(subscriber.getInsertedOnDat());
	
				            xmlInsertedOn = DatatypeFactory.newInstance().newXMLGregorianCalendar(
				            		gcInsertedOn);
				            xmlInsertedOn.setTimezone( DatatypeConstants.FIELD_UNDEFINED );
				        }
				        
				        if (subscriber.getUpdatedOnDat() != null){
					        GregorianCalendar gcUpdatedOn = new GregorianCalendar();
					        gcUpdatedOn.setTime(subscriber.getUpdatedOnDat());
				        
				            xmlBirthDay = DatatypeFactory.newInstance().newXMLGregorianCalendar(
				                    gc);
				            xmlBirthDay.setTimezone( DatatypeConstants.FIELD_UNDEFINED );
				            
				            
				            xmlUpdatedOn = DatatypeFactory.newInstance().newXMLGregorianCalendar(
				            		gcUpdatedOn);
				            xmlUpdatedOn.setTimezone( DatatypeConstants.FIELD_UNDEFINED );
				        }
			            
			        } catch (DatatypeConfigurationException e) {
			            e.printStackTrace();
			        }
				}
				
				ObjectFactory factory = new ObjectFactory();
				responseWS.setFrontPhoto(factory.createGetCIResponseFrontPhoto(subscriber.getFrontPhotoByt()));
				responseWS.setBackPhoto(factory.createGetCIResponseBackPhoto(subscriber.getBackPhotoByt()));
							
				responseWS.setCi(subscriber.getCiChr());
				responseWS.setBirthDate(xmlBirthDay);
				responseWS.setCity(subscriber.getCityChr());
				responseWS.setAddress(subscriber.getAddressChr());
				responseWS.setSource(subscriber.getSourceChr());
				responseWS.setInsertedOnDate(xmlInsertedOn);
				responseWS.setUpdatedOnDate(xmlUpdatedOn);
				
				log(getCIRequest.getConsumerID(),getClass(), getCIRequest.getSource(), Action.INFO, getOperation(), getCIRequest.toString(), MessageFormat.format(I18nBean.iValue("tigomoney.message.CISuccessfullyRecovered"), 
						getCIRequest.getCi()), GetCIResponseEvent.CI_REGISTERED.getValue(), MessageFormat.format(I18nBean.iValue("tigomoney.message.CISuccessfullyRecovered"), getCIRequest.getCi()));				
			}
			return responseWS;
			
		} catch (GenericFacadeException e) {
			responseWS.setCode(CommonsEvent.INTERNAL_ERROR.getValue());
			responseWS.setDescription(MessageFormat.format(I18nBean.iValue("tigomoney.message.ErrorHasOcurredWhenRecoverCI"), getCIRequest.getCi()));
			responseWS.setTransactionID(getCIRequest.getTransactionID());
			notifier.error(getCIRequest.getConsumerID(),getClass(), getCIRequest.getSource(), MessageFormat.format(I18nBean.iValue("tigomoney.message.ErrorHasOcurredLog"), getOperation().value, getCIRequest.toString()), e);
			return responseWS;
		}catch (Exception e) {
			responseWS.setCode(CommonsEvent.INTERNAL_ERROR.getValue());
			responseWS.setTransactionID(getCIRequest.getTransactionID());
			responseWS.setDescription(MessageFormat.format(I18nBean.iValue("tigomoney.message.ErrorHasOcurredWhenRecoverCI"), getCIRequest.getCi()));
			notifier.error(getCIRequest.getConsumerID(),getClass(), getCIRequest.getSource(), MessageFormat.format(I18nBean.iValue("tigomoney.message.ErrorHasOcurredLog"), getOperation().value, getCIRequest.toString()), e);
			return responseWS;
		}
	}

	@Override
	protected boolean parametersValidated(BaseRequest request) {
		GetCI getCIRequest = (GetCI)request;
		if (getCIRequest == null || getCIRequest.getCi() == null || getCIRequest.getCi().isEmpty()
				|| getCIRequest.getConsumerID() == null ||  getCIRequest.getConsumerID().isEmpty()
				|| getCIRequest.getConsumerPWD() == null || getCIRequest.getConsumerPWD().isEmpty()
				|| getCIRequest.getSource() == null || getCIRequest.getSource().isEmpty()
				|| getCIRequest.getTransactionID() == null || getCIRequest.getTransactionID().isEmpty()){
			return false;
		}
		return true;
	}

	@Override
	protected OperationEvent getOperation() {
		return OperationEvent.GET_CI;
	}	

}
