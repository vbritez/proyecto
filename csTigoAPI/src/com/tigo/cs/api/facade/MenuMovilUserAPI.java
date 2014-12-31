package com.tigo.cs.api.facade;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.tigo.cs.domain.MenuMovilUser;

public abstract class MenuMovilUserAPI extends AbstractAPI<MenuMovilUser> implements Serializable {

    private static final long serialVersionUID = -7811279298347739480L;

    protected MenuMovilUserAPI() {
        super(MenuMovilUser.class);
    }

    public MenuMovilUser getMenuMovileUser(String user, String pass, String appKey) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("select mu from MenuMovilUser mu where mu.usernameChr = :user" +
                    " and mu.passwordChr = :pass and mu.application.key = :appKey " +
                    " and mu.enabledChr = true");
            query.setParameter("user", user);
            query.setParameter("pass", pass);
            query.setParameter("appKey", appKey);
            query.setMaxResults(1);
            return (MenuMovilUser) query.getSingleResult();
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(MenuMovilUserAPI.class,null,
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
