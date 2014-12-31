package com.tigo.cs.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.tigo.cs.api.facade.AbstractAPI;
import com.tigo.cs.commons.util.NumberUtil;
import com.tigo.cs.domain.Classification;
import com.tigo.cs.facade.ClassificationFacade;

@FacesConverter(value = "classificationConverter")
public class ClassificationConverter implements Converter {

    private final ClassificationFacade classificationFacade;

    public ClassificationConverter() {
        classificationFacade = AbstractAPI.getLocalInstance(ClassificationFacade.class);
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (!NumberUtil.isLong(value)) {
            return null;
        }
        return classificationFacade.find(Long.valueOf(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Classification) {
            return ((Classification) value).getClassificationCod().toString();
        } else {
            throw new IllegalArgumentException("Object: " + value + " of type:"
                + value.getClass().getName()
                + "; expected type: Classification");
        }
    }
}
