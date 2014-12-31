package com.tigo.cs.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.tigo.cs.api.facade.AbstractAPI;
import com.tigo.cs.commons.util.NumberUtil;
import com.tigo.cs.domain.Phone;
import com.tigo.cs.facade.PhoneFacade;

@FacesConverter(value = "phoneConverter")
public class PhoneConverter implements Converter {

    private final PhoneFacade phoneFacade;

    public PhoneConverter() {
        phoneFacade = AbstractAPI.getLocalInstance(PhoneFacade.class);
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {

        if (!NumberUtil.isLong(value)) {
            return null;
        }
        return phoneFacade.find(Long.parseLong(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Phone) {
            Phone p = (Phone) value;
            return p.getPhoneCod().toString();
        } else {
            throw new IllegalArgumentException("Object: " + value + " of type:"
                + value.getClass().getName() + "; expected type: Phone");
        }
    }
}
