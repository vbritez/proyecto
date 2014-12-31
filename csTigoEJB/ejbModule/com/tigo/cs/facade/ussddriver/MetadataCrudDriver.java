package com.tigo.cs.facade.ussddriver;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import py.com.lothar.ussddriverinterfaces.driver.DriverInterface;

import com.tigo.cs.api.entities.MetadataCrudService;
import com.tigo.cs.api.enumerate.ServiceIdentifier;
import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.api.service.MetadataCrudServiceAPI;
import com.tigo.cs.commons.log.Action;
import com.tigo.cs.domain.Meta;

/***
 * 
 * Driver para guardar el metadata creado por el cliente desde USSD.
 * 
 * 
 * @author exttnu
 * 
 */

@Stateless
@ServiceIdentifier(id = ServiceIdentifier.Id.DATOS)
public class MetadataCrudDriver extends MetadataCrudServiceAPI<MetadataCrudService> implements DriverInterface {

    private static final String SC_DATA = "SC_DATA";
    private static final String METACODE_POS = "METACODE_POS";
    private static final String VALOR = "VALOR";
    private static final String CODIGO = "CODIGO";
    private static final String FIND_BY = "FIND_BY";
    private static final String CONSULTA_CODIGO = "CODIGO";
    private static final String CONSULTA_NOMBRE = "NOMBRE";

    @EJB
    private FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    @Override
    public void convertToBean() {
        getEntity().setValue(getNodeValue(VALOR));
        getEntity().setCode(getNodeValue(CODIGO));
    }

    @Override
    protected void assignEvent() throws InvalidOperationException {
        try {
            Integer pos = Integer.parseInt(getNodeValue(METACODE_POS));
            List<Meta> metaList = null;

            String discriminator = getNodeValue(SC_DATA);
            if (discriminator.equals(MetaCrudEvent.CREATE.getDescription())) {
                tranType = MetaCrudEvent.CREATE;
                metaList = facadeContainer.getUserphoneMetaAPI().getUserphoneMetaList(
                        getUserphone(), true, false, false);
                getEntity().setEvent(MetaCrudEvent.CREATE.getDescription());
            } else if (discriminator.equals(MetaCrudEvent.UPDATE.getDescription())) {
                tranType = MetaCrudEvent.UPDATE;
                metaList = facadeContainer.getUserphoneMetaAPI().getUserphoneMetaList(
                        getUserphone(), false, true, false);
                getEntity().setEvent(MetaCrudEvent.UPDATE.getDescription());
            } else if (discriminator.equals(MetaCrudEvent.DELETE.getDescription())) {
                tranType = MetaCrudEvent.DELETE;
                metaList = facadeContainer.getUserphoneMetaAPI().getUserphoneMetaList(
                        getUserphone(), false, false, true);
                getEntity().setEvent(MetaCrudEvent.DELETE.getDescription());
            } else if (discriminator.equals(MetaCrudEvent.READ.getDescription())) {
                tranType = MetaCrudEvent.READ;
                metaList = getFacadeContainer().getMetaAPI().findMetaByClientAndEnabled(
                        getClient().getClientCod(), true);
                getEntity().setEvent(MetaCrudEvent.READ.getDescription());
                discriminator = getNodeValue(FIND_BY);
                if (discriminator != null
                    && discriminator.equals(CONSULTA_CODIGO)) {
                    getEntity().setEvent("C");
                } else if (discriminator.equals(CONSULTA_NOMBRE)) {
                    getEntity().setEvent("N");
                }
            }
            Meta meta = metaList.get(pos - 1);
            getEntity().setMetaCode(meta.getMetaCod().intValue());

        } catch (Exception e) {
            getFacadeContainer().getNotifier().log(getClass(), null,
                    Action.ERROR, e.getMessage());
            String msg = getFacadeContainer().getI18nAPI().iValue(
                    "error.DispatchmentGeneralError");
            throw new InvalidOperationException(msg);
        }
    }
}
