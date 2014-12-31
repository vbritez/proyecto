package com.tigo.cs.api.facade;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.tigo.cs.commons.jpa.EmptyResultException;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.jpa.MoreThanOneResultException;
import com.tigo.cs.domain.Screen;

public abstract class ScreenAPI extends AbstractAPI<Screen> {

    public ScreenAPI() {
        super(Screen.class);
    }

    public Screen findScreenByPage(String screenPage) throws MoreThanOneResultException, GenericFacadeException {
        try {
            return this.findEntityWithNamedQuery("Screen.findByPageChr",
                    addSingleParam("pageChr", screenPage));
        } catch (EmptyResultException ex) {
            return null;
        } catch (MoreThanOneResultException ex) {
            throw ex;
        } catch (Exception e) {
            throw new GenericFacadeException(getClass());
        }
    }

    public List<Screen> getScreensNoSecurityAuth() throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createNamedQuery("Screen.findByNosecurityChr");
            query.setParameter("nosecurityChr", true);
            return (List<Screen>) query.getResultList();
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<Screen> getScreensNoSecurityAuthClient() throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createNamedQuery("Screen.findByNosecurityChr");
            query.setParameter("nosecurityChr", true);
            return (List<Screen>) query.getResultList();
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

}
