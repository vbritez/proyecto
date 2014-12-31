package com.tigo.cs.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.tigo.cs.api.facade.AbstractAPI;
import com.tigo.cs.commons.util.NumberUtil;
import com.tigo.cs.domain.Screenclient;
import com.tigo.cs.facade.ScreenClientFacade;

@FacesConverter(value = "screenclientConverter")
public class ScreenClientConverter implements Converter {

    private final ScreenClientFacade screenclientFacade;

    public ScreenClientConverter() {
        screenclientFacade = AbstractAPI.getLocalInstance(ScreenClientFacade.class);
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (!NumberUtil.isLong(value)) {
            return null;
        }
        return screenclientFacade.find(Long.valueOf(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Screenclient) {
            return ((Screenclient) value).getScreenclientCod().toString();
        } else {
            throw new IllegalArgumentException("Object: " + value + " of type:"
                + value.getClass().getName() + "; expected type: Screenclient");
        }
    }
}
