package com.tigo.cs.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.tigo.cs.domain.view.DataClient;
import com.tigo.cs.facade.view.DataClientFacade;
import com.tigo.cs.security.SecurityBean;

/**
 *
 * @author Tania Nuñez
 * @version $Id$
 */
@FacesConverter(value = "dataclientConverter")
public class DataClientConverter implements Converter {

    private final DataClientFacade dataFacade;

    public DataClientConverter() {
    	dataFacade = DataClientFacade.getLocalInstance(DataClientFacade.class);
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().equals("")) {
		    return null;
		}
        DataClient data = dataFacade.findEntityByClientMetaCodigo(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(), 1L, value);
		if (data == null && value != null){
			DataClient dc = new DataClient();
			dc.setCliente(value);
			return dc;
		}else
			return data;
//        return dataFacade.findEntityByClientMetaCodigo(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(), 1L, value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null || value.equals("")) {
            return null;
        }
        if (value instanceof DataClient) {
        	DataClient meta = (DataClient) value;
            return meta.getDataPK().getCodigo();
        } else {
            throw new IllegalArgumentException("Object: " + value + " of type:" + value.getClass().getName() + "; expected type: DataClient");
        }
    }
}
