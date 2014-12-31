package com.tigo.cs.facade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.GlobalParameter;

@Stateless
public class GlobalParameterFacade extends AbstractFacade<GlobalParameter>{

    public GlobalParameterFacade() {
        super(GlobalParameter.class);
    }

    public String findByCode(String code) throws GenericFacadeException{
        EntityManager em = null;
        try {
           em = getEntityManager();;
            Query query = em.createNamedQuery("GlobalParameter.findByCodeChr");
            query.setParameter("codeChr", code);
            return ((GlobalParameter) query.getSingleResult()).getValueChr();
        } catch (NoResultException e) {
            return "";
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), e);
        } 
    }
}
