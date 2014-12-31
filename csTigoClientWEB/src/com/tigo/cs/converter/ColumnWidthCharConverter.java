package com.tigo.cs.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.tigo.cs.view.ApplicationBean;

@FacesConverter(value = "columnWidthCharConverter")
public class ColumnWidthCharConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {

        Integer maxlength = ApplicationBean.getInstance().getColumnWidthChar();

        String string = (String) value;

        if (string.length() > maxlength) {
            return string.substring(0, maxlength);
        } else {
            return string;
        }
    }

}
