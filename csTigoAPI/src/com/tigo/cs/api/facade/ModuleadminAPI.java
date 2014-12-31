package com.tigo.cs.api.facade;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.Moduleadmin;

public abstract class ModuleadminAPI extends AbstractAPI<Moduleadmin> {

    public ModuleadminAPI() {
        super(Moduleadmin.class);
    }

    public List<Moduleadmin> getActiveModules() throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT DISTINCT ma "
                + "FROM Moduleadmin ma " + "WHERE ma.moduleadminCod > 0 "
                + "AND SIZE(ma.screenCollection) > 0  "
                + "ORDER BY ma.orderNum");
            return (List<Moduleadmin>) query.getResultList();
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }
}
