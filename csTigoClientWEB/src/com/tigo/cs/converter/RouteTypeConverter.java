package com.tigo.cs.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.tigo.cs.api.facade.AbstractAPI;
import com.tigo.cs.commons.util.NumberUtil;
import com.tigo.cs.domain.RouteType;
import com.tigo.cs.facade.RouteTypeFacade;

@FacesConverter(value = "routeTypeConverter")
public class RouteTypeConverter implements Converter {

    private final RouteTypeFacade routeTypeFacade;

    public RouteTypeConverter() {
        routeTypeFacade = AbstractAPI.getLocalInstance(RouteTypeFacade.class);
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (!NumberUtil.isLong(value)) {
            return null;
        }
        return routeTypeFacade.find(Long.valueOf(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof RouteType) {
            return ((RouteType) value).getRouteTypeCod().toString();
        } else {
            throw new IllegalArgumentException("Object: " + value + " of type:"
                + value.getClass().getName() + "; expected type: RouteType");
        }
    }
}
