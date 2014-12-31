package com.tigo.cs.api.facade;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.GlobalParameter;

public abstract class GlobalParameterAPI extends AbstractAPI<GlobalParameter> {

    public GlobalParameterAPI() {
        super(GlobalParameter.class);
    }

    public String findByCode(String code) throws GenericFacadeException {
        EntityManager em = null;
        try {
           em = getFacadeContainer().getEntityManager();
            Query query = em.createNamedQuery("GlobalParameter.findByCodeChr");
            query.setParameter("codeChr", code);
            return ((GlobalParameter) query.getSingleResult()).getValueChr();
        } catch (NoResultException e) {
            return "";
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }
}
