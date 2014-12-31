package com.tigo.cs.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.tigo.cs.domain.view.DataIcon;
import com.tigo.cs.facade.view.DataIconFacade;
import com.tigo.cs.security.SecurityBean;

/**
 *
 * @author Tania Nuñez
 * @version $Id$
 */
@FacesConverter(value = "dataiconConverter")
public class DataIconConverter implements Converter {

    private final DataIconFacade dataFacade;

    public DataIconConverter() {
    	dataFacade = DataIconFacade.getLocalInstance(DataIconFacade.class);
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null) {
		    return null;
		}
		return dataFacade.findEntityByClientMetaCodigo(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(), 21L, value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof DataIcon) {
        	DataIcon meta = (DataIcon) value;
            return meta.getDataPK().getCodigo();
        } else {
            throw new IllegalArgumentException("Object: " + value + " of type:" + value.getClass().getName() + "; expected type: DataIcon");
        }
    }
}
