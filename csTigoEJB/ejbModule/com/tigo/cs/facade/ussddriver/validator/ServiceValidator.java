package com.tigo.cs.facade.ussddriver.validator;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import py.com.lothar.ussddriverinterfaces.response.ValidationResponse;
import py.com.lothar.ussddriverinterfaces.validator.ValidationDriverInterface;

import com.tigo.cs.api.entities.BaseServiceBean;
import com.tigo.cs.api.enumerate.ServiceIdentifier;
import com.tigo.cs.api.enumerate.ServiceIdentifier.Id;
import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.exception.OperationNotAllowedException;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.api.service.AbstractServiceAPI;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;

@Stateless
@ServiceIdentifier(id = Id.PERMISSION)
public class ServiceValidator extends AbstractServiceAPI<BaseServiceBean> implements ValidationDriverInterface {

    private static final String CELL_ID = "CELL_ID";
    private static final String LAC = "LAC";

    @EJB
    private FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    public ServiceValidator() {
        setEntity(new BaseServiceBean());
    }

    @Override
    public ValidationResponse validate(String msisdn, HashMap<String, HashMap<String, String>> hm) {

        try {
            setNodes(hm);
            setEntity(null);
            getEntity().setMsisdn(msisdn);
            getEntity().setGrossMessage(hm.toString());
            setGrossMessageIn(getEntity().getGrossMessage());
            setCellphoneNumber(new BigInteger(msisdn));
            getEntity().setApplicationKey("u55d");
            Integer cellIDUSSD = null;
            Integer lacUSSD = null;
            if (getNodeValue(CELL_ID) != null) {
                cellIDUSSD = Integer.parseInt(getNodeValue(CELL_ID));
            }
            if (getNodeValue(LAC) != null) {
                lacUSSD = Integer.parseInt(getNodeValue(LAC));
            }
            if (cellIDUSSD != null && getEntity().getCellId() == null) {
                getEntity().setCellId(cellIDUSSD);
            }
            if (lacUSSD != null && getEntity().getLac() == null) {
                getEntity().setLac(lacUSSD);
            }
            init();
            validate();

            return returnValidationResponse(true, getUserphone().getNameChr(),
                    getUserCode());

        } catch (InvalidOperationException ex) {
            getFacadeContainer().getNotifier().error(getClass(), null,
                    ex.getMessage(), ex);
            return returnValidationResponse(false, ex.getMessage(), null);
        } catch (OperationNotAllowedException e) {
            getFacadeContainer().getNotifier().error(getClass(), null,
                    e.getMessage(), e);
            return returnValidationResponse(false, e.getMessage(), null);
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(getClass(), null,
                    e.getMessage(), e);
            return returnValidationResponse(
                    false,
                    getFacadeContainer().getI18nAPI().iValue(
                            "restriction.GenericError"), null);
        }

    }

    @Override
    public BaseServiceBean getEntity() {
        if (super.getEntity() == null) {
            setEntity(new BaseServiceBean());
        }
        return super.getEntity();
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
