package com.tigo.cs.api.facade;

import java.text.MessageFormat;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.log.Action;
import com.tigo.cs.domain.TrackingConfiguration;
import com.tigo.cs.domain.TrackingPeriod;
import com.tigo.cs.domain.Userphone;

public abstract class TrackingPeriodAPI extends AbstractAPI<TrackingPeriod> {

    public TrackingPeriodAPI() {
        super(TrackingPeriod.class);
    }

    public List<Userphone> getUserphoneListByTrackingPeriod(Long trackingperiodCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery("SELECT tp.userphones "
                + " FROM TrackingPeriod tp "
                + " WHERE tp.trackingPeriodCod = :trackingPeriodCod");
            q.setParameter("trackingPeriodCod", trackingperiodCod);
            return (List<Userphone>) q.getResultList();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<TrackingPeriod> getTrackingPeriodListByUserphone(Long userphoneCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery("SELECT sp "
                + " FROM TrackingPeriod sp, in (sp.userphones) m "
                + " WHERE m.userphoneCod = :userphoneCod ");
            // + " AND sp.status = true ");
            q.setParameter("userphoneCod", userphoneCod);
            return (List<TrackingPeriod>) q.getResultList();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    /*
     * Este metodo cambia el estado de los shiftConfiguration y ShiftPeriod a
     * false, NO borra fisicamente.
     */
    public void deleteEntity(Long trackingperiodCod) throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            TrackingPeriod trackingPeriod;
            try {
                trackingPeriod = em.find(TrackingPeriod.class,
                        trackingperiodCod);
            } catch (EntityNotFoundException enfe) {
                throw new Exception("The trackingPeriod with id "
                    + trackingperiodCod + " no longer exists.", enfe);
            }
            List<TrackingConfiguration> tcList = getFacadeContainer().getTrackingConfigurationAPI().getTrackingConfListByTrackingPeriod(
                    trackingperiodCod);
            for (TrackingConfiguration tc : tcList) {
                tc.setStatus(false);
                getFacadeContainer().getTrackingConfigurationAPI().edit(tc);
            }
            trackingPeriod.setStatus(false);
            edit(trackingPeriod);            
        } catch (Exception e) {
            throw new GenericFacadeException(TrackingPeriodAPI.class, MessageFormat.format(
                    getFacadeContainer().getI18nAPI().iValue(
                            "web.client.backingBean.message.Error"), e));
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }
    

//    @Override
//    public TrackingPeriod edit(TrackingPeriod entity) {
//        EntityManager em = null;
//        try {
//            em = getFacadeContainer().getEntityManager();
//            if (getFacadeContainer().isEntityManagerTransactional()) {
//                em.getTransaction().begin();
//            }
//            em.merge(entity);
//            if (getFacadeContainer().isEntityManagerTransactional()) {
//                em.getTransaction().commit();
//            }
//            return entity;
//        } catch (Exception e) {
//            getFacadeContainer().getNotifier().error(getClass(),
//                    e.getMessage(), e);
//            if (getFacadeContainer().isEntityManagerTransactional()) {
//                em.getTransaction().rollback();
//            }
//            return null;
//        } finally {
//            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
//                em.close();
//            }
//        }
//    }
}