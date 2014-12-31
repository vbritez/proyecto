package com.tigo.cs.api.facade;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.tigo.cs.domain.view.DataIcon;

public class DataIconAPI extends AbstractAPI<DataIcon> {

    public DataIconAPI() {
        super(DataIcon.class);
    }

    public DataIcon findEntityByClientMetaCodigo(Long codClient, Long codMeta, String codigo) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery(""
                                                        + " SELECT da "
                                                        + " FROM DataIcon da " 
                                                        + " WHERE da.dataPK.codClient = :clientcod "
                                                        + " AND da.dataPK.codMeta = :metaCod "
                                                        + " AND da.dataPK.codigo = :codigo ");
            query.setParameter("clientcod", codClient);
            query.setParameter("metaCod", codMeta);
            query.setParameter("codigo", codigo);
            query.setMaxResults(1);
            return (DataIcon) query.getSingleResult();
        }catch (NoResultException e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
        
    }
	
	public List<DataIcon> findByClient(Long codClient) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery(""
                    + " SELECT da "
                    + " FROM DataIcon da " 
                    + " WHERE da.dataPK.codClient = :clientcod  ");
            query.setParameter("clientcod", codClient);
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
	
	
	public DataIcon findByUrl(String url) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT ip FROM DataIcon ip "
                + "WHERE ip.url = :url ");
            query.setParameter("url", url);
            query.setMaxResults(1);
            return (DataIcon) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(DataIconAPI.class, null,
                    e.getMessage(), e);
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }
}
