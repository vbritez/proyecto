package com.tigo.cs.converter;

import java.util.LinkedList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.tigo.cs.domain.IconType;
import com.tigo.cs.facade.IconTypeFacade;

@FacesConverter(value = "iconTypeConverter")
public class IconTypeConverter implements Converter {

public final IconTypeFacade iconTypeFacade;
public static List<IconType> iconTypeList = new LinkedList<IconType>();

public IconTypeConverter() {
	iconTypeFacade = IconTypeFacade.getLocalInstance(IconTypeFacade.class);
	iconTypeList = iconTypeFacade.findAll();
}

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
    	return iconTypeFacade.find(Long.valueOf(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
    	if (value == null) {
            return null;
        }
        if (value instanceof IconType) {
            return ((IconType) value).getIconTypeCod().toString();
        } else {
            throw new IllegalArgumentException("Object: " + value + " of type:"
                + value.getClass().getName() + "; expected type: IconType");
        }
    }
}
