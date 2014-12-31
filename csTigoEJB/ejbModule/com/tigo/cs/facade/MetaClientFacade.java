package com.tigo.cs.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.tigo.cs.api.facade.MetaClientAPI;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.commons.log.Action;
import com.tigo.cs.domain.Client;
import com.tigo.cs.domain.Meta;
import com.tigo.cs.domain.MetaClient;
import com.tigo.cs.domain.MetaClientPK;

@DependsOn("EJBFacadeContainer")
@Stateless
public class MetaClientFacade extends MetaClientAPI {

    @EJB
    protected FacadeContainer facadeContainer;

    @EJB
    protected ClientFacade clientFacade;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    public MetaClientFacade() {
    }

    public void associateMetas(List<Meta> selectedMetaList, Client client) {
        try {
            List<Meta> metasExistentes = new ArrayList<Meta>();
            /*
             * Dehabilita en caso necesario, Se recorre la lista metaclient y si
             * el objeto meta presente en la en esta lista existe tambien en la
             * lista de metas asociado en este instante entonces, se asigna 1
             * (estado habilitado), sino 0 deshabilitado
             */
            client = clientFacade.find(client.getClientCod());
            for (MetaClient metaClient : client.getMetaClientList()) {
                if (!selectedMetaList.contains(metaClient.getMeta())) {
                    metaClient.setEnabledChr(false);
                } else {
                    metaClient.setEnabledChr(true);
                    metasExistentes.add(metaClient.getMeta());
                }
                edit(metaClient);
            }

            /*
             * Se agrega un meta en caso de una nueva asociaci�n Cuando el
             * cliente es nuevo, los metas se agregan siempre a la colecci�n
             * metaclient Si es una actualizaci�n, se agrega a la colecci�n los
             * que no existen
             */
            for (Meta meta : selectedMetaList) {
                MetaClient mc = new MetaClient();
                mc.setEnabledChr(true);
                if (client.getClientCod() != null
                    && !metasExistentes.contains(meta)) {
                    mc.setMetaClientPK(new MetaClientPK(meta.getMetaCod(), client.getClientCod()));
                    mc.setClient(client);
                    mc.setMeta(meta);
                    client.getMetaClientList().add(mc);
                } else if (client.getClientCod() == null) {
                    mc.setMeta(meta);
                    client.getMetaClientList().add(mc);
                }
            }

            clientFacade.edit(client);
        } catch (Exception e) {
            getFacadeContainer().getNotifier().log(getClass(), null,
                    Action.ERROR, e.getMessage());
        }
    }
}
