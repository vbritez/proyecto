package com.tigo.cs.api.facade;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.Classification;
import com.tigo.cs.domain.ReportConfig;
import com.tigo.cs.domain.ReportConfigMail;
import com.tigo.cs.domain.Screenclient;

public abstract class ReportConfigAPI extends AbstractAPI<ReportConfig> {

    public ReportConfigAPI() {
        super(ReportConfig.class);
    }

    public Screenclient getScreenclientListByDescription(String descriptionChr) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT sc " + "FROM Screenclient sc "
                + "WHERE sc.descriptionChr = :descriptionChr ");
            query.setParameter("descriptionChr", descriptionChr);
            return (Screenclient) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public String saveEditing(ReportConfig entity) throws GenericFacadeException {
        try {
            if (entity.getReportConfigCod() == null) {
                super.create(entity);
                for (ReportConfigMail mail : entity.getReportConfigMails()) {
                    mail.setReportConfig(entity);
                    getFacadeContainer().getReportConfigMailAPI().create(mail);
                }
            } else {
                ReportConfig newEntity = new ReportConfig();
                newEntity.setClassifications(entity.getClassifications());
                newEntity.setClient(entity.getClient());
                newEntity.setConfigTypeChr(entity.getConfigTypeChr());
                newEntity.setDayNum(entity.getDayNum());
                newEntity.setDescriptionChr(entity.getDescriptionChr());
                newEntity.setNextExecutionTimeDat(entity.getNextExecutionTimeDat());
                newEntity.setReportTypeChr(entity.getReportTypeChr());
                newEntity.setScreenclients(entity.getScreenclients());
                newEntity.setStartTimeDat(entity.getStartTimeDat());
                newEntity.setStatusChr(entity.getStatusChr());
                newEntity.setTimeDat(entity.getTimeDat());
                newEntity.setUserweb(entity.getUserweb());

                super.create(newEntity);
                for (ReportConfigMail mail : entity.getReportConfigMails()) {
                    mail.setReportConfig(newEntity);
                    getFacadeContainer().getReportConfigMailAPI().create(mail);
                }

                List<ReportConfigMail> oldMailList = getReportConfigMailList(entity.getReportConfigCod());
                for (ReportConfigMail old : oldMailList) {
                    getFacadeContainer().getReportConfigMailAPI().remove(old);
                }
                super.remove(entity);

                // super.edit(entity);
            }
        } catch (Exception e) {

            throw new GenericFacadeException(ReportConfigAPI.class, getFacadeContainer().getI18nAPI().iValue(
                    "web.admin.backingBean.commons.message.AnErrorHasOcurred"), e);

        }
        return null;
    }

    public List<ReportConfigMail> getReportConfigMailList(Long reportConfigCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT r.reportConfigMails "
                + "FROM ReportConfig r "
                + "WHERE r.reportConfigCod = :reportConfigCod ");
            query.setParameter("reportConfigCod", reportConfigCod);
            return (List<ReportConfigMail>) query.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<ReportConfigMail>();
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<Screenclient> getReportConfigScreenclientList(Long reportConfigCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT r.screenclients "
                + "FROM ReportConfig r "
                + "WHERE r.reportConfigCod = :reportConfigCod ");
            query.setParameter("reportConfigCod", reportConfigCod);
            return (List<Screenclient>) query.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<Screenclient>();
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<Classification> getReportConfigClassificationList(Long reportConfigCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT r.classifications "
                + "FROM ReportConfig r "
                + "WHERE r.reportConfigCod = :reportConfigCod ");
            query.setParameter("reportConfigCod", reportConfigCod);
            return (List<Classification>) query.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<Classification>();
        }
    }

    public void deleteEntity(ReportConfig entity) throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            ReportConfig reportConfig;
            try {
                reportConfig = em.find(ReportConfig.class,
                        entity.getReportConfigCod());
            } catch (EntityNotFoundException enfe) {
                throw new Exception("The reportConfig with id "
                    + entity.getReportConfigCod() + " no longer exists.", enfe);
            }
            reportConfig.setStatusChr(false);
            super.edit(reportConfig);
        } catch (Exception e) {
            throw new GenericFacadeException(ReportConfigAPI.class, MessageFormat.format(
                    getFacadeContainer().getI18nAPI().iValue(
                            "web.client.backingBean.message.Error"), e));
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }
}
