package com.tigo.cs.api.facade;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.tigo.cs.domain.ussd.UssdUser;

public abstract class UssdUserAPI extends AbstractAPI<UssdUser> {

    public UssdUserAPI() {
        super(UssdUser.class);
    }

    public Long findByCode(String code) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery(" SELECT m.id FROM UssdUser m "
                + " WHERE m.code = :code ");
            q.setParameter("code", code);
            return (Long) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public UssdUser getUssdUserById(Long ussdUserId) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("" + "SELECT u  " + "FROM UssdUser u "
                + "WHERE u.ussdUserCod = :ussdUserId");
            query.setParameter("ussdUserId", ussdUserId);
            return (UssdUser) query.getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    @Override
    public UssdUser edit(UssdUser entity) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            if (getFacadeContainer().isEntityManagerTransactional()) {
                em.getTransaction().begin();
            }
            UssdUser ussdUserManaged = find(entity.getId(),
                    "ussdMenuEntryUssdUserList");
            ussdUserManaged.setCode(entity.getCode());
            ussdUserManaged.setDescription(entity.getDescription());
            ussdUserManaged.setName(entity.getName());
            ussdUserManaged.setUssdApplication(entity.getUssdApplication());
            ussdUserManaged.setUssdMenuEntryUssdUserList(entity.getUssdMenuEntryUssdUserList());
            em.merge(ussdUserManaged);
            if (getFacadeContainer().isEntityManagerTransactional()) {
                em.getTransaction().commit();
            }
            return ussdUserManaged;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(ClientAPI.class, null,
                    e.getMessage(), e);
            if (getFacadeContainer().isEntityManagerTransactional()) {
                em.getTransaction().rollback();
            }
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }
}
