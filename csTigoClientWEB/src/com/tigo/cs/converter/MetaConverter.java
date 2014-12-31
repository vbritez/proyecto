package com.tigo.cs.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.tigo.cs.commons.util.NumberUtil;
import com.tigo.cs.domain.Meta;
import com.tigo.cs.facade.MetaFacade;

@FacesConverter(value = "metaConverter")
public class MetaConverter implements Converter {

    private final MetaFacade metaFacade;

    public MetaConverter() {
        metaFacade = MetaFacade.getLocalInstance(MetaFacade.class);
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {

        if (!NumberUtil.isLong(value)) {
            return null;
        }
        return metaFacade.find(Long.parseLong(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Meta) {
            Meta meta = (Meta) value;
            return meta.getMetaCod().toString();
        } else {
            throw new IllegalArgumentException("Object: " + value + " of type:"
                + value.getClass().getName() + "; expected type: Meta");
        }
    }
}
