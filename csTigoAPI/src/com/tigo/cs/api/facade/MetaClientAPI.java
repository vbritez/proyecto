package com.tigo.cs.api.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.tigo.cs.domain.Client;
import com.tigo.cs.domain.MetaClient;

public abstract class MetaClientAPI extends AbstractAPI<MetaClient> {

    public MetaClientAPI() {
        super(MetaClient.class);
    }

    /**
     * Obtiene un objeto MetaClient dado un cliente y meta.
     * 
     * @param codClient
     *            Código del cliente
     * @param codMeta
     *            Código del meta
     * @return un MetaClient.
     */
    public MetaClient findByMetaAndClient(Long codClient, Long codMeta) {
        try {
            finderParams = prepareParams();
            finderParams.put("codMeta", codMeta);
            finderParams.put("codClient", codClient);
            return findEntityWithNamedQuery(
                    "MetaClient.findByCodMetaAndCodClient", finderParams);
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(getClass(), null,
                    e.getMessage(), null);
            return null;
        }
    }

    /**
     * Obtiene una colección de Metas dado un cliente.
     * 
     * @param codClient
     *            Código del cliente
     * @return una lista de metas asignado al cliente.
     */
    public List<MetaClient> findByClientAndEnabled(Long codClient, Boolean enabled) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("codClient", codClient);
        params.put("enabledChr", enabled);
        return findListWithNamedQuery("MetaClient.findByCodClientAndEnabled",
                params);
    }

    public List<MetaClient> findMetaClientByUserweb(Long userwebCod) {
        return findListWithNamedQuery("Userweb.findMetaClientByUserwebCod",
                addSingleParam("userwebCod", userwebCod));
    }

    public List<MetaClient> getMetaClientList(Client client) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT DISTINCT mc FROM MetaClient mc "
                + " WHERE mc.client.clientCod = :clientCod"
                + " AND mc.enabledChr = true");

            query.setParameter("clientCod", client.getClientCod());
            List<MetaClient> lista = query.getResultList();
            return lista;
        } catch (Exception e) {
            return new ArrayList<MetaClient>();
        }
    }
}
