package com.tigo.cs.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.tigo.cs.api.facade.AbstractAPI;
import com.tigo.cs.commons.util.NumberUtil;
import com.tigo.cs.domain.Userphone;
import com.tigo.cs.facade.UserphoneFacade;

@FacesConverter(value = "userphoneConverter")
public class UserphoneConverter implements Converter {

    private final UserphoneFacade userphoneFacade;

    public UserphoneConverter() {
        userphoneFacade = AbstractAPI.getLocalInstance(UserphoneFacade.class);
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (!NumberUtil.isLong(value)) {
            return null;
        }
        return userphoneFacade.find(Long.valueOf(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Userphone) {
            return ((Userphone) value).getUserphoneCod().toString();
        } else {
            throw new IllegalArgumentException("Object: " + value + " of type:"
                + value.getClass().getName() + "; expected type: Userphone");
        }
    }
}
