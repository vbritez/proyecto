package com.tigo.cs.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.tigo.cs.commons.util.NumberUtil;
import com.tigo.cs.domain.Functionality;
import com.tigo.cs.facade.FunctionalityFacade;

@FacesConverter(value = "functionalityConverter")
public class FunctionalityConverter implements Converter {

    private final FunctionalityFacade functionalityFacade;

    public FunctionalityConverter() {
        functionalityFacade = FunctionalityFacade.getLocalInstance(FunctionalityFacade.class);
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (!NumberUtil.isLong(value)) {
            return null;
        }
        return functionalityFacade.find(Long.valueOf(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Functionality) {
            return ((Functionality) value).getFunctionalityCod().toString();
        } else {
            throw new IllegalArgumentException("Object: " + value + " of type:"
                + value.getClass().getName() + "; expected type: Functionality");
        }
    }
}
