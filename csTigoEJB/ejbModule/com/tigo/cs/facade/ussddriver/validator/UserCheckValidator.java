package com.tigo.cs.facade.ussddriver.validator;

import java.util.HashMap;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import py.com.lothar.ussddriverinterfaces.response.ValidationResponse;
import py.com.lothar.ussddriverinterfaces.validator.ValidationDriverInterface;

import com.tigo.cs.api.entities.BaseServiceBean;
import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.exception.OperationNotAllowedException;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.api.service.AbstractServiceAPI;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;

@Stateless
public class UserCheckValidator extends AbstractServiceAPI<BaseServiceBean> implements ValidationDriverInterface {

    @EJB
    private FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    @Override
    public ValidationResponse validate(String msisdn, HashMap<String, HashMap<String, String>> hm) {
        try {
            setNodes(hm);
            init(msisdn, hm.toString());
            validate();
            return returnValidationResponse(
                    true,
                    getFacadeContainer().getI18nAPI().iValue(
                            "check.datatype.message.SuccessfulValidation"),
                    null);
        } catch (InvalidOperationException e) {
            return returnValidationResponse(false, e.getMessage(), null);
        }
    }

    private ValidationResponse returnValidationResponse(boolean isOk, String message, String userCode) {
        return new ValidationResponse(isOk, userCode, message);
    }

    @Override
    protected void validate() throws InvalidOperationException {
        /*
         * inicialmente la entrada no pasa ninguna validacion y al no cumplir
         * con alguna validacion se muestra el mensaje del DataCheck
         */
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
