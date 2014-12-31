package com.tigo.cs.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.tigo.cs.commons.util.NumberUtil;
import com.tigo.cs.domain.ClientGoal;
import com.tigo.cs.facade.ClientGoalFacade;

@FacesConverter(value = "clientGoalConverter")
public class ClientGoalConverter implements Converter {

    private final ClientGoalFacade clientGoalFacade;

    public ClientGoalConverter() {
        clientGoalFacade = ClientGoalFacade.getLocalInstance(ClientGoalFacade.class);
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (!NumberUtil.isLong(value)) {
            return null;
        }
        return clientGoalFacade.find(Long.valueOf(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof ClientGoal) {
            return ((ClientGoal) value).getClientGoalCod().toString();
        } else {
            throw new IllegalArgumentException("Object: " + value + " of type:"
                + value.getClass().getName() + "; expected type: ClientGoal");
        }
    }
}
