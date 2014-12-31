package com.tigo.cs.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.tigo.cs.api.facade.AbstractAPI;
import com.tigo.cs.commons.util.NumberUtil;
import com.tigo.cs.domain.Service;
import com.tigo.cs.facade.ServiceFacade;

@FacesConverter(value = "serviceConverter")
public class ServiceConverter implements Converter {

    private final ServiceFacade serviceFacade;

    public ServiceConverter() {
        serviceFacade = AbstractAPI.getLocalInstance(ServiceFacade.class);
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (!NumberUtil.isLong(value)) {
            return null;
        }
        return serviceFacade.find(Long.valueOf(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Service) {
            return ((Service) value).getServiceCod().toString();
        } else {
            throw new IllegalArgumentException("Object: " + value + " of type:"
                + value.getClass().getName() + "; expected type: Service");
        }
    }
}
