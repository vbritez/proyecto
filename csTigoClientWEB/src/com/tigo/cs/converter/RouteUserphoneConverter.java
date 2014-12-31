package com.tigo.cs.converter;

import java.util.StringTokenizer;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.tigo.cs.api.facade.AbstractAPI;
import com.tigo.cs.domain.RouteUserphone;
import com.tigo.cs.domain.RouteUserphonePK;
import com.tigo.cs.domain.ServiceFunctionality;
import com.tigo.cs.facade.RouteUserphoneFacade;

@FacesConverter(value = "routeUserphoneConverter")
public class RouteUserphoneConverter implements Converter {

    private final RouteUserphoneFacade routeUserphoneFacade;

    public RouteUserphoneConverter() {
        routeUserphoneFacade = AbstractAPI.getLocalInstance(RouteUserphoneFacade.class);
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        RouteUserphonePK pk = new RouteUserphonePK();
        StringTokenizer stkn = new StringTokenizer(value, "|");
        pk.setCodRoute(Long.parseLong(stkn.nextToken()));
        pk.setCodUserphone(Long.parseLong(stkn.nextToken()));

        return routeUserphoneFacade.find(pk);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof ServiceFunctionality) {
            return ((RouteUserphone) value).getRouteUserphonePK().getCodRoute()
                + "|"
                + ((RouteUserphone) value).getRouteUserphonePK().getCodUserphone();
        } else {
            throw new IllegalArgumentException("Object: " + value + " of type:"
                + value.getClass().getName()
                + "; expected type: RouteUserphone");
        }
    }
}
