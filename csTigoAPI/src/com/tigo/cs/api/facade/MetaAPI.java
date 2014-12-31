package com.tigo.cs.api.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.Meta;

public abstract class MetaAPI extends AbstractAPI<Meta> {

    public MetaAPI() {
        super(Meta.class);
    }

    /**
     * Obtiene una colección de Metas dado un cliente.
     * 
     * @param codClient
     *            Código del cliente
     * @return una lista de metas asignado al cliente.
     */
    public List<Meta> findMetaByClient(Long codClient) {
        return findListWithNamedQuery("MetaClient.findMetaByCodClient",
                this.addSingleParam("codClient", codClient));
    }

    /**
     * Obtiene una colección de Metas dado un cliente.
     * 
     * @param codClient
     *            Código del cliente
     * @return una lista de metas asignado al cliente.
     */
    public List<Meta> findMetaByClientAndEnabled(Long codClient, Boolean enabled) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("codClient", codClient);
        params.put("enabledChr", enabled);
        return findListWithNamedQuery(
                "MetaClient.findMetaByCodClientAndEnabled", params);
    }

    public List<Long> getMetadataCodByUserphone(Long clientCod) throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery("" + " SELECT DISTINCT mc.meta.metaCod "
                + " FROM MetaClient mc "
                + " WHERE mc.client.clientCod = :clientCod"
                + "   AND mc.enabledChr = true");
            q.setParameter("clientCod", clientCod);

            List<Long> result = q.getResultList();
            return result;
        } catch (NoResultException e) {
            return new ArrayList<Long>();
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), "Cannot obtain list of metadata by client from facade.", e);
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

}
