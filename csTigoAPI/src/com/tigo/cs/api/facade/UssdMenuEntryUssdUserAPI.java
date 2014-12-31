package com.tigo.cs.api.facade;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.tigo.cs.domain.Client;
import com.tigo.cs.domain.Service;
import com.tigo.cs.domain.Userphone;
import com.tigo.cs.domain.ussd.UssdMenuEntry;
import com.tigo.cs.domain.ussd.UssdMenuEntryUssdUser;
import com.tigo.cs.domain.ussd.UssdMenuEntryUssdUserPK;
import com.tigo.cs.domain.ussd.UssdUser;

public abstract class UssdMenuEntryUssdUserAPI extends AbstractAPI<UssdMenuEntryUssdUser> {

    public UssdMenuEntryUssdUserAPI() {
        super(UssdMenuEntryUssdUser.class);
    }

    public UssdMenuEntryUssdUser findByMenuEntryUssdUser(UssdMenuEntryUssdUserPK ussdMenuEntryUssdUserPK) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery(" SELECT m FROM UssdMenuEntryUssdUser m "
                + " WHERE m.ussdMenuEntryUssdUserPK.ussdmenuentriesId = :menuEntryCod "
                + " AND m.ussdMenuEntryUssdUserPK.ussduserId = :ussduserId");

            q.setParameter("menuEntryCod",
                    ussdMenuEntryUssdUserPK.getUssdmenuentriesId());
            q.setParameter("ussduserId",
                    ussdMenuEntryUssdUserPK.getUssduserId());
            return (UssdMenuEntryUssdUser) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<UssdMenuEntryUssdUser> findByUssduserId(Long ussduserId) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createNamedQuery("UssdMenuEntryUssdUser.findByUssduserId");
            query.setParameter("ussduserId", ussduserId);
            return query.getResultList();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<UssdMenuEntry> findByUserAndService(Userphone userphone, Service service) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery("SELECT m FROM UssdMenuEntry m , in (m.ussdServiceCorrespList) c "
                + "WHERE m.id in ( "
                + "         SELECT l.ussdMenuEntryUssdUserPK.ussdmenuentriesId FROM Userphone u , "
                + "             in (u.ussdUser.ussdMenuEntryUssdUserList) l "
                + "         WHERE l.ussdUser.id = :userphoneCod )"
                + "AND c.service = :service");

            q.setParameter("userphoneCod", userphone.getUserphoneCod());
            q.setParameter("service", service);
            return q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    /*
     * 
     */
    public boolean removeByUser(Userphone userphone) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery("delete FROM UssdMenuEntryUssdUser u "
                + " WHERE u.ussdUser.id = :userphoneCod"
                + " AND u.ussdMenuEntryUssdUserPK.ussdmenuentriesId  in ("
                + " select  usc.ussdServiceCorrespPK.ussdmenuentriesId from UssdServiceCorresp usc"
                + " where usc.ussdServiceCorrespPK.ussdmenuentriesId = u.ussdMenuEntryUssdUserPK.ussdmenuentriesId)");
            q.setParameter("userphoneCod", userphone.getUserphoneCod());
            q.executeUpdate();
        } catch (Exception e) {
            return false;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
        return true;
    }

    public boolean addUssMenuEntryByUserphone(Userphone userphone, List<UssdMenuEntry> ussdMenuEntries) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            UssdUser ussdUser = getFacadeContainer().getUssdUserAPI().find(
                    userphone.getUserphoneCod());
            for (UssdMenuEntry ussdMenuEntry : ussdMenuEntries) {
                UssdMenuEntryUssdUser ussdMenuEntryUssdUser = new UssdMenuEntryUssdUser();
                UssdMenuEntryUssdUserPK ussdMenuEntryUssdUserPK = new UssdMenuEntryUssdUserPK();
                ussdMenuEntryUssdUserPK.setUssdmenuentriesId(ussdMenuEntry.getId());
                ussdMenuEntryUssdUserPK.setUssduserId(ussdUser.getId());
                ussdMenuEntryUssdUser.setUssdUser(ussdUser);
                ussdMenuEntryUssdUser.setUssdMenuEntryUssdUserPK(ussdMenuEntryUssdUserPK);
                create(ussdMenuEntryUssdUser);
            }
        } catch (Exception e) {
            return false;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
        return true;
    }

    public boolean removeByClientAndService(Client client, List<Service> service) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            if (service != null && service.size() > 0) {
                Query q = em.createQuery("DELETE FROM UssdMenuEntryUssdUser u "
                    + "WHERE u.ussdMenuEntryUssdUserPK.ussduserId IN ("
                    + "             SELECT up.userphoneCod FROM Userphone up "
                    + "             WHERE up.client = :client )"
                    + "AND u.ussdMenuEntryUssdUserPK.ussdmenuentriesId IN "
                    + "     (SELECT c.ussdMenuEntry.id FROM UssdServiceCorresp c "
                    + "     WHERE c.service in (:service))");
                q.setParameter("client", client);
                q.setParameter("service", service);
                q.executeUpdate();
                return true;
            }
        } catch (Exception e) {
            return false;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
        return false;
    }

    public List<UssdMenuEntryUssdUser> findByMenuEntry(Long menuEntryCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery(" SELECT m FROM UssdMenuEntryUssdUser m "
                + " WHERE m.ussdMenuEntryUssdUserPK.ussdmenuentriesId = :menuEntryCod ");

            q.setParameter("menuEntryCod", menuEntryCod);
            return q.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<UssdMenuEntryUssdUser>();
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    /**
     * Metodo para INSERTAR o ELIMINAR entradas en la tabla
     * USSD_MENU_ENTRY_USSD_USER en caso de que se tenga consulta de Datos o
     * creacion de Datos.
     * 
     * */

    public void insertUssdEntriesForUser(Long ussdUserId, List<Long> idList) {
        try {
            for (Long id : idList) {
                UssdMenuEntryUssdUserPK pk = new UssdMenuEntryUssdUserPK();
                pk.setUssdmenuentriesId(id);
                pk.setUssduserId(ussdUserId);
                UssdMenuEntryUssdUser entryUser = getFacadeContainer().getUssdMenuEntryUssdUserAPI().findByMenuEntryUssdUser(
                        pk);
                if (entryUser == null) {
                    UssdUser user = getFacadeContainer().getUssdUserAPI().find(
                            ussdUserId);
                    UssdMenuEntryUssdUser menuEntryUssdUser = new UssdMenuEntryUssdUser();
                    menuEntryUssdUser.setUssdMenuEntryUssdUserPK(pk);
                    menuEntryUssdUser.setUssdUser(user);
                    getFacadeContainer().getUssdMenuEntryUssdUserAPI().create(
                            menuEntryUssdUser);
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void deleteUssdEntriesForUser(Long ussdUserId, List<Long> idList) {
        try {
            for (Long id : idList) {
                UssdMenuEntryUssdUserPK pk = new UssdMenuEntryUssdUserPK();
                pk.setUssdmenuentriesId(id);
                pk.setUssduserId(ussdUserId);
                UssdMenuEntryUssdUser entryUser = getFacadeContainer().getUssdMenuEntryUssdUserAPI().findByMenuEntryUssdUser(
                        pk);
                if (entryUser != null) {
                    getFacadeContainer().getUssdMenuEntryUssdUserAPI().remove(
                            entryUser);
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
