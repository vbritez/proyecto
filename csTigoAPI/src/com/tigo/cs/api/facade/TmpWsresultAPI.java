package com.tigo.cs.api.facade;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.jpa.MoreThanOneResultException;
import com.tigo.cs.domain.TmpWsresult;

public abstract class TmpWsresultAPI extends AbstractAPI<TmpWsresult> {

    public TmpWsresultAPI() {
        super(TmpWsresult.class);
    }

    public String findData(Long codClient, String sessionId, String dataType, String dataId) throws GenericFacadeException, MoreThanOneResultException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createNamedQuery("TmpWsresult.findByParameters");
            query.setParameter("codClient", codClient);
            query.setParameter("sessionId", sessionId);
            query.setParameter("dataType", dataType);
            query.setParameter("dataId", dataId);
            return ((TmpWsresult) query.getSingleResult()).getDataC();
        } catch (NoResultException nre) {
            return null;
        } catch (NonUniqueResultException nue) {
            throw new MoreThanOneResultException(nue);
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public int removeSessionTmpCacheData(String sessionId) throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("DELETE FROM TmpWsresult o WHERE o.sessionId = :sessionId");
            query.setParameter("sessionId", sessionId);
            return query.executeUpdate();
        } catch (NoResultException nre) {
            return 0;
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public int removeTmpCacheData() throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("DELETE FROM TmpWsresult o");
            return query.executeUpdate();
        } catch (NoResultException nre) {
            return 0;
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }
}
