package com.tigo.cs.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.tigo.cs.domain.view.DataActivity;
import com.tigo.cs.facade.view.DataActivityFacade;
import com.tigo.cs.security.SecurityBean;

/**
 *
 * @author Tania Nuñez
 * @version $Id$
 */
@FacesConverter(value = "dataactivityConverter")
public class DataActivityConverter implements Converter {

    private final DataActivityFacade dataFacade;

    public DataActivityConverter() {
    	dataFacade = DataActivityFacade.getLocalInstance(DataActivityFacade.class);
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null) {
		    return null;
		}
		return dataFacade.findEntityByClientMetaCodigo(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(), 11L, value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof DataActivity) {
        	DataActivity meta = (DataActivity) value;
            return meta.getDataPK().getCodigo();
        } else {
            throw new IllegalArgumentException("Object: " + value + " of type:" + value.getClass().getName() + "; expected type: DataActivity");
        }
    }
}
