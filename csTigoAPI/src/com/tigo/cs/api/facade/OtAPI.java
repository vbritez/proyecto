package com.tigo.cs.api.facade;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.log.Action;
import com.tigo.cs.domain.Classification;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;
import com.tigo.cs.domain.Userphone;
import com.tigo.cs.domain.Userweb;
import com.tigo.cs.domain.view.DataStatus;
import com.tigo.cs.domain.view.Ot;

/**
 * 
 * @author Miguel Zorrilla
 */
public abstract class OtAPI extends AbstractAPI<Ot> {

    public OtAPI() {
        super(Ot.class);
    }

    private SimpleDateFormat sdFormat;

    @SuppressWarnings("unchecked")
    public List<Ot> getNotFinalizedOts(Long clientCod, String finalizedStatusCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery(""
                + "SELECT o FROM Ot o "
                + "WHERE o.codClient = :clientcod AND o.statusCod NOT LIKE :statusCod");
            query.setParameter("clientcod", clientCod);
            query.setParameter("statusCod", finalizedStatusCod);
            return query.getResultList();
        } catch (NonUniqueResultException e) {
            return null;
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public Ot getActualActivity(Long userphoneCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            GregorianCalendar gc = new GregorianCalendar();
            gc.set(Calendar.HOUR_OF_DAY, 23);
            gc.set(Calendar.MINUTE, 59);
            String dateToStr = getSdFormat().format(gc.getTime());

            Query query = em.createQuery(" SELECT o " + " FROM Ot o "
                + " WHERE o.statusCod IN (1, 2) "
                + " AND o.codUserphone = :codUserphone "
                + " AND o.assignedDate <= to_date('" + dateToStr
                + "', 'dd/MM/yyyy hh24:mi') " + " ORDER BY o.assignedDate ASC ");
            query.setParameter("codUserphone", userphoneCod);
            query.setMaxResults(1);
            return (Ot) query.getSingleResult();
        } catch (NonUniqueResultException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public Ot getLastActivity(Long userphoneCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery(" SELECT o " + " FROM Ot o "
                + " WHERE o.statusCod IN (4, 5) "
                + " AND o.orderNumber IS NOT NULL "
                + " AND o.codUserphone = :codUserphone "
                + " ORDER BY o.orderNumber DESC ");
            query.setParameter("codUserphone", userphoneCod);
            query.setMaxResults(1);
            return (Ot) query.getSingleResult();
        } catch (NonUniqueResultException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public DataStatus getDataStatusByCode(String code, Long clientCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery(" " + " SELECT d "
                + " FROM DataStatus d "
                + " WHERE d.dataPK.codClient = :clientCod "
                + " AND d.dataPK.codigo = :code");
            query.setParameter("clientCod", clientCod);
            query.setParameter("code", code);
            query.setMaxResults(1);
            return (DataStatus) query.getSingleResult();
        } catch (NonUniqueResultException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public Ot nextAsignedOT(Long userphoneCod, Long orderNumber) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("" + " SELECT ot  " + " FROM Ot ot "
                + " WHERE ot.statusCod IN (1, 2) "
                + " AND ot.orderNumber IS NOT NULL "
                + " AND ot.codUserphone = :userphoneCod "
                + " AND ot.orderNumber = ( "
                + "     SELECT min(o.orderNumber)  " + "     FROM Ot o "
                + "     WHERE o.statusCod IN (1, 2) "
                + "     AND o.orderNumber IS NOT NULL "
                + "     AND o.codUserphone = :userphoneCod "
                + "     AND o.orderNumber > :orderNumber " + " ) "
                + " ORDER BY ot.orderNumber ");
            query.setParameter("userphoneCod", userphoneCod);
            query.setParameter("orderNumber", orderNumber);
            return (Ot) query.getSingleResult();
        } catch (NonUniqueResultException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public List<Ot> nextAsignedOTs(Long userphoneCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Ot actualOT = getActualActivity(userphoneCod);

            GregorianCalendar gc = new GregorianCalendar();
            gc.set(Calendar.HOUR_OF_DAY, 23);
            gc.set(Calendar.MINUTE, 59);
            String dateToStr = getSdFormat().format(gc.getTime());

            if (actualOT != null) {
                Query query = em.createQuery("" + " SELECT o  " + " FROM Ot o "
                    + " WHERE o.statusCod IN (1) "
                    + " AND o.codUserphone = :userphoneCod "
                    + " AND o.assignedDate >= :date "
                    + " AND o.assignedDate <= to_date('" + dateToStr
                    + "', 'dd/MM/yyyy hh24:mi') " + " ORDER BY o.assignedDate ");
                query.setParameter("userphoneCod", userphoneCod);
                query.setParameter("date", actualOT.getAssignedDate());
                query.setMaxResults(5);
                return query.getResultList();
            } else {
                return new ArrayList<Ot>();
            }
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public Long getNextOrderNumber(Long userphoneCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery(" " + " SELECT max(o.orderNumber) "
                + " FROM Ot o " + " WHERE o.orderNumber IS NOT NULL "
                + " AND o.codUserphone = :userphoneCod "
                + " ORDER BY o.orderNumber ASC  ");
            query.setParameter("userphoneCod", userphoneCod);

            Long nextVal = (Long) query.getSingleResult();
            if (nextVal != null)
                return nextVal + 1;
            else {
                return 1L;
            }
        } catch (NonUniqueResultException e) {
            e.printStackTrace();
            return 0L;
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public List<ServiceValueDetail> getOtDetails(Long serviceValueCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("" + " SELECT svd "
                + " FROM ServiceValueDetail svd "
                + " WHERE svd.serviceValue.servicevalueCod = :servicevalueCod "
                + " ORDER BY svd.recorddateDat ASC");
            query.setParameter("servicevalueCod", serviceValueCod);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    /**
     * UPDATE SERVICE_VALUE SET COLUMN6_chr = '20/04/2012 09:00' WHERE
     * SERVICEVALUE_COD = 4000211
     * 
     * SE MODIFICO PARA QUE CADA VEZ QUE SE ASIGNA A ALGUN USERPHONE, SE GENERE
     * UN SVD.
     * */
    public void assignDate(Long serviceValueCod, String assignedDate, String endDate, Long userphoneCod, String assignedStatusCod, String observation) {
        ServiceValue sv = getFacadeContainer().getServiceValueAPI().find(
                serviceValueCod);
        sv.setColumn6Chr(assignedDate);
        sv.setColumn7Chr(assignedStatusCod);
        sv.setColumn8Chr(endDate);
        // sv.setColumn10Chr(getNextOrderNumber(userphoneCod).toString());
        Userphone userphone = getFacadeContainer().getUserphoneAPI().find(
                userphoneCod);
        sv.setUserphone(userphone);
        getFacadeContainer().getServiceValueAPI().edit(sv);

        /* se crea svd para cuando se asigna una OT a un userphone */
        ServiceValueDetail svd = new ServiceValueDetail();
        svd.setServiceValue(sv);
        svd.setColumn1Chr(assignedStatusCod);
        svd.setColumn2Chr(observation);
        getFacadeContainer().getServiceValueDetailAPI().create(svd);
    }

    public void assignDate(Long serviceValueCod, String assignedDate, String endDate, String statusCod) {
        ServiceValue sv = getFacadeContainer().getServiceValueAPI().find(
                serviceValueCod);
        sv.setColumn6Chr(assignedDate);
        sv.setColumn7Chr(statusCod);
        sv.setColumn8Chr(endDate);
        getFacadeContainer().getServiceValueAPI().edit(sv);
    }

    public void changeUserphone(Long serviceValueCod, Long userphoneCod, String statusCod, String observation) {
        ServiceValue sv = getFacadeContainer().getServiceValueAPI().find(
                serviceValueCod);
        Userphone u = getFacadeContainer().getUserphoneAPI().find(userphoneCod);
        sv.setUserphone(u);
        // sv.setColumn10Chr(getNextOrderNumber(userphoneCod).toString());
        getFacadeContainer().getServiceValueAPI().edit(sv);

        /* se crea svd para cuando se reasigna una OT */
        ServiceValueDetail svd = new ServiceValueDetail();
        svd.setServiceValue(sv);
        svd.setColumn1Chr(statusCod);
        svd.setColumn2Chr(observation);
        getFacadeContainer().getServiceValueDetailAPI().create(svd);
    }

    public void changeStatus(Long serviceValueCod, String statusCod, String observation) {
        ServiceValue sv = getFacadeContainer().getServiceValueAPI().find(
                serviceValueCod);
        sv.setColumn7Chr(statusCod);
        getFacadeContainer().getServiceValueAPI().edit(sv);

        /* se crea svd para cuando se reasigna una OT */
        ServiceValueDetail svd = new ServiceValueDetail();
        svd.setServiceValue(sv);
        svd.setColumn1Chr(statusCod);
        svd.setColumn2Chr(observation);
        getFacadeContainer().getServiceValueDetailAPI().create(svd);
    }

    public List<Ot> getOtListByUserphoneInZone(String zoneCod, List<String> finalizedStatusCodList, Long clientCod) {
        List<Userphone> userphoneList = getUserphoneByZone(zoneCod, clientCod);
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            if (userphoneList != null && userphoneList.size() > 0) {
                String userphoneCodeList = "";
                for (Userphone userphone : userphoneList) {
                    userphoneCodeList += userphone.getUserphoneCod() + ",";
                }
                String statusStr = "";
                for (String str : finalizedStatusCodList) {
                    statusStr += "'" + str + "',";
                }
                statusStr = statusStr.substring(0, statusStr.length() - 1);

                userphoneCodeList = userphoneCodeList.substring(0,
                        userphoneCodeList.length() - 1);
                Query query = em.createQuery(" SELECT o FROM Ot o "
                    + " WHERE o.codClient = :clientcod "
                    + " AND o.statusCod NOT IN (" + statusStr + ") "
                    + " AND o.zoneCod = :zoneCod "
                    + " AND o.assignedDate is not NULL "
                    + " AND o.codUserphone in ( " + userphoneCodeList + ")");
                query.setParameter("clientcod", clientCod);
                // query.setParameter("statusCod", finalizedStatusCod);
                query.setParameter("zoneCod", zoneCod);
                List<Ot> otList = query.getResultList();
                return otList;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
        return new ArrayList<Ot>();
    }

    public List<Ot> getOtListByZoneClassificationStatus(String zoneCod, List<String> statusCodList, Userweb userweb) {
        List<Userphone> userphoneList = getUserphoneByZoneClassifAndService(
                zoneCod, userweb);
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            if (userphoneList != null && userphoneList.size() > 0) {
                String userphoneCodeList = "";
                for (Userphone userphone : userphoneList) {
                    userphoneCodeList += userphone.getUserphoneCod() + ",";
                }
                String statusStr = "";
                for (String str : statusCodList) {
                    statusStr += "'" + str + "',";
                }
                statusStr = statusStr.substring(0, statusStr.length() - 1);

                userphoneCodeList = userphoneCodeList.substring(0,
                        userphoneCodeList.length() - 1);
                Query query = em.createQuery(" SELECT o FROM Ot o "
                    + " WHERE o.statusCod IN (" + statusStr + ") "
                    + " AND o.zoneCod = :zoneCod "
                    + " AND o.codUserphone in ( " + userphoneCodeList + ")");
                query.setParameter("zoneCod", zoneCod);
                List<Ot> otList = query.getResultList();
                return otList;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
        return new ArrayList<Ot>();
    }

    public List<Userphone> getUserphoneByZone(String zoneCod, Long clientCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT u FROM Userphone u, IN (u.metaData) md "
                + "WHERE md.metaDataPK.codClient = :codClient "
                + "AND md.metaDataPK.codMeta = :codMeta "
                + "AND md.metaDataPK.codMember = :codMember "
                + "AND md.metaDataPK.codeChr = :codeChr");
            query.setParameter("codClient", clientCod);
            query.setParameter("codMeta", 13L);
            query.setParameter("codMember", 1L);
            query.setParameter("codeChr", zoneCod);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<Userphone> getUserphoneByZoneClassifAndService(String zoneCod, Userweb userweb) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery(""
                + " SELECT DISTINCT u FROM Userphone u, "
                + " IN (u.classificationList ) cl , "
                + " IN (u.clientServiceFunctionalityList) f , "
                + " IN (u.metaData) md " + " WHERE cl in (:classifications) "
                + " AND u.enabledChr = true "
                + " AND f.serviceFunctionality.service.serviceCod = 16 "
                + " AND md.metaDataPK.codMeta = 13 "
                + " AND md.metaDataPK.codMember = 1 "
                + " AND md.metaDataPK.codeChr = :zoneCod "
                + " ORDER BY u.nameChr ");
            List<Classification> classifications = getFacadeContainer().getClassificationAPI().findByUserweb(
                    userweb);
            if (classifications == null) {
                return new ArrayList<Userphone>();
            }
            query.setParameter("classifications", classifications);
            query.setParameter("zoneCod", zoneCod);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<Ot> getOtListByUserphoneZoneStatus(Long userphoneCod, String zoneCod, List<String> statusCodList) {
        String statusStr = "";
        for (String str : statusCodList) {
            statusStr += "'" + str + "',";
        }
        statusStr = statusStr.substring(0, statusStr.length() - 1);
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery(" SELECT o FROM Ot o "
                + " WHERE o.statusCod IN (" + statusStr + ") "
                + " AND o.zoneCod = :zoneCod "
                + " AND o.codUserphone = :userphoneCod");
            query.setParameter("zoneCod", zoneCod);
            query.setParameter("userphoneCod", userphoneCod);
            List<Ot> otList = query.getResultList();

            return otList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<Ot> getOtListByUserphoneStatusDate(Long userphoneCod, String statusCod, Long clientCod, Date from, Date to) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery(" SELECT o FROM Ot o "
                + " WHERE o.codClient = :clientcod "
                + " AND o.statusCod = :statusCod "
                + " AND o.assignedDate between :from and :to "
                + " AND o.assignedDate is not NULL "
                + " AND o.codUserphone = :userphoneCod");
            query.setParameter("clientcod", clientCod);
            query.setParameter("statusCod", statusCod);
            query.setParameter("userphoneCod", userphoneCod);
            query.setParameter("from", from);
            query.setParameter("to", to);
            List<Ot> otList = query.getResultList();
            return otList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    /**
     * M�todo que trae todas las OTs asignadas a un userphone que no esten en
     * estado no terminado, postergado o cancelado cuya fecha de asignacion sea
     * igual a date
     * 
     * */
    public List<Ot> getOtListByUserphoneStatusDate(Long userphoneCod, List<String> statusCodList, Date from, Date to) {
        EntityManager em = null;
        try {
            String statusStr = "";
            for (String str : statusCodList) {
                statusStr += "'" + str + "',";
            }
            statusStr = statusStr.substring(0, statusStr.length() - 1);

            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery(" SELECT o FROM Ot o "
                + " WHERE o.statusCod IN (" + statusStr + ") "
                + " AND o.assignedDate between :from and :to "
                + " AND o.codUserphone = :userphoneCod");
            query.setParameter("userphoneCod", userphoneCod);
            query.setParameter("from", from);
            query.setParameter("to", to);
            List<Ot> otList = query.getResultList();
            return otList;
        } catch (NoResultException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<Ot> getOtListByStatusDate(String statusCod, Long clientCod, Date from, Date to) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery(" SELECT o FROM Ot o "
                + " WHERE o.codClient = :clientcod "
                + " AND o.statusCod = :statusCod "
                + " AND o.assignedDate between :from and :to "
                + " AND o.assignedDate is not NULL ");
            query.setParameter("clientcod", clientCod);
            query.setParameter("statusCod", statusCod);
            query.setParameter("from", from);
            query.setParameter("to", to);
            List<Ot> otList = query.getResultList();
            return otList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public String saveEditing(Ot entity) throws Exception {
        Object obj = entity.getPrimaryKey();
        if (obj == null) {
            createEntity(entity);
        } else {
            editEntity(entity);
        }
        return null;
    }

    private void createEntity(Ot entity) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            if (getFacadeContainer().isEntityManagerTransactional()) {
                em.getTransaction().begin();
            }
            ServiceValue sv = new ServiceValue();
            // Long nextVal = getServiceValueNextval();
            // sv.setServicevalueCod(nextVal);
            sv.setService(getFacadeContainer().getServiceAPI().find(16L));
            sv.setUserphone(getFacadeContainer().getUserphoneAPI().find(0L));

            sv.setColumn1Chr(entity.getOtCode());
            sv.setColumn2Chr(entity.getActivityCod());
            sv.setColumn3Chr(entity.getClientCod());
            sv.setColumn4Chr(entity.getZoneCod());
            sv.setColumn5Chr(getSdFormat().format(entity.getCreatedDate()));

            sv.setColumn6Chr(entity.getAssignedDate() != null ? getSdFormat().format(
                    entity.getAssignedDate()) : null);
            sv.setColumn7Chr(entity.getStatusCod());
            sv.setColumn8Chr(entity.getEndDate() != null ? getSdFormat().format(
                    entity.getEndDate()) : null);
            sv.setColumn9Chr(entity.getCodClient().toString());
            sv.setColumn10Chr(entity.getClientDescription());
            getFacadeContainer().getServiceValueAPI().create(sv);

            if (entity.getOtCode() == null || entity.getOtCode().isEmpty()) {
                sv.setColumn1Chr(sv.getServicevalueCod().toString());
            }

            if (getFacadeContainer().isEntityManagerTransactional()) {
                em.getTransaction().commit();
            }
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(getClass(), null,
                    e.getMessage(), e);
            if (getFacadeContainer().isEntityManagerTransactional()) {
                em.getTransaction().rollback();
            }
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    private void editEntity(Ot entity) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            if (getFacadeContainer().isEntityManagerTransactional()) {
                em.getTransaction().begin();
            }
            ServiceValue sv = getFacadeContainer().getServiceValueAPI().find(
                    entity.getServicevalueCod());
            sv.setService(getFacadeContainer().getServiceAPI().find(16L));
            sv.setUserphone(getFacadeContainer().getUserphoneAPI().find(
                    entity.getCodUserphone()));
            sv.setColumn1Chr(entity.getOtCode());
            sv.setColumn2Chr(entity.getActivityCod());
            sv.setColumn3Chr(entity.getClientCod());
            sv.setColumn4Chr(entity.getZoneCod());
            sv.setColumn5Chr(getSdFormat().format(entity.getCreatedDate()));

            sv.setColumn6Chr(getSdFormat().format(entity.getAssignedDate()));
            sv.setColumn7Chr(entity.getStatusCod());
            sv.setColumn8Chr(getSdFormat().format(entity.getEndDate()));
            sv.setColumn9Chr(entity.getCodClient().toString());
            sv.setColumn10Chr(entity.getClientDescription());
            getFacadeContainer().getServiceValueAPI().edit(sv);

            if (getFacadeContainer().isEntityManagerTransactional()) {
                em.getTransaction().commit();
            }

        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(getClass(), null,
                    e.getMessage(), e);
            if (getFacadeContainer().isEntityManagerTransactional()) {
                em.getTransaction().rollback();
            }
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public SimpleDateFormat getSdFormat() {
        if (sdFormat == null) {
            String otScheduleDateFormat = null;
            try {
                otScheduleDateFormat = getFacadeContainer().getGlobalParameterAPI().findByCode(
                        "ot.schedule.dateformat");
            } catch (GenericFacadeException ex) {
            }
            if (otScheduleDateFormat == null
                || otScheduleDateFormat.length() == 0) {
                otScheduleDateFormat = "dd/MM/yyyy HH:mm";
            }
            sdFormat = new SimpleDateFormat(otScheduleDateFormat);
        }
        return sdFormat;
    }

    public void setSdFormat(SimpleDateFormat sdFormat) {
        this.sdFormat = sdFormat;
    }

    public Ot getOt(String otCode) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery(" SELECT o " + " FROM Ot o "
                + " WHERE o.otCode= :otCode " + " ORDER BY o.orderNumber ASC ");
            query.setParameter("otCode", otCode);
            query.setMaxResults(1);
            return (Ot) query.getSingleResult();
        } catch (NonUniqueResultException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Ot> getOtList(String otCode, Long clientCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery(" SELECT o " + " FROM Ot o "
                + " WHERE o.otCode= :otCode " + " AND o.codClient= :clientCod ");
            query.setParameter("otCode", otCode);
            query.setParameter("clientCod", clientCod);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Ot> findRange(String sql, int[] range, String whereCriteria, String orderbyCriteria, List<Classification> classifications) {

        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            String JPQL = sql;

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
                if (sql.contains("classifications")) {
                    q.setParameter("classifications", classifications);
                }
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

            return q.getResultList();
        } catch (Exception e) {
            getFacadeContainer().getNotifier().log(getClass(), null,
                    Action.ERROR, e.getMessage(), e);
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public Integer countJPQL(String JPQL, List<Classification> classifications) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery(JPQL);
            if (classifications != null) {
                if (JPQL.contains("classifications")) {
                    q.setParameter("classifications", classifications);
                }
            }
            getFacadeContainer().getNotifier().signal(getClass(), Action.INFO,
                    JPQL);
            return ((Long) q.getSingleResult()).intValue();
        } catch (Exception e) {
            return 0;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public void deleteEntity(Ot entity) {
        try {
            ServiceValue sv = getFacadeContainer().getServiceValueAPI().find(
                    entity.getServicevalueCod());
            getFacadeContainer().getServiceValueAPI().remove(sv);
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(getClass(), null,
                    e.getMessage(), e);
        }
    }
}
