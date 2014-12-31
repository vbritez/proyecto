package com.tigo.cs.api.facade;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.tigo.cs.domain.ClientServiceFunctionality;
import com.tigo.cs.domain.Userphone;

public abstract class ClientServiceFunctionalityAPI extends AbstractAPI<ClientServiceFunctionality> {

    public ClientServiceFunctionalityAPI() {
        super(ClientServiceFunctionality.class);
    }

    public ClientServiceFunctionality getClientServiceFunctionality(Long codClient, Long codService, Long codFunctionality) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            Query query = em.createQuery("SELECT csf "
                + "FROM ClientServiceFunctionality csf "
                + "WHERE csf.serviceFunctionality.id.codFunctionality = :codFunc "
                + "AND csf.serviceFunctionality.id.codService = :codServ "
                + "AND csf.client.clientCod = :codCli ");
            query.setParameter("codFunc", codFunctionality);
            query.setParameter("codServ", codService);
            query.setParameter("codCli", codClient);
            return (ClientServiceFunctionality) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<ClientServiceFunctionality> getClientServiceFunctionalitiesByClientcod(Long codClient) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            Query query = em.createQuery("" + "DELETE csf "
                + "FROM ClientServiceFunctionality csf "
                + "WHERE csf.client.clientCod = :codClient ");
            query.setParameter("codClient", codClient);
            return query.getResultList();
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<ClientServiceFunctionality> findClientServiceFunctionalitiesByClientcod(Long codClient) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery(""
                + "SELECT csf "
                + "FROM ClientServiceFunctionality csf "
                + "WHERE csf.client.clientCod = :codClient "
                + "ORDER BY csf.serviceFunctionality.functionality.functionalityCod ");
            query.setParameter("codClient", codClient);
            return query.getResultList();
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    @Override
    public void remove(ClientServiceFunctionality entity) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            Query query = em.createNativeQuery(""
                + "DELETE "
                + "FROM userphone_client_ser_func ucsf "
                + "WHERE ucsf.cod_client = ? AND ucsf.cod_service = ? AND ucsf.cod_functionality = ?");
            query.setParameter(1, entity.getClient().getClientCod());
            query.setParameter(
                    2,
                    entity.getServiceFunctionality().getService().getServiceCod());
            query.setParameter(
                    3,
                    entity.getServiceFunctionality().getFunctionality().getFunctionalityCod());
            query.executeUpdate();

            query = em.createNativeQuery(""
                + "DELETE "
                + "FROM userweb_client_ser_func ucsf "
                + "WHERE ucsf.cod_client = ? AND ucsf.cod_service = ? AND ucsf.cod_functionality = ?");
            query.setParameter(1, entity.getClient().getClientCod());
            query.setParameter(
                    2,
                    entity.getServiceFunctionality().getService().getServiceCod());
            query.setParameter(
                    3,
                    entity.getServiceFunctionality().getFunctionality().getFunctionalityCod());
            query.executeUpdate();

            super.remove(entity);
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<ClientServiceFunctionality> findByUserphone(Userphone userphone) {

        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            Query q = em.createQuery("SELECT f FROM Userphone u, "
                + "IN (u.clientServiceFunctionalityList) f "
                + "WHERE u.userphoneCod = :userphoneCod "
                + "AND f.clientServiceFunctionalityPK.codFunctionality = 0");
            q.setParameter("userphoneCod", userphone.getUserphoneCod());
            return q.getResultList();
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<ClientServiceFunctionality> getServiceFunctionalitiesByRole(Long roleclientCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            Query query = em.createQuery(""
                + "SELECT csfL "
                + "FROM RoleClient c, "
                + "IN (c.clientServiceFunctionalityList) csfL "
                + "WHERE c.roleClientCod = :roleclientcod "
                + "ORDER BY csfL.serviceFunctionality.service.descriptionChr, csfL.serviceFunctionality.functionality.descriptionChr");
            query.setParameter("roleclientcod", roleclientCod);
            return query.getResultList();
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }
}
