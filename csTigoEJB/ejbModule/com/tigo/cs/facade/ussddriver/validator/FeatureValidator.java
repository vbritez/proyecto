package com.tigo.cs.facade.ussddriver.validator;

import java.util.HashMap;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import py.com.lothar.ussddriverinterfaces.response.ValidationResponse;
import py.com.lothar.ussddriverinterfaces.validator.ValidationDriverInterface;

import com.tigo.cs.api.entities.DynamicFormService;
import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.exception.OperationNotAllowedException;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.api.service.AbstractFeatureBasicServiceAPI;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;

@Stateless
public class FeatureValidator extends AbstractFeatureBasicServiceAPI<DynamicFormService> implements ValidationDriverInterface {

    @EJB
    private FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    public FeatureValidator() {
    }

    @Override
    public DynamicFormService getEntity() {
        if (super.getEntity() == null) {
            super.setEntity(new DynamicFormService());
        }
        return super.getEntity();
    }

    @Override
    public ValidationResponse validate(String msisdn, HashMap<String, HashMap<String, String>> hm) {
        setNodes(hm);
        setEntity(null);
        setFeatureElement(null);
        setClientFeature(null);
        setPhone(null);
        setClient(null);
        setCellphoneNumber(null);

        getEntity().setMsisdn(msisdn);
        getEntity().setGrossMessage(hm.toString());
        setGrossMessageIn(getEntity().getGrossMessage());
        init(getEntity());
        try {
            validate();
            return returnValidationResponse(
                    true,
                    getFacadeContainer().getI18nAPI().iValue(
                            "message.ussd.validator.ValidRequest"),
                    getUserCode());

        } catch (InvalidOperationException e) {
            return returnValidationResponse(false, e.getMessage(), null);
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(FeatureValidator.class,
                    null, e.getMessage(), e, false);
            return returnValidationResponse(
                    false,
                    getFacadeContainer().getI18nAPI().iValue(
                            "message.ussd.validator.GenericProblem"), null);
        }
    }

    @Override
    protected void validate() throws InvalidOperationException {
        if (getNodeValue("SHORTCUT_CODE") != null) {
            super.validate();
        }
    }

    private ValidationResponse returnValidationResponse(boolean isOk, String message, String userCode) {
        return new ValidationResponse(isOk, userCode, message);
    }

    @Override
    protected void assignEvent() {
    }

    @Override
    public String processService() throws GenericFacadeException, com.tigo.cs.api.exception.InvalidOperationException, OperationNotAllowedException {
        return null;
    }

    @Override
    protected ServiceValue treatHeader() {
        return null;
    }

    @Override
    protected List<ServiceValueDetail> treatDetails(ServiceValue serviceValue) {
        return null;
    }

    @Override
    public void convertToBean() {
    }

}
