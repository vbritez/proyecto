package com.tigo.cs.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.tigo.cs.api.facade.AbstractAPI;
import com.tigo.cs.commons.util.NumberUtil;
import com.tigo.cs.domain.RoleClient;
import com.tigo.cs.facade.RoleClientFacade;

@FacesConverter(value = "roleclientConverter")
public class RoleClientConverter implements Converter {

    private final RoleClientFacade roleclientFacade;

    public RoleClientConverter() {
        roleclientFacade = AbstractAPI.getLocalInstance(RoleClientFacade.class);
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {

        if (!NumberUtil.isLong(value)) {
            return null;
        }
        return roleclientFacade.find(Long.parseLong(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof RoleClient) {
            RoleClient role = (RoleClient) value;
            return role.getRoleClientCod().toString();
        } else {
            throw new IllegalArgumentException("Object: " + value + " of type:"
                + value.getClass().getName() + "; expected type: RoleClient");
        }
    }
}
