package com.tigo.cs.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.tigo.cs.api.facade.AbstractAPI;
import com.tigo.cs.commons.util.NumberUtil;
import com.tigo.cs.domain.PhoneList;
import com.tigo.cs.facade.PhoneListFacade;

@FacesConverter(value = "phonelistConverter")
public class PhoneListConverter implements Converter {

    private final PhoneListFacade phoneListFacade;

    public PhoneListConverter() {
        phoneListFacade = AbstractAPI.getLocalInstance(PhoneListFacade.class);
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {

        if (!NumberUtil.isLong(value)) {
            return null;
        }
        return phoneListFacade.find(Long.parseLong(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof PhoneList) {
            PhoneList pList = (PhoneList) value;
            return pList.getPhoneListCod().toString();
        } else {
            throw new IllegalArgumentException("Object: " + value + " of type:"
                + value.getClass().getName() + "; expected type: PhoneList");
        }
    }
}
