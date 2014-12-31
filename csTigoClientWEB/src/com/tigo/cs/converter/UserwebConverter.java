package com.tigo.cs.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.tigo.cs.api.facade.AbstractAPI;
import com.tigo.cs.commons.util.NumberUtil;
import com.tigo.cs.domain.Userweb;
import com.tigo.cs.facade.UserwebFacade;

@FacesConverter(value = "userwebConverter")
public class UserwebConverter implements Converter {

    private final UserwebFacade userwebFacade;

    public UserwebConverter() {
        userwebFacade = AbstractAPI.getLocalInstance(UserwebFacade.class);
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (!NumberUtil.isLong(value)) {
            return null;
        }
        return userwebFacade.find(Long.valueOf(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Userweb) {
            return ((Userweb) value).getUserwebCod().toString();
        } else {
            throw new IllegalArgumentException("Object: " + value + " of type:"
                + value.getClass().getName() + "; expected type: Userweb");
        }
    }
}
