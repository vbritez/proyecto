package com.tigo.cs.api.facade;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.tigo.cs.domain.RoleClientScreen;

public abstract class RoleClientScreenAPI extends AbstractAPI<RoleClientScreen> {

    public RoleClientScreenAPI() {
        super(RoleClientScreen.class);
    }

    public List<RoleClientScreen> getRoleclientscreenListByService(Long idClient, Long idService) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            Query query = em.createQuery("SELECT DISTINCT rcs "
                + "FROM RoleClientScreen rcs "
                + "WHERE rcs.roleClient.client.clientCod = :client "
                + "AND rcs.screenclient.service.serviceCod = :service ");
            query.setParameter("client", idClient);
            query.setParameter("service", idService);
            if (query.getResultList() != null) {
                return (List<RoleClientScreen>) query.getResultList();
            } else {
                return new ArrayList<RoleClientScreen>();
            }
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<RoleClientScreen> getRoleclientscreenListByMeta(Long idClient, Long idMeta) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            Query query = em.createQuery("SELECT DISTINCT rcs "
                + "FROM RoleClientScreen rcs "
                + "WHERE rcs.roleClient.client.clientCod = :client "
                + "AND rcs.screenclient.meta.metaCod = :meta ");
            query.setParameter("client", idClient);
            query.setParameter("meta", idMeta);
            if (query.getResultList() != null) {
                return (List<RoleClientScreen>) query.getResultList();
            } else {
                return new ArrayList<RoleClientScreen>();
            }
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }
}
