package com.tigo.cs.api.facade;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.jpa.MoreThanOneResultException;
import com.tigo.cs.domain.Phone;
import com.tigo.cs.domain.PhoneList;

public abstract class PhoneListAPI extends AbstractAPI<PhoneList> {

    public PhoneListAPI() {
        super(PhoneList.class);
    }

    public List<Phone> getClientPhone(Long phoneListCod, Long clientCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT p.phones FROM PhoneList p "
                + " WHERE p.phoneListCod = :phoneListCod "
                + " AND p.client.clientCod = :clientCod");
            query.setParameter("phoneListCod", phoneListCod);
            query.setParameter("clientCod", clientCod);
            return (List<Phone>) query.getResultList();
        } catch (Exception e) {
            return new ArrayList<Phone>();
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<Phone> getClientPhone(Long clientCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT DISTINCT p.phones FROM PhoneList p "
                + " WHERE p.client.clientCod = :clientCod");
            query.setParameter("clientCod", clientCod);
            return (List<Phone>) query.getResultList();
        } catch (Exception e) {
            return new ArrayList<Phone>();
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<Phone> getNoClientPhone(Long phoneListCod, Long clientCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT p.phones FROM PhoneList p "
                + " WHERE p.phoneListCod != :phoneListCod "
                + " AND p.client.clientCod = :clientCod");
            query.setParameter("phoneListCod", phoneListCod);
            query.setParameter("clientCod", clientCod);
            return (List<Phone>) query.getResultList();
        } catch (Exception e) {
            return new ArrayList<Phone>();
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<PhoneList> getClientPhoneList(Long clientCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT pl FROM PhoneList pl "
                + " WHERE pl.client.clientCod = :clientCod");
            query.setParameter("clientCod", clientCod);
            return (List<PhoneList>) query.getResultList();
        } catch (Exception e) {
            return new ArrayList<PhoneList>();
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<Phone> getPhones(Long phoneListCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT DISTINCT p.phones from PhoneList p "
                + " WHERE p.phoneListCod = :phoneListCod ");
            query.setParameter("phoneListCod", phoneListCod);
            return (List<Phone>) query.getResultList();
        } catch (Exception e) {
            return new ArrayList<Phone>();
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<PhoneList> getPhoneListByClient(Long clientCod) throws MoreThanOneResultException, GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createNamedQuery("PhoneList.findByClient");
            query.setParameter("clientCod", clientCod);
            return (List<PhoneList>) query.getResultList();
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<PhoneList> getPhoneListByClientType(Long clientCod, String type) throws MoreThanOneResultException, GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createNamedQuery("PhoneList.findByClientType");
            query.setParameter("clientCod", clientCod);
            query.setParameter("typeChr", type);
            return (List<PhoneList>) query.getResultList();
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

}
