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
import com.tigo.cs.domain.DataCheck;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;
import com.tigo.cs.domain.ussd.UssdMenuEntry;

@Stateless
public class DataCheckValidator extends AbstractServiceAPI<BaseServiceBean> implements ValidationDriverInterface {

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
                    getUserCode());
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
        if (getNodes() != null && !getNodes().keySet().isEmpty()) {
            for (String entrada : getNodes().keySet()) {
                /*
                 * validacion entrada
                 */
                DataCheck dataCheck = getFacadeContainer().getUssdMenuEntryAPI().findDataCheckByEntryCode(
                        entrada, getUserphone().getClient());
                UssdMenuEntry menuEntry = getFacadeContainer().getUssdMenuEntryAPI().findByCode(
                        entrada);
                String value = getNodeValue(entrada);
                if (dataCheck != null) {
                    validateEntry(menuEntry.getDescription(), value, dataCheck);
                }
            }

        }
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
