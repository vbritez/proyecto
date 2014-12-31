package com.tigo.cs.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.tigo.cs.api.facade.AbstractAPI;
import com.tigo.cs.commons.util.NumberUtil;
import com.tigo.cs.domain.Feature;
import com.tigo.cs.facade.FeatureFacade;

@FacesConverter(value = "featureConverter")
public class FeatureConverter implements Converter {

    private final FeatureFacade featureFacade;

    public FeatureConverter() {
        featureFacade = AbstractAPI.getLocalInstance(FeatureFacade.class);
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {

        if (!NumberUtil.isLong(value)) {
            return null;
        }
        return featureFacade.find(Long.parseLong(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Feature) {
            Feature feat = (Feature) value;
            return feat.getFeatureCode().toString();
        } else {
            throw new IllegalArgumentException("Object: " + value + " of type:"
                + value.getClass().getName() + "; expected type: Feature");
        }
    }
}
