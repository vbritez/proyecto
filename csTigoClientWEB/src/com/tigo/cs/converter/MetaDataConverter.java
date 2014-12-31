package com.tigo.cs.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.tigo.cs.commons.util.NumberUtil;
import com.tigo.cs.domain.MetaData;
import com.tigo.cs.facade.MetaDataFacade;
import com.tigo.cs.security.SecurityBean;

@FacesConverter(value = "metadataConverter")
public class MetaDataConverter implements Converter {

    private final MetaDataFacade metadataFacade;

    public MetaDataConverter() {
        metadataFacade = MetaDataFacade.getLocalInstance(MetaDataFacade.class);
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        try {
            if (!NumberUtil.isLong(value)) {
                return null;
            }
            return metadataFacade.findByClientMetaMemberAndCode(
                    SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(),
                    7L, 1L, value);
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof MetaData) {
            MetaData meta = (MetaData) value;
            return meta.getMetaDataPK().getCodeChr();
        } else {
            throw new IllegalArgumentException("Object: " + value + " of type:"
                + value.getClass().getName() + "; expected type: MetaData");
        }
    }
}
