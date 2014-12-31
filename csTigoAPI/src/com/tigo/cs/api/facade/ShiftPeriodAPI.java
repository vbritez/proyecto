package com.tigo.cs.api.facade;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.log.Action;
import com.tigo.cs.domain.Classification;
import com.tigo.cs.domain.MetaData;
import com.tigo.cs.domain.ShiftConfiguration;
import com.tigo.cs.domain.ShiftPeriod;

/**
 * 
 * @author Miguel Zorrilla
 */
public abstract class ShiftPeriodAPI extends AbstractAPI<ShiftPeriod> {

    public ShiftPeriodAPI() {
        super(ShiftPeriod.class);
    }

    public List<ShiftPeriod> getShiftPeriodByStatus(boolean status) {
        return findListWithNamedQuery("ShiftPeriod.getShiftPeriodByStatus",
                addSingleParam("status", status));
    }

    public List<MetaData> getMetaDatalistByShiftPeriod(Long shiftperiodCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery("SELECT sp.metaDatas "
                + " FROM ShiftPeriod sp "
                + " WHERE sp.shiftPeriodCod = :shiftperioCod");
            q.setParameter("shiftperioCod", shiftperiodCod);
            return (List<MetaData>) q.getResultList();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<ShiftPeriod> getShiftPeriodListByGuard(String guardCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery("SELECT sp "
                + " FROM ShiftPeriod sp, in (sp.metaDatas) m "
                + " WHERE m.metaDataPK.codeChr = :guardCode "
                + " AND sp.status = true ");
            q.setParameter("guardCode", guardCod);
            return (List<ShiftPeriod>) q.getResultList();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    @Override
    public List<ShiftPeriod> findRange(int[] range, String whereCriteria, String orderbyCriteria, List<Classification> classifications) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            String JPQL = "SELECT DISTINCT o FROM ".concat(
                    ShiftPeriod.class.getSimpleName()).concat(" o ");
            if (whereCriteria != null && whereCriteria.trim().length() > 0) {
                JPQL += whereCriteria.trim();
            }
            if (orderbyCriteria != null && orderbyCriteria.trim().length() > 0) {
                JPQL += " ORDER BY " + orderbyCriteria.trim();
            }
            getFacadeContainer().getNotifier().signal(getClass(), Action.INFO,
                    JPQL);
            Query q = em.createQuery(JPQL);
            if (classifications != null) {
                q.setParameter("classifications", classifications);
            }

            // Si el rando fue pasado como parametro entonces, se setea
            // el maxResult y firstResult
            if (range != null) {
                if (range.length > 1) {
                    q.setMaxResults(range[1] - range[0]);
                }
                if (range.length > 0) {
                    q.setFirstResult(range[0]);
                }
            }
            return (List<ShiftPeriod>) q.getResultList();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    @Override
    public Integer count(String whereCriteria) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            String subJPQL = " select distinct sp.SHIFT_PERIOD_COD "
                + " from SHIFT_PERIOD sp "
                + " inner join PERSON_SHIFT_PERIOD psp on (sp.SHIFT_PERIOD_COD = psp.COD_SHIFT_PERIOD) "
                + " inner join meta_data md on psp.COD_CLIENT = md.COD_CLIENT "
                + "		and psp.COD_MEMBER = md.COD_MEMBER "
                + "      and psp.COD_META = md.COD_META "
                + "      and psp.CODE_CHR = md.CODE_CHR, " + " meta_client mc "
                + " where " + " md.COD_CLIENT = mc.COD_CLIENT "
                + " and md.COD_META = mc.COD_META ";

            String JPQL = "SELECT COUNT(*) FROM (" + subJPQL;
            if (whereCriteria != null && whereCriteria.trim().length() > 0) {
                JPQL += whereCriteria.trim();
            }

            JPQL += ")";
            Query q = em.createNativeQuery(JPQL);
            getFacadeContainer().getNotifier().signal(getClass(), Action.INFO,
                    JPQL);

            return ((BigDecimal) q.getSingleResult()).intValue();
        } catch (Exception e) {
            return 0;
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
    public void deleteEntity(Long shiftperiodCod) throws GenericFacadeException {
        EntityManager em = null;
        try {
            ShiftPeriod shiftPeriod;
            try {
                em = getFacadeContainer().getEntityManager();
                shiftPeriod = em.find(ShiftPeriod.class, shiftperiodCod);
                shiftPeriod.getShiftPeriodCod();
            } catch (EntityNotFoundException enfe) {
                throw new Exception("The shiftPeriod with id " + shiftperiodCod
                    + " no longer exists.", enfe);
            }
            /*
             * Client client = shiftPeriod.getClient(); if (client != null) {
             * client.getShiftPeriods().remove(shiftPeriod); client =
             * super.edit(client); } List<MetaData> metaDataList =
             * shiftPeriod.getMetaDatas(); for (MetaData metaDataListMetaData :
             * metaDataList) {
             * metaDataListMetaData.getShiftPeriods().remove(shiftPeriod);
             * metaDataListMetaData = super.edit(metaDataListMetaData); }
             */
            List<ShiftConfiguration> scList = getFacadeContainer().getShiftConfigurationAPI().getShiftConfListByShiftPeriod(
                    shiftperiodCod);
            for (ShiftConfiguration sc : scList) {
                // shiftConfigurationFacade.remove(sc);
                sc.setStatus(false);
                getFacadeContainer().getShiftConfigurationAPI().edit(sc);
            }
            // super.remove(shiftPeriod);
            shiftPeriod.setStatus(false);
            super.edit(shiftPeriod);
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), MessageFormat.format(
                    getFacadeContainer().getI18nAPI().iValue(
                            "web.client.backingBean.message.Error"), e));
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }
}
