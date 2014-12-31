package com.tigo.cs.converter;

import java.math.BigInteger;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.tigo.cs.commons.util.NumberUtil;
import com.tigo.cs.commons.util.StringUtils;

@FacesConverter(value = "cellphoneConverter")
public class CellphoneConverter implements Converter {

    public static final int CELLPHONE_NUM_LENGTH = 12;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (!NumberUtil.isBigInteger(StringUtils.removeSpaces(value))) {
            return null;
        }
        if (StringUtils.removeSpaces(value).length() != CELLPHONE_NUM_LENGTH) {
            return null;
        }
        return new BigInteger(StringUtils.removeSpaces(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String
            && NumberUtil.isBigInteger(StringUtils.removeSpaces(value.toString()))) {
            value = new BigInteger(StringUtils.removeSpaces(value.toString()));
        }
        if (value instanceof BigInteger) {
            String stringVal = value.toString();

            if (stringVal.length() < CELLPHONE_NUM_LENGTH) {
                return "595 9";
            }

            stringVal = stringVal.substring(0, 3).concat(" ").concat(
                    stringVal.substring(3, 6)).concat(" ").concat(
                    stringVal.substring(6));

            return stringVal;
        } else {
            throw new IllegalArgumentException("Object: " + value + " of type:"
                + value.getClass().getName() + "; expected type: BigInteger");
        }
    }
}
