package com.tigo.cs.api.facade;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.util.DateUtil;
import com.tigo.cs.domain.Client;
import com.tigo.cs.domain.TrackingConfiguration;
import com.tigo.cs.domain.TrackingConfigurationPK;
import com.tigo.cs.domain.TrackingPeriod;
import com.tigo.cs.domain.Userphone;

public abstract class TrackingConfigurationAPI extends AbstractAPI<TrackingConfiguration> {

    public TrackingConfigurationAPI() {
        super(TrackingConfiguration.class);
    }

    public String saveEditing(TrackingPeriod entity, Long clientCod, Long userphoneCod) throws GenericFacadeException {
        try {
            Object obj = entity.getTrackingPeriodCod();

            /*
             * VERIFICAR QUE EL PERIODO NO TENGA CONFLICTO CON PERIODOS DE OTROS
             * USERPHONES ASIGNADOS AL MISMO PERIODO
             */
            String result = null;
            if (obj != null) {
                TrackingPeriod tp = getFacadeContainer().getTrackingPeriodAPI().find(
                        obj);
                if (tp != null) {
                    List<Userphone> uList = getFacadeContainer().getTrackingPeriodAPI().getUserphoneListByTrackingPeriod(
                            tp.getTrackingPeriodCod());
                    if (uList != null && uList.size() > 0) {
                        for (TrackingConfiguration tc : entity.getTrackingConfigurations()) {
                            tc.setTrackingPeriod(entity);
                            for (Userphone u : uList) {
                                result = verifyConfigurationConflicts(
                                        u.getUserphoneCod(), tc);
                                if (result != null)
                                    break;
                            }
                        }
                    }
                }
            }
            if (result != null) {
                return result;
            }

            if (obj == null) {
                /*
                 * GUARDAR EL SHIFT PERIOD
                 */
                TrackingPeriod sp = getFacadeContainer().getTrackingPeriodAPI().create(
                        entity);
                /*
                 * TRAER USUARIO TELEFONICO
                 */
                if (userphoneCod != null) {
                    Userphone u = getFacadeContainer().getUserphoneAPI().find(
                            userphoneCod);
                    if (sp.getUserphones() == null)
                        sp.setUserphones(new ArrayList<Userphone>());
                    sp.getUserphones().add(u);
                }

                for (TrackingConfiguration sc : entity.getTrackingConfigurations()) {
                    /*
                     * ShiftConfigurationPK pk = new ShiftConfigurationPK();
                     * sc.setShiftConfigurationPK(pk);
                     */

                    sc.setTrackingPeriod(sp);
                    create(sc);
                }
            } else {
                List<TrackingConfiguration> tcList = getTrackingConfListByTrackingPeriod(entity.getTrackingPeriodCod());

                for (TrackingConfiguration sc : tcList) {
                    TrackingConfiguration sc_ = this.find(sc.getId());
                    this.remove(sc_);
                }
                for (TrackingConfiguration sc : entity.getTrackingConfigurations()) {
                    sc.setTrackingPeriod(entity);
                    create(sc);
                }
                getFacadeContainer().getTrackingPeriodAPI().edit(entity);
                // this.edit(entity);
            }
        } catch (Exception e) {
            throw new GenericFacadeException(TrackingConfigurationAPI.class, MessageFormat.format(
                    getFacadeContainer().getI18nAPI().iValue(
                            "web.client.backingBean.message.Error"), e));
        }

        return null;
    }

    @Override
    public TrackingConfiguration create(TrackingConfiguration entity) {
        TrackingConfiguration tc = (TrackingConfiguration) entity;
        TrackingConfigurationPK pk = new TrackingConfigurationPK();
        tc.setId(pk);
        tc.getId().setCodTrackingPeriod(
                tc.getTrackingPeriod().getTrackingPeriodCod());
        tc.getId().setTrackingConfigurationCod(
                getTrackingConfigurationNextval());
        return (TrackingConfiguration) super.create(tc);
    }

    public Long getTrackingConfigurationNextval() {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createNativeQuery("SELECT TRACKING_CONFIGURATION_SEQ.nextval FROM DUAL");
            BigDecimal nextVal = (BigDecimal) q.getSingleResult();
            return nextVal.longValue();
        } catch (Exception e) {
            throw null;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<TrackingConfiguration> getTrackingConfListByTrackingPeriod(Long trackingperiodCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery("SELECT tp.trackingConfigurations "
                + " FROM TrackingPeriod tp "
                + " WHERE tp.trackingPeriodCod = :trackingPeriodCod");
            q.setParameter("trackingPeriodCod", trackingperiodCod);
            return (List<TrackingConfiguration>) q.getResultList();
        } catch (Exception e) {
            throw null;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<TrackingConfiguration> getTrackingConfListByTrackingPeriodStatus(Long trackingperiodCod, Boolean status) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery("SELECT tcList"
                + " FROM TrackingPeriod tp, "
                + " in (tp.trackingConfigurations) tcList"
                + " WHERE tp.trackingPeriodCod = :trackingPeriodCod"
                + " AND scList.status = :status "
                + " AND scList.running = false ");
            q.setParameter("trackingPeriodCod", trackingperiodCod);
            q.setParameter("status", status);
            return (List<TrackingConfiguration>) q.getResultList();
        } catch (Exception e) {
            throw null;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    private Long convertHourToMinutes(Date duration) {
        String hour = DateUtil.getFormattedDate(duration, "HH:mm");
        String[] hhmm = hour.split(":");
        Long minutes = Long.valueOf(hhmm[0]) * 60 + Long.valueOf(hhmm[1]);

        return minutes;
    }

    public String verifyConfigurationConflicts(Long userphoneCod, TrackingConfiguration tc) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            String whereDays = "";
            for (int i = 0; i < tc.getDays().length(); i++) {
                whereDays += "sc.days like '%" + tc.getDays().charAt(i)
                    + "%' or ";
            }
            whereDays = whereDays.substring(0, whereDays.length() - 3);

            Long hourFrom = convertHourToMinutes(tc.getStartTime());
            Long hourTo = hourFrom + tc.getDuration() + tc.getToleranceTime();

            String notInStr = "";
            if (tc.getTrackingPeriod().getTrackingPeriodCod() != null)
                notInStr = " and sc.trackingPeriod.trackingPeriodCod not in ("
                    + tc.getTrackingPeriod().getTrackingPeriodCod() + ") ";

            Query q = em.createQuery("select sc from TrackingConfiguration sc, in (sc.trackingPeriod.userphones) m "
                + " where (sc.trackingPeriod.dateFrom <= :dateTo "
                + " and :dateFrom <= sc.trackingPeriod.dateTo) "
                + " and ("
                + whereDays
                + ")"
                + " and (sc.startMinutes <= :hourTo "
                + " and :hourFrom <= (sc.startMinutes + sc.duration + sc.toleranceTime)) "
                + " and m.userphoneCod = :userphoneCod "
                + notInStr
                + " and sc.status = true "
                + " and sc.trackingPeriod.status = true ");
            q.setParameter("dateFrom", tc.getTrackingPeriod().getDateFrom());
            q.setParameter("dateTo", tc.getTrackingPeriod().getDateTo());
            q.setParameter("hourFrom", hourFrom);
            q.setParameter("hourTo", hourTo);
            q.setParameter("userphoneCod", userphoneCod);

            List<TrackingConfiguration> result = (List<TrackingConfiguration>) q.getResultList();
            String message = "";
            if (result != null && result.size() > 0) {
                for (TrackingConfiguration trackC : result) {
                    message += MessageFormat.format(
                            getFacadeContainer().getI18nAPI().iValue(
                                    "web.client.trackingconf.service.message.trackingConflictsMessage"),
                            userphoneCod,
                            trackC.getTrackingPeriod().getDescription());
                }
                return message;
            } else
                return null;
        } catch (Exception e) {
            throw null;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public TrackingConfiguration getConfiguration(Client client, String codUserphone, Date dateinDat) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            /*
             * recuperamos la configuracion del guardia para la fecha
             */
            String jpql = "SELECT c FROM TrackingConfiguration c, IN (c.trackingPeriod.userphones) m "
                + "WHERE c.status               = true "
                + "AND c.trackingPeriod.status  = true "
                + "AND m.userphoneCod           = :codUserphone "
                + "AND :dateinDat between c.nextExecutionTime and c.nextExecutionToleranceTime "
                + "AND :dateinDat between c.trackingPeriod.dateFrom and c.trackingPeriod.dateTo";

            Query q = em.createQuery(jpql);
            q.setParameter("client", client.getClientCod());
            q.setParameter("codUserphone", codUserphone);
            q.setParameter("dateinDat", dateinDat);

            return (TrackingConfiguration) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public Boolean conflictInPeriod(Long userphoneCod, Long trackingPeriodCod) {
        List<TrackingConfiguration> scList = getTrackingConfListByTrackingPeriod(trackingPeriodCod);
        for (TrackingConfiguration sc : scList) {
            if (verifyConfigurationConflicts(userphoneCod, sc) != null)
                return true;
        }

        return false;
    }    
    
    
//    @Override
//    public TrackingConfiguration edit(TrackingConfiguration entity) {
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
