package com.tigo.cs.api.facade;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.tigo.cs.domain.AndroidVersion;

public class AndroidVersionAPI extends AbstractAPI<AndroidVersion> {

    public AndroidVersionAPI() {
        super(AndroidVersion.class);
    }

    public AndroidVersion findByVersionName(String versionName) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createNamedQuery("AndroidVersion.findByVersionName");
            query.setParameter("versionName", versionName);
            return (AndroidVersion) query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }
}
