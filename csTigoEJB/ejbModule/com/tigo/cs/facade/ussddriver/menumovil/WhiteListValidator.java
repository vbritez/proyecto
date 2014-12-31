package com.tigo.cs.facade.ussddriver.menumovil;

import java.util.HashMap;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import py.com.lothar.ussddriverinterfaces.response.ValidationResponse;
import py.com.lothar.ussddriverinterfaces.validator.ValidationDriverInterface;

import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.facade.ussddriver.FeatureEntryDynamicDriver;

@Stateless
public class WhiteListValidator implements ValidationDriverInterface {

    @EJB
    private FacadeContainer facadeContainer;

    @Override
    public ValidationResponse validate(String msisdn, HashMap<String, HashMap<String, String>> nodes) {

        ValidationResponse response = new ValidationResponse(false, null, "El servicio se encuentra en mantenimiento y estara disponible en breve.");

        try {
            String whiteList = facadeContainer.getGlobalParameterAPI().findByCode(
                    "menumovil.ips.whitelist");

            if (whiteList != null && whiteList.compareTo("0") == 0) {
                return new ValidationResponse(true, null, "Paso validacion");
            }

            Long ussdUser = facadeContainer.getUssdUserAPI().findByCode(msisdn);

            if (ussdUser == null) {
                return response;
            } else {
                return new ValidationResponse(true, null, "Paso validacion");
            }

        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setOk(false);
            facadeContainer.getNotifier().error(
                    FeatureEntryDynamicDriver.class, null, e.getMessage(), e);
            return response;
        }

    }

}
