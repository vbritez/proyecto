package com.tigo.cs.api.facade;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.tigo.cs.domain.view.DataAttendant;

/**
 *
 * @author Miguel Zorrilla
 */
public abstract class DataAttendantAPI extends AbstractAPI<DataAttendant> {

    public DataAttendantAPI() {
        super(DataAttendant.class);
    }

    public DataAttendant findByClientMetaCodigo(Long codClient, Long codMeta, String username) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery(""
                    + " SELECT da "
                    + " FROM DataAttendant da "
                    + " WHERE da.dataPK.codClient = :clientcod "
                    + " AND da.dataPK.codMeta = :metaCod "
                    + " AND da.nombreUsuario = :username");
            query.setParameter("clientcod", codClient);
            query.setParameter("metaCod", codMeta);
            query.setParameter("username", username);
            return (DataAttendant) query.getSingleResult();

        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                    && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }

    }

}
