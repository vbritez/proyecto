package com.tigo.cs.api.facade;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.Moduleadmin;
import com.tigo.cs.domain.Roleadmin;
import com.tigo.cs.domain.Screen;
import com.tigo.cs.domain.Useradmin;

public abstract class UseradminAPI extends AbstractAPI<Useradmin> {

    public UseradminAPI() {
        super(Useradmin.class);
    }

    @Override
    public Useradmin edit(Useradmin entity) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            if (getFacadeContainer().isEntityManagerTransactional()) {
                em.getTransaction().begin();
            }
            Useradmin useradmin = entity;
            Useradmin useradminManaged = find(useradmin.getUseradminCod(),
                    "roleadminList");

            useradminManaged.setPasswordChr(useradmin.getPasswordChr());
            useradminManaged.setChangepasswChr(useradmin.getChangepasswChr());
            useradminManaged.setEnabledChr(useradmin.getEnabledChr());
            useradminManaged.setLdapChr(useradmin.getLdapChr());
            useradminManaged.setPwdRetryCount(useradmin.getPwdRetryCount());
            useradminManaged.setExpirationDate(useradmin.getExpirationDate());

            useradminManaged.setNameChr(useradmin.getNameChr());
            useradminManaged.setDescriptionChr(useradmin.getDescriptionChr());
            useradminManaged.setCellphoneNum(useradmin.getCellphoneNum());
            useradminManaged.getRoleadminList().clear();

            List<Roleadmin> roles = useradmin.getRoleadminList();
            for (Roleadmin role : roles) {
                useradminManaged.getRoleadminList().add(
                        getFacadeContainer().getRoleadminAPI().find(
                                role.getRoleadminCod()));
            }
            em.merge(useradminManaged);
            if (getFacadeContainer().isEntityManagerTransactional()) {
                em.getTransaction().commit();
            }
            return useradminManaged;
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

    public Useradmin findByUsername(String username) throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            if (username != null) {
                Query query = em.createNamedQuery("Useradmin.findByUsernameChr");
                query.setParameter("usernameChr", username.toLowerCase());
                return (Useradmin) query.getSingleResult();
            }
            throw new GenericFacadeException(this.getClass(), getFacadeContainer().getI18nAPI().iValue(
                    "ejb.admin.userAdminFacade.exception.MissingUsernameParameter"));
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(UseradminAPI.class, null,
                    e.getMessage(), e);
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<Moduleadmin> getUserMenuModules(Long userId) throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT DISTINCT mda "
                + "FROM Moduleadmin mda, " + "IN (mda.screenCollection) scr, "
                + "IN (scr.roleadminScreenCollection) ras, "
                + "IN (ras.roleadmin.useradminList) uas "
                + "WHERE uas.useradminCod = :userId "
                + "AND mda.moduleadminCod > 0 " + "AND ras.readChr = true "
                + "AND scr.showonmenuChr = true " + "ORDER BY mda.orderNum");
            query.setParameter("userId", userId);
            return query.getResultList();
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<Screen> getUserScreensByModule(Long userId, Long moduleId) throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            if (moduleId.longValue() > 0) {
                Query query = em.createQuery("SELECT DISTINCT scr "
                    + "FROM Screen scr, "
                    + "IN (scr.roleadminScreenCollection) ras, "
                    + "IN (ras.roleadmin.useradminList) uas "
                    + "WHERE uas.useradminCod = :userId "
                    + "AND scr.moduleadmin.moduleadminCod = :moduleId "
                    + "AND ras.readChr = true "
                    + "AND scr.showonmenuChr = true " + "ORDER BY scr.orderNum");
                query.setParameter("userId", userId);
                query.setParameter("moduleId", moduleId);
                return query.getResultList();
            }
            throw new GenericFacadeException(this.getClass(), getFacadeContainer().getI18nAPI().iValue(
                    "ejb.admin.userDminFacade.exception.ModuleIdParameterMustBeGreaterThanZero"));
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public Boolean getUserScreenAccess(Long userId, String viewId) throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT DISTINCT scr "
                + "FROM Screen scr, "
                + "IN (scr.roleadminScreenCollection) ras, "
                + "IN (ras.roleadmin.useradminList) uas "
                + "WHERE uas.useradminCod = :userId "
                + "AND scr.pageChr = :viewId " + "AND ras.readChr = true");
            query.setParameter("userId", userId);
            query.setParameter("viewId", viewId);
            return query.getSingleResult() != null;
        } catch (NoResultException e) {
            return Boolean.FALSE;
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public Boolean getUserScreenAccess(Long userId, String viewId, String crudActionFieldName) throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT DISTINCT scr "
                + "FROM Screen scr, "
                + "IN (scr.roleadminScreenCollection) ras, "
                + "IN (ras.roleadmin.useradminList) uas "
                + "WHERE uas.useradminCod = :userId "
                + "AND scr.pageChr = :viewId " + "AND ras."
                + crudActionFieldName + " = true");
            query.setParameter("userId", userId);
            query.setParameter("viewId", viewId);
            return query.getSingleResult() != null;
        } catch (NoResultException e) {
            return Boolean.FALSE;
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

}
