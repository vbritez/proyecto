package com.tigo.cs.api.facade;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.util.DateUtil;
import com.tigo.cs.domain.Client;
import com.tigo.cs.domain.MetaData;
import com.tigo.cs.domain.ShiftConfiguration;
import com.tigo.cs.domain.ShiftConfigurationPK;
import com.tigo.cs.domain.ShiftPeriod;

/**
 * 
 * @author Miguel Zorrilla
 */
public abstract class ShiftConfigurationAPI extends AbstractAPI<ShiftConfiguration> {

    public ShiftConfigurationAPI() {
        super(ShiftConfiguration.class);
    }

    public String saveEditing(ShiftPeriod entity, Long clientCod, String guardCod) throws GenericFacadeException {
        try {
            Object obj = entity.getShiftPeriodCod();

            /*
             * VERIFICAR QUE EL PERIODO NO TENGA CONFLICTO CON PERIODOS DE OTROS
             * GUARDIAS ASIGNADOS AL MISMO PERIODO
             */
            String result = null;
            if (obj != null) {
                ShiftPeriod sp = getFacadeContainer().getShiftPeriodAPI().find(
                        obj);
                if (sp != null) {
                    List<MetaData> mdList = getFacadeContainer().getShiftPeriodAPI().getMetaDatalistByShiftPeriod(
                            sp.getShiftPeriodCod());
                    if (mdList != null && mdList.size() > 0) {
                        for (ShiftConfiguration sc : entity.getShiftConfigurations()) {
                            sc.setShiftPeriod(entity);
                            for (MetaData md : mdList) {
                                result = verifyConfigurationConflicts(
                                        md.getMetaDataPK().getCodeChr(), sc);
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

            /*
             * VERIFICAR QUE EL PERIODO NO TENGA CONFLICTO CON CONF DE OTROS
             * GUARDIAS SI RESULT ES DISTINTO DE CERO, EXISTE CONFLICTO.
             * RETORNA.
             */
            result = null;
            for (ShiftConfiguration sc : entity.getShiftConfigurations()) {
                sc.setShiftPeriod(entity);
                result = verifyConfigurationConflicts(guardCod, sc);
                if (result != null)
                    break;
            }
            if (result != null)
                return result;

            if (obj == null) {
                /*
                 * GUARDAR EL SHIFT PERIOD
                 */
                ShiftPeriod sp = getFacadeContainer().getShiftPeriodAPI().create(
                        entity);
                /*
                 * TRAER META DEL GUARDIA
                 */
                MetaData m = getFacadeContainer().getMetaDataAPI().findByClientMetaMemberAndCode(
                        clientCod, 4L, 1L, guardCod);
                if (sp.getMetaDatas() == null)
                    sp.setMetaDatas(new ArrayList<MetaData>());
                sp.getMetaDatas().add(m);

                for (ShiftConfiguration sc : entity.getShiftConfigurations()) {
                    /*
                     * ShiftConfigurationPK pk = new ShiftConfigurationPK();
                     * sc.setShiftConfigurationPK(pk);
                     */

                    sc.setShiftPeriod(sp);
                    create(sc);
                }
            } else {
                List<ShiftConfiguration> scList = getShiftConfListByShiftPeriod(entity.getShiftPeriodCod());

                for (ShiftConfiguration sc : scList) {
                    ShiftConfiguration sc_ = this.find(sc.getShiftConfigurationPK());
                    this.remove(sc_);
                }
                for (ShiftConfiguration sc : entity.getShiftConfigurations()) {
                    sc.setShiftPeriod(entity);
                    create(sc);
                }
                getFacadeContainer().getShiftPeriodAPI().edit(entity);
                // this.edit(entity);
            }
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), MessageFormat.format(
                    getFacadeContainer().getI18nAPI().iValue(
                            "web.client.backingBean.message.Error"), e));
        }

        return null;
    }

    @Override
    public ShiftConfiguration create(ShiftConfiguration entity) {
        ShiftConfiguration sc = (ShiftConfiguration) entity;
        ShiftConfigurationPK pk = new ShiftConfigurationPK();
        sc.setShiftConfigurationPK(pk);
        sc.getShiftConfigurationPK().setCodShiftPeriod(
                sc.getShiftPeriod().getShiftPeriodCod());
        sc.getShiftConfigurationPK().setShiftConfigurationCod(
                getShiftConfigurationNextval());
        return (ShiftConfiguration) super.create(sc);
    }

    public Long getShiftConfigurationNextval() {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createNativeQuery("SELECT SHIFT_CONFIGURATION_SEQ.nextval FROM DUAL");
            BigDecimal nextVal = (BigDecimal) q.getSingleResult();
            return nextVal.longValue();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<ShiftConfiguration> getShiftConfListByShiftPeriod(Long shiftperiodCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery("SELECT sp.shiftConfigurations "
                + " FROM ShiftPeriod sp "
                + " WHERE sp.shiftPeriodCod = :shiftperioCod");
            q.setParameter("shiftperioCod", shiftperiodCod);
            return (List<ShiftConfiguration>) q.getResultList();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<ShiftConfiguration> getShiftConfListByShiftPeriodStatus(Long shiftperiodCod, Boolean status) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery("SELECT scList" + " FROM ShiftPeriod sp, "
                + " in (sp.shiftConfigurations) scList"
                + " WHERE sp.shiftPeriodCod = :shiftperioCod"
                + " AND scList.status = :status "
                + " AND scList.running = false ");
            q.setParameter("shiftperioCod", shiftperiodCod);
            q.setParameter("status", status);
            return (List<ShiftConfiguration>) q.getResultList();
        } catch (Exception e) {
            return null;
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

    public String verifyConfigurationConflicts(String guardCod, ShiftConfiguration sc) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            String whereDays = "";
            for (int i = 0; i < sc.getDays().length(); i++) {
                whereDays += "sc.days like '%" + sc.getDays().charAt(i)
                    + "%' or ";
            }
            whereDays = whereDays.substring(0, whereDays.length() - 3);

            Long hourFrom = convertHourToMinutes(sc.getStartTime());
            Long hourTo = hourFrom + sc.getDuration() + sc.getToleranceTime();

            String notInStr = "";
            if (sc.getShiftPeriod().getShiftPeriodCod() != null)
                notInStr = " and sc.shiftPeriod.shiftPeriodCod not in ("
                    + sc.getShiftPeriod().getShiftPeriodCod() + ") ";

            Query q = em.createQuery("select sc from ShiftConfiguration sc, in (sc.shiftPeriod.metaDatas) m "
                + " where (sc.shiftPeriod.dateFrom <= :dateTo "
                + " and :dateFrom <= sc.shiftPeriod.dateTo) "
                + " and ("
                + whereDays
                + ")"
                + " and (sc.startMinutes <= :hourTo "
                + " and :hourFrom <= (sc.startMinutes + sc.duration + sc.toleranceTime)) "
                + " and m.metaDataPK.codeChr = :guardCod "
                + notInStr
                + " and sc.status = true "
                + " and sc.shiftPeriod.status = true ");
            q.setParameter("dateFrom", sc.getShiftPeriod().getDateFrom());
            q.setParameter("dateTo", sc.getShiftPeriod().getDateTo());
            q.setParameter("hourFrom", hourFrom);
            q.setParameter("hourTo", hourTo);
            q.setParameter("guardCod", guardCod);

            List<ShiftConfiguration> result = (List<ShiftConfiguration>) q.getResultList();
            String message = "";
            if (result != null && result.size() > 0) {
                for (ShiftConfiguration shiftC : result) {
                    message += MessageFormat.format(
                            getFacadeContainer().getI18nAPI().iValue(
                                    "web.client.guardroundconf.service.message.guardConflictsMessage"),
                            guardCod, shiftC.getShiftPeriod().getDescription());
                }
                return message;
            } else
                return null;
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public ShiftConfiguration getConfiguration(Client client, String codGuard, Date dateinDat) {

        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            /*
             * recuperamos la configuracion del guardia para la fecha
             */
            String jpql = "SELECT c FROM ShiftConfiguration c, IN (c.shiftPeriod.metaDatas) m "
                + "WHERE c.status               = true "
                + "AND c.shiftPeriod.status     = true "
                + "AND m.metaDataPK.codClient   = :client "
                + "AND m.metaDataPK.codMeta     = 4 "
                + "AND m.metaDataPK.codMember   = 1 "
                + "AND m.metaDataPK.codeChr     = :codGuard "
                + "AND :dateinDat between c.nextExecutionTime and c.nextExecutionToleranceTime "
                + "AND :dateinDat between c.shiftPeriod.dateFrom and c.shiftPeriod.dateTo";

            Query q = em.createQuery(jpql);
            q.setParameter("client", client.getClientCod());
            q.setParameter("codGuard", codGuard);
            q.setParameter("dateinDat", dateinDat);

            return (ShiftConfiguration) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<MetaData> guardWithNoMarkingOnShift(ShiftConfiguration shiftConfiguration) {

        /*
         * No es necesario filtrar por cliente, ya que una configuracion esta
         * relacionada solamente a un cliente, a traves del periodo, y este, a
         * traves de los metadatas
         */
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery("SELECT m FROM ShiftPeriod sp, "
                + "IN (sp.metaDatas) m "
                + "WHERE sp.shiftPeriodCod = :shiftPeriodCod "
                + "AND m.metaDataPK.codMeta = 4 "
                + "AND m.metaDataPK.codMember = 1 "
                + "AND not exists ("
                + "                 SELECT sv FROM ServiceValue sv, "
                + "					IN (sv.serviceValueDetailCollection) svd "
                + "                 WHERE sv.service.serviceCod = 15"
                + "                 AND sv.column2Chr = :shiftPeriodCodStr"
                + "                 AND sv.column3Chr = :shiftConfCodStr"
                + "                 AND sv.userphone.client = m.metaDataPK.codClient"
                + "                 AND svd.recorddateDat between :nextExec and  :nextExecTolerance"
                + "                 AND sv.column1Chr = m.metaDataPK.codeChr"
                + "                 AND sv.enabledChr = true  "
                + "                   )");

            q.setParameter("shiftPeriodCod",
                    shiftConfiguration.getShiftPeriod().getShiftPeriodCod());
            q.setParameter(
                    "shiftPeriodCodStr",
                    shiftConfiguration.getShiftPeriod().getShiftPeriodCod().toString());
            q.setParameter(
                    "shiftConfCodStr",
                    shiftConfiguration.getShiftConfigurationPK().getShiftConfigurationCod().toString());
            q.setParameter("nextExec",
                    shiftConfiguration.getNextExecutionTime(),
                    TemporalType.TIMESTAMP);
            q.setParameter("nextExecTolerance",
                    shiftConfiguration.getNextExecutionToleranceTime(),
                    TemporalType.TIMESTAMP);

            return (List<MetaData>) q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }

    }

    public Boolean conflictInPeriod(String guardCod, Long shiftPeriodCod) {
        List<ShiftConfiguration> scList = getShiftConfListByShiftPeriod(shiftPeriodCod);
        for (ShiftConfiguration sc : scList) {
            if (verifyConfigurationConflicts(guardCod, sc) != null)
                return true;
        }

        return false;
    } 
}
