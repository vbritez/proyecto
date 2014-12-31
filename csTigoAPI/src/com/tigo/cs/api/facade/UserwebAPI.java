package com.tigo.cs.api.facade;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.Classification;
import com.tigo.cs.domain.Service;
import com.tigo.cs.domain.Userweb;

public abstract class UserwebAPI extends AbstractAPI<Userweb> {

    public UserwebAPI() {
        super(Userweb.class);
    }

    @Override
    public Userweb edit(Userweb entity) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            if (getFacadeContainer().isEntityManagerTransactional()) {
                em.getTransaction().begin();
            }

            Userweb persisted = find(entity.getUserwebCod());
            persisted.setAdminNum(entity.getAdminNum());
            persisted.setCellphoneNum(entity.getCellphoneNum());
            persisted.setChangepasswChr(entity.getChangepasswChr());
            persisted.setClassificationList(entity.getClassificationList());
            persisted.setDescriptionChr(entity.getDescriptionChr());
            persisted.setEnabledChr(entity.getEnabledChr());
            persisted.setMailChr(entity.getMailChr());
            persisted.setNameChr(entity.getNameChr());
            persisted.setPasswordChr(entity.getPasswordChr());
            persisted.setPwdRetryCount(entity.getPwdRetryCount());
            persisted.setRoleClientList(entity.getRoleClientList());
            em.merge(persisted);
            if (getFacadeContainer().isEntityManagerTransactional()) {
                em.getTransaction().commit();
            }
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
        return null;
    }

    public Userweb getUserwebByUsername(String username) throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            if (username != null) {
                Query query = em.createNamedQuery("Userweb.findByUsernameChr");
                query.setParameter("usernameChr", username.toLowerCase());
                return (Userweb) query.getSingleResult();
            }
            throw new GenericFacadeException(this.getClass(), getFacadeContainer().getI18nAPI().iValue(
                    "ejb.admin.userWebFacade.exception.MissingUsernameParameter"));
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public void setUserwebHash(String username, String hash) throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            if (username != null) {
                Query query = em.createNamedQuery("Userweb.findByUsernameChr");
                query.setParameter("usernameChr", username.toLowerCase());
                Userweb user = (Userweb) query.getSingleResult();
                user.setChangepasshashChr(hash);
                edit(user);
                return;
            }
            throw new GenericFacadeException(this.getClass(), getFacadeContainer().getI18nAPI().iValue(
                    "ejb.client.userWebFacade.exception.MissingParameterUsername"));
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<Service> getClientServices(Long userwebCod) throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT DISTINCT s "
                + "FROM Service s, " + "  IN (s.screenClientList) scList, "
                + "  IN (scList.roleClientScreenList) rcList, "
                + "  IN (rcList.roleClient.userwebList) uList "
                + " WHERE uList.userwebCod = :userweb "
                + " ORDER BY s.descriptionChr");
            query.setParameter("userweb", userwebCod);
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

    public Boolean isScreenAllowebForUserclient(Long codUserweb, String viewId, boolean isAdmin) throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            boolean result = false;
            Query query = em.createQuery("SELECT DISTINCT sc "
                + "FROM Screenclient sc, "
                + "IN (sc.roleClientScreenList) rcsList, "
                + "IN (rcsList.roleClient.userwebList) uList "
                + "WHERE rcsList.accessible = true "
                + "AND uList.userwebCod = :codUserweb "
                + "AND sc.pageChr =:viewId "
                + "AND rcsList.roleClient.enabled = true");
            query.setParameter("codUserweb", codUserweb);
            query.setParameter("viewId", viewId);
            result = query.getResultList().size() == 1;// .getSingleResult() !=
                                                       // null;

            if (!result && isAdmin) {
                // Se ve si se esta tratando de acceder a una pantalla
                // administrativa
                query = em.createQuery("SELECT DISTINCT sc "
                    + "FROM Screenclient sc "
                    + "WHERE sc.codModuleclient.moduleclientCod = 4 "
                    + "AND sc.showonmenuChr = true "
                    + "AND sc.pageChr =:viewId " + "ORDER BY sc.orderNum");
                query.setParameter("viewId", viewId);
                result = query.getSingleResult() != null;
            }

            return result;
        } catch (NoResultException e) {
            return false;
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }

    }

    public Boolean getUserRolePrivileges(Long userwebCod, Long serviceCod, Long functionalityCod) throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT u "
                + "FROM Userweb u, "
                + "IN (u.roleClientList) rc, "
                + "IN (rc.clientServiceFunctionalityList) csf "
                + "WHERE u.userwebCod = :userwebCod "
                + "AND csf.serviceFunctionality.service.serviceCod = :serviceCod "
                + "AND csf.serviceFunctionality.functionality.functionalityCod = :functionalityCod");
            query.setParameter("userwebCod", userwebCod);
            query.setParameter("serviceCod", serviceCod);
            query.setParameter("functionalityCod", functionalityCod);
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

    public List<Classification> getClassificationsByUserweb(Long userwebCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("" + "SELECT u.classificationList "
                + "FROM Userweb u " + "WHERE u.userwebCod = :userwebCod");
            query.setParameter("userwebCod", userwebCod);
            List<Classification> lista = query.getResultList();
            return lista;
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<Userweb> findByUserwebAndClassification(Userweb userweb) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery("SELECT DISTINCT u FROM Userweb u,"
                + " IN (u.classificationList ) cl "
                + " WHERE cl in (:classifications) "
                + " AND u.enabledChr = true " + " ORDER BY u.nameChr ");
            // List<Classification> classifications =
            // getFacadeContainer().getClassificationAPI().findByUserweb(
            // userweb);
            List<Classification> classifications = getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                    userweb);
            if (classifications == null) {
                return null;
            }
            q.setParameter("classifications", classifications);
            return q.getResultList();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public Userweb isUserwebBlocked(Userweb userweb) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery("SELECT u FROM Userweb u "
                + " WHERE u.userwebCod = :userwebCod "
                + " AND u.enabledChr = false "
                + " AND u.pwdRetryCount >= :pwdRetryCount ");

            q.setParameter("userwebCod", userweb.getUserwebCod());
            q.setParameter(
                    "pwdRetryCount",
                    Integer.valueOf(getFacadeContainer().getGlobalParameterAPI().findByCode(
                            "password.loginretry")));
            return (Userweb) q.getSingleResult();
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
