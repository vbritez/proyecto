package com.tigo.cs.api.facade;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.Moduleclient;
import com.tigo.cs.domain.Screenclient;

public abstract class ModuleclientAPI extends AbstractAPI<Moduleclient> {

    public ModuleclientAPI() {
        super(Moduleclient.class);
    }

    public List<Moduleclient> getActiveModules() throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            Query query = em.createQuery("SELECT DISTINCT m "
                + "FROM Moduleclient m " + "WHERE m.moduleclientCod > 0 "
                + "AND SIZE(m.screenclientList) > 0  " + "ORDER BY m.orderNum");
            return (List<Moduleclient>) query.getResultList();
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<Screenclient> getScreenclientListByModuleAndService(Long codModule, Long codService) throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            Query query = em.createQuery("SELECT DISTINCT sc "
                + "FROM Screenclient sc "
                + "WHERE sc.codModuleclient.moduleclientCod = :codModule "
                + "AND sc.service.serviceCod = :codService "
                + "AND sc.showonmenuChr = '1' " + "ORDER BY sc.orderNum");
            query.setParameter("codModule", codModule);
            query.setParameter("codService", codService);
            return (List<Screenclient>) query.getResultList();
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    /**
     * Nuevo algoritmo para cambios de seguridad y acceso a la aplicación
     */
    public List<Screenclient> getScreenclientListByModuleAndUserweb(Long codModuleClient, Long codUserweb, boolean isAdmin) throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            if (codModuleClient != 4) {
                Query query = em.createQuery("SELECT DISTINCT sc "
                    + "FROM Screenclient sc, "
                    + "IN (sc.roleClientScreenList) rcsList, "
                    + "IN (rcsList.roleClient.userwebList) uList "
                    + "WHERE sc.codModuleclient.moduleclientCod = :codModuleClient "
                    + "AND rcsList.accessible = 1 "
                    + "AND sc.showonmenuChr = '1' "
                    + "AND uList.userwebCod = :codUserweb "
                    + "AND rcsList.roleClient.enabled = 1 "
                    + "ORDER BY sc.orderNum");
                query.setParameter("codModuleClient", codModuleClient);
                query.setParameter("codUserweb", codUserweb);
                return (List<Screenclient>) query.getResultList();
            } else if (isAdmin) {
                // Se agregan las pantallas administrativas
                Query query = em.createQuery("SELECT DISTINCT sc "
                    + "FROM Screenclient sc "
                    + "WHERE sc.codModuleclient.moduleclientCod = :codModuleClient "
                    + "AND sc.showonmenuChr = '1' " + "ORDER BY sc.orderNum");
                query.setParameter("codModuleClient", codModuleClient);
                return (List<Screenclient>) query.getResultList();
            }
            return new ArrayList<Screenclient>();
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<Screenclient> getScreenclientListByModuleAndMeta(Long codModule, Long codMeta) throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            Query query = em.createQuery("SELECT DISTINCT sc "
                + "FROM Screenclient sc "
                + "WHERE sc.codModuleclient.moduleclientCod = :codModule "
                + "AND sc.meta.metaCod = :codMeta "
                + "AND sc.showonmenuChr = '1' " + "ORDER BY sc.orderNum");
            query.setParameter("codModule", codModule);
            query.setParameter("codMeta", codMeta);
            return (List<Screenclient>) query.getResultList();
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<Screenclient> getScreenclientListByModule(Long codModule) throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            Query query = em.createQuery("SELECT DISTINCT sc "
                + "FROM Screenclient sc "
                + "WHERE sc.codModuleclient.moduleclientCod = :codModule "
                + "AND sc.service = null " + "AND sc.meta = null "
                + "AND sc.showonmenuChr = '1' " + "ORDER BY sc.orderNum");
            query.setParameter("codModule", codModule);
            return (List<Screenclient>) query.getResultList();
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }
}
