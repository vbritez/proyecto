package com.tigo.cs.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.tigo.cs.api.facade.AbstractAPI;
import com.tigo.cs.commons.util.NumberUtil;
import com.tigo.cs.domain.FeatureElement;
import com.tigo.cs.facade.FeatureElementFacade;

@FacesConverter(value = "featureElementConverter")
public class FeatureElementConverter implements Converter {

    private final FeatureElementFacade featureElementFacade;

    public FeatureElementConverter() {
        featureElementFacade = AbstractAPI.getLocalInstance(FeatureElementFacade.class);
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {

        if (!NumberUtil.isLong(value)) {
            return null;
        }
        return featureElementFacade.find(Long.parseLong(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof FeatureElement) {
            FeatureElement feat = (FeatureElement) value;
            return feat.getFeatureElementCod().toString();
        } else {
            throw new IllegalArgumentException("Object: " + value + " of type:"
                + value.getClass().getName()
                + "; expected type: FeatureElement");
        }
    }
}
