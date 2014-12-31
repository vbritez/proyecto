package com.tigo.cs.api.facade;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.tigo.cs.domain.view.DataStatus;

/**
 *
 * @author Miguel Zorrilla
 */
public abstract class DataStatusAPI extends AbstractAPI<DataStatus> {

    public DataStatusAPI() {
        super(DataStatus.class);
    }

    public List<DataStatus> findByClientMetaCodigo(Long codClient, Long codMeta, String codigo) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery(""
                                                        + " SELECT ds "
                                                        + " FROM DataStatus ds " 
                                                        + " WHERE ds.dataPK.codClient = :clientcod "
                                                        + " AND ds.dataPK.codMeta = :metaCod "
                                                        + " AND ds.dataPK.codigo = :codigo ");
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
    
    public DataStatus findEntityByClientMetaCodigo(Long codClient, Long codMeta, String codigo) {
        EntityManager em = null;
        try{
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery(""
                                                        + " SELECT ds "
                                                        + " FROM DataStatus ds " 
                                                        + " WHERE ds.dataPK.codClient = :clientcod "
                                                        + " AND ds.dataPK.codMeta = :metaCod "
                                                        + " AND ds.dataPK.codigo = :codigo ");
            query.setParameter("clientcod", codClient);
            query.setParameter("metaCod", codMeta);
            query.setParameter("codigo", codigo);
            query.setMaxResults(1);
            return (DataStatus) query.getSingleResult();
        }catch (NoResultException e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
        
    }


    public List<DataStatus> findByClientMeta(Long codClient, Long codMeta) {
        EntityManager em = null;
        try{
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery(""
                    + " SELECT ds "
                    + " FROM DataStatus ds " 
                    + " WHERE ds.dataPK.codClient = :clientcod "
                    + " AND ds.dataPK.codMeta = :metaCod ");
            query.setParameter("clientcod", codClient);
            query.setParameter("metaCod", codMeta);
            return query.getResultList();
        }catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }
    
    
    public DataStatus findByCodigo(String codigo) {
        EntityManager em = null;
        try{
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery(""
                    + " SELECT ds "
                    + " FROM DataStatus ds " 
                    + " WHERE ds.dataPK.codigo = :codigo ");
            query.setParameter("codigo", codigo);
            return (DataStatus) query.getSingleResult();
        }catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }
}
