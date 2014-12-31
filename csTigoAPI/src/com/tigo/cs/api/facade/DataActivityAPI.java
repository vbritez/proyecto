package com.tigo.cs.api.facade;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.tigo.cs.domain.view.DataActivity;
import com.tigo.cs.domain.view.DataStatus;

/**
 *
 * @author Miguel Zorrilla
 */
public abstract class DataActivityAPI extends AbstractAPI<DataStatus> {

    public DataActivityAPI() {
        super(DataStatus.class);
    }

    public List<DataActivity> findByClientMetaCodigo(Long codClient, Long codMeta, String codigo) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery(""
                                                        + " SELECT da "
                                                        + " FROM DataActivity da " 
                                                        + " WHERE da.dataPK.codClient = :clientcod "
                                                        + " AND da.dataPK.codMeta = :metaCod "
                                                        + " AND da.dataPK.codigo = :codigo ");
            query.setParameter("clientcod", codClient);
            query.setParameter("metaCod", codMeta);
            query.setParameter("codigo", codigo);
            return query.getResultList();
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
    
    public DataActivity findEntityByClientMetaCodigo(Long codClient, Long codMeta, String codigo) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery(""
                                                        + " SELECT da "
                                                        + " FROM DataActivity da " 
                                                        + " WHERE da.dataPK.codClient = :clientcod "
                                                        + " AND da.dataPK.codMeta = :metaCod "
                                                        + " AND da.dataPK.codigo = :codigo ");
            query.setParameter("clientcod", codClient);
            query.setParameter("metaCod", codMeta);
            query.setParameter("codigo", codigo);
            query.setMaxResults(1);
            return (DataActivity) query.getSingleResult();
        }catch (NoResultException e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
        
    }


    public List<DataActivity> findByClientMeta(Long codClient, Long codMeta) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery(""
                    + " SELECT da "
                    + " FROM DataActivity da " 
                    + " WHERE da.dataPK.codClient = :clientcod "
                    + " AND da.dataPK.codMeta = :metaCod ");
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
