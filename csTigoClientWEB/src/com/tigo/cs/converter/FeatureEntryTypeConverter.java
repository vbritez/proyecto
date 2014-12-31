package com.tigo.cs.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.tigo.cs.api.facade.AbstractAPI;
import com.tigo.cs.commons.util.NumberUtil;
import com.tigo.cs.domain.FeatureEntryType;
import com.tigo.cs.facade.FeatureEntryTypeFacade;

@FacesConverter(value = "featureEntryTypeConverter")
public class FeatureEntryTypeConverter implements Converter {

    private final FeatureEntryTypeFacade featureEntryTypeFacade;

    public FeatureEntryTypeConverter() {
        featureEntryTypeFacade = AbstractAPI.getLocalInstance(FeatureEntryTypeFacade.class);
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {

        if (!NumberUtil.isLong(value)) {
            return null;
        }
        FeatureEntryType r = featureEntryTypeFacade.find(Long.parseLong(value));
        return r;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof FeatureEntryType) {
            FeatureEntryType feat = (FeatureEntryType) value;
            return feat.getFeatureEntryTypeCod().toString();
        } else {
            throw new IllegalArgumentException("Object: " + value + " of type:"
                + value.getClass().getName()
                + "; expected type: FeatureEntryType");
        }
    }
}
