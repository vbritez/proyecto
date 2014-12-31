package com.tigo.cs.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.tigo.cs.domain.view.DataStatus;
import com.tigo.cs.facade.view.DataStatusFacade;
import com.tigo.cs.security.SecurityBean;

/**
 *
 * @author Tania Nuñez
 * @version $Id$
 */
@FacesConverter(value = "datastatusConverter")
public class DataStatusConverter implements Converter {

    private final DataStatusFacade dataFacade;

    public DataStatusConverter() {
    	dataFacade = DataStatusFacade.getLocalInstance(DataStatusFacade.class);
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null) {
		    return null;
		}
		return dataFacade.findEntityByClientMetaCodigo(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(), 12L, value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof DataStatus) {
        	DataStatus meta = (DataStatus) value;
            return meta.getDataPK().getCodigo();
        } else {
            throw new IllegalArgumentException("Object: " + value + " of type:" + value.getClass().getName() + "; expected type: DataStatus");
        }
    }
}
