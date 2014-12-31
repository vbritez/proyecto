package com.tigo.cs.api.facade;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import com.tigo.cs.commons.ejb.FacadeException;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.Client;
import com.tigo.cs.domain.Message;
import com.tigo.cs.domain.Service;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;
import com.tigo.cs.domain.ShiftConfiguration;
import com.tigo.cs.domain.ShiftConfigurationPK;
import com.tigo.cs.domain.Userphone;

/**
 * 
 * @author Javier Kovacs
 * @version $Id: ServiceValueDetailFacade.java 280 2012-01-05 20:21:18Z
 *          javier.kovacs $
 */
public abstract class ServiceValueDetailAPI extends AbstractAPI<ServiceValueDetail> {

    public ServiceValueDetailAPI() {
        super(ServiceValueDetail.class);
    }

    public ServiceValueDetail[] getServiceValueDetailList(Userphone userphone, String condicion) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery(" SELECT s FROM ServiceValueDetail s "
                + " WHERE s.serviceValue.userphone = :userphone " + condicion
                + " AND   s.enabledChr = true "
                + " ORDER BY s.servicevaluedetailCod desc");
            q.setParameter("userphone", userphone);
            ServiceValueDetail[] result = (ServiceValueDetail[]) q.getResultList().toArray(
                    new ServiceValueDetail[0]);

            return result;
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public ServiceValueDetail[] getServiceValueDetailList(Long userphoneCod, Long serviceCod, String condicion) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery(" SELECT s FROM ServiceValueDetail s "
                + " WHERE s.serviceValue.userphone.userphoneCod = :userphoneCod "
                + " AND   s.serviceValue.service.serviceCod = :serviceCod "
                + " AND   s.enabledChr = true " + condicion
                + " ORDER BY s.servicevaluedetailCod desc");
            q.setParameter("userphoneCod", userphoneCod);
            q.setParameter("serviceCod", serviceCod);
            q.setMaxResults(1);
            ServiceValueDetail[] result = (ServiceValueDetail[]) q.getResultList().toArray(
                    new ServiceValueDetail[0]);
            return result;
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public ServiceValueDetail getLastAttendanceServiceValueDetail(String employee, Service service, Client client) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery(" SELECT   s FROM ServiceValueDetail s "
                + " WHERE     s.serviceValue.service      	= :service "
                + " AND     s.serviceValue.column1Chr   	= :employee "
                + " AND 	s.serviceValue.userphone.client = :client "
                + " AND     s.enabledChr = true "
                + " ORDER BY s.servicevaluedetailCod desc");
            q.setParameter("employee", employee);
            q.setParameter("service", service);
            q.setParameter("client", client);
            q.setMaxResults(1);
            return (ServiceValueDetail) q.getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public ServiceValueDetail getPresenceInitAttendanceEventServiceValueDetail(String employee, Service service, Client client) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery(" SELECT   s FROM ServiceValueDetail s "
                + " WHERE   s.serviceValue.service      	= :service "
                + " AND     s.serviceValue.column1Chr   	= :employee "
                + " AND 	s.serviceValue.userphone.client = :client "
                + " AND     s.column1Chr   					= 'P' "
                + " AND     s.column2Chr   					= 'I' "
                + " AND     s.enabledChr = true "
                + " ORDER BY s.servicevaluedetailCod desc");
            q.setParameter("employee", employee);
            q.setParameter("service", service);
            q.setParameter("client", client);
            q.setMaxResults(1);
            return (ServiceValueDetail) q.getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public int validateMarking(Long serviceValueCod, Date markingDate) throws FacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            GregorianCalendar dateHour = new GregorianCalendar();
            dateHour.setTime(markingDate);
            dateHour.set(Calendar.MINUTE, 0);
            dateHour.set(Calendar.SECOND, 0);
            Query q = em.createQuery(" SELECT s FROM ServiceValueDetail s "
                + " WHERE s.serviceValue.servicevalueCod = :serviceValueCod "
                + " AND   s.recorddateDat >= :dateHour "
                + " AND   s.enabledChr = true "
                + " ORDER BY s.servicevaluedetailCod desc");
            q.setParameter("serviceValueCod", serviceValueCod);
            q.setParameter("dateHour", dateHour.getTime(),
                    TemporalType.TIMESTAMP);
            ServiceValueDetail[] result = (ServiceValueDetail[]) q.getResultList().toArray(
                    new ServiceValueDetail[0]);
            if (result != null && result.length > 0) {
                ServiceValueDetail svd = result[0];
                if (svd.getColumn2Chr() == null) {
                    return 1; // Ya existe una marcación para la hora actual.
                              // GUARD_RE_MARKING_NOT_ALLOWEB
                } else {
                    return 2; // Marcacion fuera de tiempo.
                              // GUARD_UNTIMELY_MARKING
                }
            } else {
                return 0; // Ok, marcar para la hora actual.
            }
        } catch (Exception e) {
            throw new FacadeException(ServiceValueDetail.class, e);
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public ServiceValueDetail getServiceValueDetailList(Userphone userphone, Service service, String condicion) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery(" SELECT s FROM ServiceValueDetail s "
                + " WHERE s.serviceValue.userphone = :userphone "
                + " AND   s.serviceValue.service = :service "
                + " AND   s.enabledChr = true " + condicion
                + " ORDER BY s.servicevaluedetailCod desc");
            q.setParameter("userphone", userphone);
            q.setParameter("service", service);
            q.setMaxResults(1);
            return (ServiceValueDetail) q.getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public ServiceValueDetail getLastServiceValueDetail(Userphone userphone, Service service, String condicion) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery(" SELECT s FROM ServiceValueDetail s "
                + " WHERE s.serviceValue.userphone = :userphone "
                + " AND   s.serviceValue.service = :service "
                + " AND   s.enabledChr = true " + condicion
                + " ORDER BY s.servicevaluedetailCod desc");
            q.setParameter("userphone", userphone);
            q.setParameter("service", service);
            q.setMaxResults(1);
            return (ServiceValueDetail) q.getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public ServiceValueDetail getSubDetailIncome(Long serviceValueCod, java.util.Date datetimeSAL) throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            String query = " SELECT svd FROM ServiceValueDetail svd "
                + " WHERE svd.serviceValue.servicevalueCod = :svCod "
                + " AND svd.column1Chr = 'ENT' "
                + " AND svd.recorddateDat < :literalDateTime "
                + " AND svd.enabledChr = true "
                + " ORDER BY svd.recorddateDat DESC";
            Query q = em.createQuery(query);
            q.setMaxResults(1);
            q.setParameter("svCod", serviceValueCod);
            q.setParameter("literalDateTime", datetimeSAL);// "'".concat(datetimeSAL).concat("'"));
            return (ServiceValueDetail) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException e) {
            throw new GenericFacadeException(getClass(), e.getMessage(), e);
        } catch (Exception e) {
            throw new GenericFacadeException(getClass(), e.getMessage(), e);
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public ServiceValueDetail getLastGuardMarking(String guardCod, Long shiftPeriodCod, Long shiftConfigurationCod, java.util.Date startTime) throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery(" SELECT DISTINCT svd "
                + " FROM ServiceValueDetail svd "
                + " WHERE svd.serviceValue.service.serviceCod = 15 "
                + " AND svd.serviceValue.column1Chr = :guardCod"
                + " AND svd.serviceValue.column2Chr = :shiftPeriodCod "
                + " AND svd.serviceValue.column3Chr = :shiftConfigurationCod "
                + " AND svd.recorddateDat > :startTime "
                + " AND svd.enabledChr = true "
                + " ORDER BY svd.recorddateDat DESC");
            query.setParameter("guardCod", guardCod);
            query.setParameter("shiftPeriodCod", shiftPeriodCod.toString());
            query.setParameter("shiftConfigurationCod",
                    shiftConfigurationCod.toString());
            query.setParameter("startTime", startTime);
            query.setMaxResults(1);
            return (ServiceValueDetail) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public ServiceValueDetail getLastServiceValueDetail(Long serviceValueCod) throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery(" SELECT svd "
                + " FROM ServiceValueDetail svd "
                + " WHERE svd.serviceValue.servicevalueCod = :serviceValueCod "
                + " AND  svd.enabledChr = true "
                + " ORDER BY svd.recorddateDat DESC");
            query.setParameter("serviceValueCod", serviceValueCod);
            query.setMaxResults(1);
            return (ServiceValueDetail) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public Double getOrderSessionTotal(Userphone userphone, Service service, ServiceValue serviceValue) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery(" SELECT SUM(svd.column5Chr) FROM ServiceValueDetail svd "
                + " WHERE    svd.serviceValue.userphone = :userphone "
                + " AND      svd.serviceValue.service = :service "
                + " AND      svd.serviceValue = :serviceValue "
                + " AND      svd.enabledChr = true ");
            q.setParameter("userphone", userphone);
            q.setParameter("service", service);
            q.setParameter("serviceValue", serviceValue);
            return Double.parseDouble((String) q.getSingleResult());
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public boolean getMarkOnShift(Long clientCod, String codGuard, Long shiftPeriodCod, Long shiftConfigurationCod, Date startTime, Date endTime) {
        Client client = getFacadeContainer().getClientAPI().find(clientCod);
        ShiftConfigurationPK pk = new ShiftConfigurationPK();
        pk.setCodShiftPeriod(shiftPeriodCod);
        pk.setShiftConfigurationCod(shiftConfigurationCod);
        ShiftConfiguration shiftConfiguration = getFacadeContainer().getShiftConfigurationAPI().find(
                pk);
        return getMarkOnShift(client, codGuard, shiftConfiguration, startTime,
                endTime);
    }

    /*
     * verifica si el determinado guardia ya tiene una marcacion en la hora
     */
    public boolean getMarkOnShift(Client client, String codGuard, ShiftConfiguration shiftConfiguration, Date startTime, Date endTime) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            String jpql = " SELECT sv from ServiceValue sv, "
                + " IN (sv.serviceValueDetailCollection) svd "
                + " WHERE sv.service = 15 " + " AND sv.column1Chr = :codGuard "
                + " AND sv.column2Chr = :codPeriod "
                + " AND sv.column3Chr = :codShiftConfiguration "
                + " AND sv.enabledChr = true "
                + " AND svd.message.dateinDat BETWEEN :startTime and :endTime "
                /*
                 * se recupera el cliente de esta manera, porque el userphone no
                 * necesariamente puede pertenecer a ese cliente, ya que si el
                 * supervisor no esta provisionado en la plataforma, se utiliza
                 * un userphone asignado a un cliente generico 0
                 */
                + "AND exists ( "
                + "    SELECT m.metaDataPK.codeChr from MetaData m "
                + "    WHERE m.metaDataPK.codClient = :client "
                + "    AND m.metaDataPK.codMeta = 4 "
                + "    AND m.metaDataPK.codMember = 1 "
                + "    AND m.metaDataPK.codeChr = sv.column1Chr ) ";

            Query q = em.createQuery(jpql);

            q.setParameter("codGuard", codGuard);
            q.setParameter(
                    "codPeriod",
                    shiftConfiguration.getShiftConfigurationPK().getCodShiftPeriod().toString());
            q.setParameter(
                    "codShiftConfiguration",
                    shiftConfiguration.getShiftConfigurationPK().getShiftConfigurationCod().toString());
            q.setParameter("startTime", startTime);
            q.setParameter("endTime", endTime);
            q.setParameter("client", client.getClientCod());
            /*
             * si retorna un valor es porque el guardia la realizo la marcacion
             * en su turno y en hora
             */
            ServiceValue sv = (ServiceValue) q.getSingleResult();

            return true;
        } catch (NoResultException e) {
            return false;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<ServiceValueDetail> findByMessage(Message message) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery(" SELECT s FROM ServiceValueDetail s "
                + " WHERE s.message = :message "
                + " AND   s.enabledChr = true ");

            q.setParameter("message", message);

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

    public List<ServiceValueDetail> findByServiceValueColumn1(ServiceValue serviceValue) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery(" SELECT s FROM ServiceValueDetail s "
                + " WHERE s.serviceValue.column1Chr = :serviceValue "
                + " AND   s.enabledChr = true "
                + " ORDER BY s.servicevaluedetailCod desc");

            q.setParameter("serviceValue",
                    serviceValue.getServicevalueCod().toString());
            List<ServiceValueDetail> result = q.getResultList();
            if (result != null && result.size() > 0) {
                return result;
            }
            return null;
        } catch (NoResultException e) {
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

    public List<ServiceValueDetail> findByServiceValueDesc(ServiceValue serviceValue) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery(" SELECT s FROM ServiceValueDetail s "
                + " WHERE s.serviceValue = :serviceValue "
                + " AND   s.enabledChr = true "
                + " ORDER BY s.servicevaluedetailCod desc");

            q.setParameter("serviceValue", serviceValue);
            List<ServiceValueDetail> result = q.getResultList();
            if (result != null && result.size() > 0) {
                return result;
            }
            return null;
        } catch (NoResultException e) {
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

    public List<ServiceValueDetail> findByServiceValue(ServiceValue serviceValue) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery(" SELECT s FROM ServiceValueDetail s "
                + " WHERE s.serviceValue = :serviceValue "
                + " AND   s.enabledChr = true "
                + " ORDER BY s.servicevaluedetailCod");

            q.setParameter("serviceValue", serviceValue);
            List<ServiceValueDetail> result = q.getResultList();
            if (result != null && result.size() > 0) {
                return result;
            }
            return null;
        } catch (NoResultException e) {
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

    @Override
    public ServiceValueDetail create(ServiceValueDetail entity) {
        entity.setEnabledChr(true);
        return super.create(entity);
    }

    public ServiceValueDetail getServiceValueDetailNextId(Long userphoneCod, Long serviceCod, Long id) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery(" SELECT s FROM ServiceValueDetail s "
                + " WHERE s.serviceValue.userphone.userphoneCod = :userphoneCod "
                + " AND   s.serviceValue.service.serviceCod = :serviceCod "
                + " AND   s.enabledChr = true "
                + " AND   s.servicevaluedetailCod > :id "
                + " ORDER BY s.servicevaluedetailCod ASC");
            q.setParameter("userphoneCod", userphoneCod);
            q.setParameter("serviceCod", serviceCod);
            q.setParameter("id", id);
            q.setMaxResults(1);
            return (ServiceValueDetail) q.getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public ServiceValueDetail getServiceValueDetailPreviosId(Long userphoneCode, Long serviceCod, Long id) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery(" SELECT s FROM ServiceValueDetail s "
                + " WHERE s.serviceValue.userphoneCod = :userphoneCode "
                + " AND   s.serviceValue.service.serviceCod = :serviceCod "
                + " AND   s.enabledChr = true "
                + " AND   s.servicevaluedetailCod < :id "
                + " ORDER BY s.servicevaluedetailCod DESC");
            q.setParameter("userphoneCode", userphoneCode);
            q.setParameter("serviceCod", serviceCod);
            q.setParameter("id", id);
            q.setMaxResults(1);
            return (ServiceValueDetail) q.getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public Long getCountServiceValueEnabled(Long servicevalueCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            String jpql = " SELECT count(*) " + " FROM ServiceValueDetail svd "
                + " WHERE svd.serviceValue.servicevalueCod = :servicevalueCod "
                + " and  svd.enabledChr = true";

            Query q = em.createQuery(jpql);
            q.setParameter("servicevalueCod", servicevalueCod);
            return (Long) q.getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public ServiceValueDetail getSvdSal(String serviceValueCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            String jpql = " SELECT svd " + " FROM ServiceValueDetail svd "
                + " WHERE svd.column5Chr = :serviceValueCod "
                + " and  svd.enabledChr = true";

            Query q = em.createQuery(jpql);
            q.setParameter("servicevalueCod",
                    "'".concat(serviceValueCod).concat("'"));
            return (ServiceValueDetail) q.getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public ServiceValueDetail getServiceValueDetailSAL(Long userphoneCod, Long serviceCod, Long id) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery(" SELECT s FROM ServiceValueDetail s "
                + " WHERE s.serviceValue.userphone.userphoneCod = :userphoneCod "
                + " AND   s.serviceValue.service.serviceCod = :serviceCod "
                + " AND   s.enabledChr = true " + " AND   s.column5Chr = :id ");
            q.setParameter("userphoneCod", userphoneCod);
            q.setParameter("serviceCod", serviceCod);
            q.setParameter("id", id.toString());
            q.setMaxResults(1);
            return (ServiceValueDetail) q.getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<ServiceValueDetail> findByServiceValue(Long servicevalueCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery(" SELECT s FROM ServiceValueDetail s "
                + " WHERE s.serviceValue.servicevalueCod = :serviceValue "
                + " AND   s.enabledChr = true " + " AND s.message != null "
                + " ORDER BY s.servicevaluedetailCod");

            q.setParameter("serviceValue", servicevalueCod);
            List<ServiceValueDetail> result = q.getResultList();
            if (result != null && result.size() > 0) {
                return result;
            }
            return null;
        } catch (NoResultException e) {
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

    // public ServiceValueDetail create(ServiceValueDetail serviceValueDetail) {
    // EntityManager em = null;
    // try {
    // em = getFacadeContainer().getEntityManager();
    // Message message = serviceValueDetail.getMessage();
    //
    // if (message != null) {
    // message = em.getReference(message.getClass(),
    // message.getMessageCod());
    // serviceValueDetail.setMessage(message);
    // }
    // ServiceValue serviceValue = serviceValueDetail.getServiceValue();
    // if (serviceValue != null) {
    // serviceValue = em.getReference(serviceValue.getClass(),
    // serviceValue.getServicevalueCod());
    // serviceValueDetail.setServiceValue(serviceValue);
    // }
    // em.persist(serviceValueDetail);
    // if (message != null) {
    // message.getServiceValueDetailCollection().add(
    // serviceValueDetail);
    // message = em.merge(message);
    // }
    // if (serviceValue != null) {
    // serviceValue.getServiceValueDetailCollection().add(
    // serviceValueDetail);
    // serviceValue = em.merge(serviceValue);
    // }
    // return em.find(ServiceValueDetail.class,
    // serviceValueDetail.getServicevaluedetailCod());
    // } catch (Exception e) {
    // e.printStackTrace(System.err);
    // return null;
    // } finally {
    // if (em != null && getFacadeContainer().isEntityManagerClosable()) {
    // em.close();
    // }
    // }
    // }
}
