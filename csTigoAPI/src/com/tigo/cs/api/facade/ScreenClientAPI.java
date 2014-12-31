package com.tigo.cs.api.facade;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.tigo.cs.commons.jpa.EmptyResultException;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.jpa.MoreThanOneResultException;
import com.tigo.cs.domain.Client;
import com.tigo.cs.domain.ClientServiceFunctionality;
import com.tigo.cs.domain.Meta;
import com.tigo.cs.domain.MetaClient;
import com.tigo.cs.domain.Screenclient;
import com.tigo.cs.domain.Service;

/**
 * 
 * @author Javier Kovacs
 * @version $Id: ScreenClientFacade.java 230 2011-12-21 11:59:10Z miguel.maciel
 *          $
 */

public abstract class ScreenClientAPI extends AbstractAPI<Screenclient> {

    public ScreenClientAPI() {
        super(Screenclient.class);
    }

    public List<Screenclient> getAllScreenclientList() {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            Query query = em.createQuery("SELECT DISTINCT sc "
                + "FROM Screenclient sc " + "WHERE sc.showonmenuChr = true "
                + "ORDER BY sc.descriptionChr ");
            return (List<Screenclient>) query.getResultList();
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<Screenclient> getScreenclientListByClientNoAdministrative(Long idClient) {

        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Client client = getFacadeContainer().getClientAPI().find(idClient);
            List<Screenclient> toReturn = new ArrayList<Screenclient>();

            Query query = em.createQuery("SELECT DISTINCT sc "
                + "FROM Screenclient sc " + "WHERE sc.showonmenuChr = true "
                + "AND sc.codModuleclient.moduleclientCod <> 4 "
                + "ORDER BY sc.descriptionChr ");

            List<Screenclient> scList = (List<Screenclient>) query.getResultList();

            List<Service> sList = new ArrayList<Service>();
            for (ClientServiceFunctionality csf : client.getClientServiceFunctionalityList()) {
                sList.add(csf.getServiceFunctionality().getService());
            }

            List<Meta> mList = new ArrayList<Meta>();
            for (MetaClient m : client.getMetaClientList()) {
                if (m.getEnabledChr()) {
                    mList.add(m.getMeta());
                }
            }

            for (Screenclient sc : scList) {
                if (sc.getService() == null && sc.getMeta() == null) {
                    toReturn.add(sc);
                    continue;
                }
                if (sc.getService() != null && sc.getMeta() != null) {
                    if (sList.contains(sc.getService())
                        && mList.contains(sc.getMeta())) {
                        toReturn.add(sc);
                        continue;
                    }

                }
                if (sc.getService() != null && sc.getMeta() == null) {
                    if (sList.contains(sc.getService())) {
                        toReturn.add(sc);
                        continue;
                    }
                }
                if (sc.getService() == null && sc.getMeta() != null) {
                    if (mList.contains(sc.getMeta())) {
                        toReturn.add(sc);
                        continue;
                    }
                }

            }
            return toReturn;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<Screenclient> getScreensNoSecurityAuthClient() throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            Query query = em.createNamedQuery("Screenclient.findByNosecurityChr");
            query.setParameter("nosecurityChr", true);
            return (List<Screenclient>) query.getResultList();
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public Screenclient findByPage(String page) throws MoreThanOneResultException, GenericFacadeException {
        EntityManager em = null;
        try {

            return this.findEntityWithNamedQuery("Screenclient.findByPageChr",
                    addSingleParam("pageChr", page));
        } catch (EmptyResultException ex) {
            return null;
        } catch (MoreThanOneResultException ex) {
            throw ex;
        } catch (Exception e) {
            throw new GenericFacadeException(getClass());
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

}
