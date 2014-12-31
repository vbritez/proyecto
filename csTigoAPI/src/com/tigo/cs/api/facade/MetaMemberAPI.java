package com.tigo.cs.api.facade;

import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.tigo.cs.commons.jpa.EmptyResultException;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.jpa.MoreThanOneResultException;
import com.tigo.cs.domain.MetaMember;

public abstract class MetaMemberAPI extends AbstractAPI<MetaMember> {

    public MetaMemberAPI() {
        super(MetaMember.class);
    }

    public MetaMember findByCodMetaAndMemberCod(Long codMeta, Long codMember) throws GenericFacadeException {
        try {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("codMeta", codMeta);
            params.put("memberCod", codMember);
            return findEntityWithNamedQuery(
                    "MetaMember.findByCodMetaAndMemberCod", params);
        } catch (EmptyResultException ex) {
            return null;
        } catch (MoreThanOneResultException ex) {
            throw new GenericFacadeException(this.getClass(), getFacadeContainer().getI18nAPI().iValue(
                    "ejb.admin.commons.exception.MoreThanOneResult"), ex);
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass());
        }
    }

    public List<MetaMember> findByCodMeta(Long codMeta) throws GenericFacadeException {
        try {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("codMeta", codMeta);
            return findListWithNamedQuery("MetaMember.findByCodMeta", params);
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass());
        }
    }

    public List<MetaMember> findReturnableByCodMeta(Long codMeta) {
        try {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("codMeta", codMeta);
            return findListWithNamedQuery("MetaMember.findReturnableByCodMeta",
                    params);
        } catch (Exception e) {
            return null;
        }
    }
    
    public Long findMaxCodMemberByCodMeta(Long codMeta) {
    	 EntityManager em = null;
         try {
             em = getFacadeContainer().getEntityManager();
             Query q = em.createQuery("SELECT max(m.metaMemberPK.memberCod) FROM MetaMember m "
                 + " WHERE m.metaMemberPK.codMeta = :codMeta ");

             q.setParameter("codMeta", codMeta);
             return (Long)q.getSingleResult();
         } catch (NoResultException e) {
             return null;
         } catch (Exception e) {
             return null;
         } finally {
             if (em != null
                 && getFacadeContainer().isEntityManagerTransactional()) {
                 em.close();
             }
         }
    }

}
