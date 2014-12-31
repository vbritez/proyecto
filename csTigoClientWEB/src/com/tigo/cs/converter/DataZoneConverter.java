package com.tigo.cs.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.tigo.cs.domain.view.DataZone;
import com.tigo.cs.facade.view.DataZoneFacade;
import com.tigo.cs.security.SecurityBean;

/**
 *
 * @author Tania Nuñez
 * @version $Id$
 */
@FacesConverter(value = "datazoneConverter")
public class DataZoneConverter implements Converter {

    private final DataZoneFacade dataFacade;

    public DataZoneConverter() {
    	dataFacade = DataZoneFacade.getLocalInstance(DataZoneFacade.class);
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().equals("")) {
		    return null;
		}
		return dataFacade.findEntityByClientMetaCodigo(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(), 13L, value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null || value.equals("")) {
            return null;
        }
        if (value instanceof DataZone) {
        	DataZone meta = (DataZone) value;
            return meta.getDataPK().getCodigo();
        } else {
            throw new IllegalArgumentException("Object: " + value + " of type:" + value.getClass().getName() + "; expected type: DataZone");
        }
    }
}
