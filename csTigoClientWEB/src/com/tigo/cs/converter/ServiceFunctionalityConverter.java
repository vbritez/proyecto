package com.tigo.cs.converter;

import java.util.StringTokenizer;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.tigo.cs.api.facade.AbstractAPI;
import com.tigo.cs.domain.ServiceFunctionality;
import com.tigo.cs.domain.ServiceFunctionalityPK;
import com.tigo.cs.facade.ServiceFunctionalityFacade;

@FacesConverter(value = "serviceFunctionalityConverter")
public class ServiceFunctionalityConverter implements Converter {

    private final ServiceFunctionalityFacade sfFacade;

    public ServiceFunctionalityConverter() {
        sfFacade = AbstractAPI.getLocalInstance(ServiceFunctionalityFacade.class);
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        ServiceFunctionalityPK pk = new ServiceFunctionalityPK();
        StringTokenizer stkn = new StringTokenizer(value, "|");
        pk.setCodService(Long.parseLong(stkn.nextToken()));
        pk.setCodFunctionality(Long.parseLong(stkn.nextToken()));

        return sfFacade.find(pk);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof ServiceFunctionality) {
            return ((ServiceFunctionality) value).getServiceFunctionalityPK().getCodService()
                + "|"
                + ((ServiceFunctionality) value).getServiceFunctionalityPK().getCodFunctionality();
        } else {
            throw new IllegalArgumentException("Object: " + value + " of type:"
                + value.getClass().getName()
                + "; expected type: ServiceFunctionality");
        }
    }
}
