package com.tigo.cs.converter;

import java.util.StringTokenizer;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.tigo.cs.domain.MetaMember;
import com.tigo.cs.domain.MetaMemberPK;
import com.tigo.cs.facade.MetaMemberFacade;

@FacesConverter(value = "metamemberConverter")
public class MetaMemberConverter implements Converter {

    private final MetaMemberFacade metamemberFacade;

    public MetaMemberConverter() {
        metamemberFacade = MetaMemberFacade.getLocalInstance(MetaMemberFacade.class);
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        MetaMemberPK pk = new MetaMemberPK();
        StringTokenizer stkn = new StringTokenizer(value, "|");
        pk.setCodMeta(Long.parseLong(stkn.nextToken()));
        pk.setMemberCod(Long.parseLong(stkn.nextToken()));

        return metamemberFacade.find(pk);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof MetaMember) {
            return ((MetaMember) value).getMetaMemberPK().getCodMeta() + "|"
                + ((MetaMember) value).getMetaMemberPK().getMemberCod();
        } else {
            throw new IllegalArgumentException("Object: " + value + " of type:"
                + value.getClass().getName() + "; expected type: MetaMember");
        }
    }
}
