package com.tigo.cs.api.facade;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.tigo.cs.domain.Client;
import com.tigo.cs.domain.DataCheck;
import com.tigo.cs.domain.Service;
import com.tigo.cs.domain.ussd.UssdMenuEntry;
import com.tigo.cs.domain.ussd.UssdMenuEntryUssdUser;
import com.tigo.cs.domain.ussd.UssdMenuEntryUssdUserPK;
import com.tigo.cs.domain.ussd.UssdUser;

public abstract class UssdMenuEntryAPI extends AbstractAPI<UssdMenuEntry> {

    public UssdMenuEntryAPI() {
        super(UssdMenuEntry.class);
    }

    public DataCheck findDataCheckByEntryCode(String code, Client client) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery("SELECT cl.dataCheck FROM UssdMenuEntry m"
                + ",IN (m.ussdMenuEntryCheckList) cl "
                + "WHERE m.code = :code " + "AND ( cl.client = :client "
                + "OR cl.client = 0 )" + "ORDER BY cl.client desc ");
            q.setParameter("code", code);
            q.setParameter("client", client);
            q.setMaxResults(1);
            return (DataCheck) q.getSingleResult();
        } catch (NoResultException exception) {
            return null;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public UssdMenuEntry findByCode(String code) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery("SELECT m FROM UssdMenuEntry m "
                + "WHERE m.code = :code ");
            q.setParameter("code", code);
            q.setMaxResults(1);
            return (UssdMenuEntry) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<UssdMenuEntry> findByService(Service s) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery("SELECT c.ussdMenuEntry FROM Service s ,in (s.ussdServiceCorresps) c "
                + "WHERE s.serviceCod = :serviceCod");
            q.setParameter("serviceCod", s.getServiceCod());
            return q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<UssdMenuEntry> findByApplication(Long applicationId) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery(" SELECT m FROM UssdMenuEntry m "
                + " WHERE m.ussdApplication.id = :applicationId ");
            q.setParameter("applicationId", applicationId);
            return (List<UssdMenuEntry>) q.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<UssdMenuEntry>();
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public UssdMenuEntry findByApplicationAndOwner(Long applicationId, Long ownerId) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery(" SELECT m FROM UssdMenuEntry m "
                + " WHERE m.ussdApplication.id = :applicationId "
                + " AND m.owner.id = :ownerId");
            q.setParameter("applicationId", applicationId);
            q.setParameter("ownerId", ownerId);
            return (UssdMenuEntry) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }
}
