package com.tigo.cs.api.facade;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.tigo.cs.domain.FeatureElement;
import com.tigo.cs.domain.Phone;
import com.tigo.cs.domain.PhoneList;

public abstract class PhoneAPI extends AbstractAPI<Phone> {

    public PhoneAPI() {
        super(Phone.class);
    }

    public List<Phone> getClientPhone(Long clientCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT DISTINCT p FROM Phone p "
                + " WHERE p.client.clientCod = :clientCod "
                + " AND p.cellphoneNum is not null");
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

    public List<PhoneList> getPhoneList(Long phoneCod, Long clientCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT DISTINCT p.phoneLists from Phone p "
                + " WHERE p.phoneCod = :phoneCod "
                + " AND p.client.clientCod = :clientCod"
                + " AND p.cellphoneNum is not null");
            query.setParameter("phoneCod", phoneCod);
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

    public List<PhoneList> getPhoneList(Long phoneCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT pl.phoneLists FROM Phone pl "
                + " WHERE pl.phoneCod = :phoneCod"
                + " AND pl.cellphoneNum is not null");
            query.setParameter("phoneCod", phoneCod);
            return (List<PhoneList>) query.getResultList();
        } catch (Exception e) {
            return new ArrayList<PhoneList>();
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public Phone getPhone(BigInteger cellphoneNum, Long clientCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT DISTINCT p from Phone p "
                + " WHERE p.cellphoneNum = :cellphoneNum "
                + " AND p.client.clientCod = :clientCod");
            query.setParameter("cellphoneNum", cellphoneNum);
            query.setParameter("clientCod", clientCod);
            return (Phone) query.getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public Long getMaxPhoneCod() {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT max(p.phoneCod) from Phone p ");
            return (Long) query.getSingleResult();
        } catch (NoResultException e) {
            return Long.valueOf(0L);
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public Phone findByMsisdnAndClient(BigInteger msisdn, Long clientCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT p FROM Phone p "
                + " WHERE p.client.clientCod = :clientCod "
                + " AND p.cellphoneNum = :msisdn");
            query.setParameter("msisdn", msisdn);
            query.setParameter("clientCod", clientCod);
            query.setMaxResults(1);
            return (Phone) query.getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public boolean isBlacklisted(FeatureElement featureElement, Phone phone) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT p from FeatureElement fe, IN (fe.phoneLists) pl, "
                + " IN (pl.phones) p "
                + " WHERE pl.typeChr = 'B' "
                + " AND fe.featureElementCod = :featureElement "
                + " AND p.phoneCod =  :phone");
            query.setParameter("phone", phone.getPhoneCod());
            query.setParameter("featureElement",
                    featureElement.getFeatureElementCod());
            query.setMaxResults(1);
            return query.getSingleResult() != null ? true : false;
        } catch (NoResultException e) {
            return false;
        } catch (Exception e) {
            return true;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public Long getPhoneNextval() {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createNativeQuery("SELECT PHONE_SEQ.nextval FROM DUAL");
            BigDecimal nextVal = (BigDecimal) q.getSingleResult();
            return nextVal.longValue();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
        return null;
    }

}
