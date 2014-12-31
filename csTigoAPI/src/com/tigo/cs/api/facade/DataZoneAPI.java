package com.tigo.cs.api.facade;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.tigo.cs.domain.view.DataZone;

/**
 *
 * @author Miguel Zorrilla
 */
public abstract class DataZoneAPI extends AbstractAPI<DataZone> {

    public DataZoneAPI() {
        super(DataZone.class);
    }

    public List<DataZone> findByClientMetaCodigo(Long codClient, Long codMeta, String codigo) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery(""
                    + " SELECT dz "
                    + " FROM DataZone dz " 
                    + " WHERE dz.dataPK.codClient = :clientcod "
                    + " AND dz.dataPK.codMeta = :metaCod "
                    + " AND dz.dataPK.codigo = :codigo ");
            query.setParameter("clientcod", codClient);
            query.setParameter("metaCod", codMeta);
            query.setParameter("codigo", codigo);
            return (List<DataZone>)query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }
    
    public DataZone findEntityByClientMetaCodigo(Long codClient, Long codMeta, String codigo) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery(""
                    + " SELECT dz "
                    + " FROM DataZone dz " 
                    + " WHERE dz.dataPK.codClient = :clientcod "
                    + " AND dz.dataPK.codMeta = :metaCod "
                    + " AND dz.dataPK.codigo = :codigo ");
            query.setParameter("clientcod", codClient);
            query.setParameter("metaCod", codMeta);
            query.setParameter("codigo", codigo);
            return (DataZone)query.getSingleResult();
        }catch (NoResultException e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    
    public List<DataZone> findByClientMetaClientName(Long codClient, Long codMeta, String zoneName) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery(""
                    + " SELECT dz "
                    + " FROM DataZone dz " 
                    + " WHERE dz.dataPK.codClient = :clientcod "
                    + " AND dz.dataPK.codMeta = :metaCod "
                    + " AND lower(dz.descripcion) like '%".concat(zoneName.toLowerCase()).concat("%' "));
            query.setParameter("clientcod", codClient);
            query.setParameter("metaCod", codMeta);
            return query.getResultList();
        }catch (NoResultException e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }
}
