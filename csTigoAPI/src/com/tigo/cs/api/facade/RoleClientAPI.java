package com.tigo.cs.api.facade;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.tigo.cs.domain.ClientServiceFunctionality;
import com.tigo.cs.domain.RoleClient;
import com.tigo.cs.domain.RoleClientScreen;
import com.tigo.cs.domain.RoleClientScreenPK;

public abstract class RoleClientAPI extends AbstractAPI<RoleClient> {

    public RoleClientAPI() {
        super(RoleClient.class);
    }

    public String saveEdit(RoleClient entity) throws Exception {
        try {
            if (entity.getRoleClientCod() == null) {
                List<RoleClientScreen> roleList = entity.getRoleClientScreenList();
                entity.setRoleClientScreenList(null);
                super.create(entity);
                for (RoleClientScreen rcs : roleList) {
                    RoleClientScreenPK pk = new RoleClientScreenPK(entity.getRoleClientCod(), rcs.getScreenclient().getScreenclientCod());
                    rcs.setRoleClientScreenPK(pk);
                    rcs.setRoleClient(entity);
                    rcs.setScreenclient(rcs.getScreenclient());
                    rcs.setAccessible(true);
                    getFacadeContainer().getRoleClientScreenAPI().create(rcs);
                }
            } else {
                RoleClient actual = find(entity.getRoleClientCod());
                List<RoleClientScreen> oldRoleList = actual.getRoleClientScreenList();
                for (RoleClientScreen rcs : oldRoleList) {
                    getFacadeContainer().getRoleClientScreenAPI().remove(rcs);
                }

                List<RoleClientScreen> newRoleList = entity.getRoleClientScreenList();
                entity.setRoleClientScreenList(null);
                edit(entity);

                for (RoleClientScreen rcs : newRoleList) {
                    RoleClientScreenPK pk = new RoleClientScreenPK(entity.getRoleClientCod(), rcs.getScreenclient().getScreenclientCod());
                    rcs.setRoleClientScreenPK(pk);
                    rcs.setRoleClient(entity);
                    rcs.setScreenclient(rcs.getScreenclient());
                    rcs.setAccessible(true);
                    getFacadeContainer().getRoleClientScreenAPI().create(rcs);
                }
            }
        } catch (Exception e) {
            throw new Exception(MessageFormat.format(
                    getFacadeContainer().getI18nAPI().iValue(
                            "web.admin.backingBean.commons.message.AnErrorHasOcurred"),
                    e));
        }

        return null;
    }

    @Override
    public RoleClient edit(RoleClient entity) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            if (getFacadeContainer().isEntityManagerTransactional()) {
                em.getTransaction().begin();
            }
            RoleClient roleClientManaged = find(entity.getRoleClientCod(), true);

            roleClientManaged.setClient(entity.getClient());
            roleClientManaged.setClientServiceFunctionalityList(entity.getClientServiceFunctionalityList());
            roleClientManaged.setDescription(entity.getDescription());
            roleClientManaged.setRoleClientScreenList(entity.getRoleClientScreenList());
            roleClientManaged.setUserwebList(entity.getUserwebList());
            roleClientManaged.setEnabled(entity.getEnabled());

            em.merge(roleClientManaged);
            if (getFacadeContainer().isEntityManagerTransactional()) {
                em.getTransaction().commit();
            }
            return roleClientManaged;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(UseradminAPI.class, null,
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

    public String deleteEditing(RoleClient entity) throws Exception {
        try {
            if (entity.getRoleClientCod() != null) {
                RoleClient actualRole = find(entity.getRoleClientCod());
                super.remove(actualRole);
            }
        } catch (Exception e) {
            throw new Exception(MessageFormat.format(
                    getFacadeContainer().getI18nAPI().iValue(
                            "web.admin.backingBean.commons.message.AnErrorHasOcurred"),
                    e));
        }
        return null;
    }

    public List<RoleClientScreen> getRoleclientscreenListByRole(Long idRoleclient) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            Query query = em.createQuery("SELECT DISTINCT rcs "
                + "FROM RoleClientScreen rcs "
                + "WHERE rcs.roleClient.roleClientCod = :clientcod ");
            query.setParameter("clientcod", idRoleclient);
            return query.getResultList();
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public RoleClientScreen getRoleclientscreen(Long idRoleclient, Long idScreenclient) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            Query query = em.createQuery("SELECT DISTINCT rcs "
                + "FROM RoleClientScreen rcs "
                + "WHERE rcs.roleClient.roleClientCod = :roleclientcod "
                + "and rcs.screenclient.screenclientCod = :screenclientcod ");
            query.setParameter("roleclientcod", idRoleclient);
            query.setParameter("screenclientcod", idScreenclient);
            if (query.getResultList() != null
                && query.getResultList().size() == 1) {
                return (RoleClientScreen) query.getSingleResult();
            } else {
                return null;
            }
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<RoleClient> getAllEnabledRoleclientByClient(Long clientCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            Query query = em.createQuery("SELECT DISTINCT rc "
                + "FROM RoleClient rc " + "WHERE rc.enabled = true "
                + "AND rc.client.clientCod = :clientCod");
            query.setParameter("clientCod", clientCod);
            return query.getResultList();
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<RoleClient> getRoleclientList(Long userwebCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            Query query = em.createQuery("" + "SELECT DISTINCT rcL "
                + "FROM Userweb u, " + "IN (u.roleClientList) rcL "
                + "WHERE u.userwebCod = :userwebcod");
            query.setParameter("userwebcod", userwebCod);
            return query.getResultList();
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<RoleClient> getRoleClientList(Long idClient, Long idService, Long idFunctionality) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            Query query = em.createQuery("SELECT DISTINCT rc "
                + "FROM RoleClient rc, "
                + "IN (rc.clientServiceFunctionalityList) csfL "
                + "WHERE csfL.client.clientCod = :client "
                + "and csfL.serviceFunctionality.id.codService = :service "
                + "and csfL.serviceFunctionality.id.codFunctionality = :functionality ");
            query.setParameter("client", idClient);
            query.setParameter("service", idService);
            query.setParameter("functionality", idFunctionality);
            if (query.getResultList() != null) {
                return query.getResultList();
            } else {
                return new ArrayList<RoleClient>();
            }
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<RoleClientScreen> findScreens(RoleClient roleClient) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            Query q = em.createQuery(""
                + "SELECT r.roleClientScreenList FROM RoleClient r "
                + "WHERE r.roleClientCod = :roleClientCod");
            q.setParameter("roleClientCod", roleClient.getRoleClientCod());
            return q.getResultList();
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<ClientServiceFunctionality> findFunctionalities(RoleClient roleClient) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            Query q = em.createQuery(""
                + "SELECT r.clientServiceFunctionalityList FROM RoleClient r "
                + "WHERE r.roleClientCod = :roleClientCod");
            q.setParameter("roleClientCod", roleClient.getRoleClientCod());
            return q.getResultList();
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

}
