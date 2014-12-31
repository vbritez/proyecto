package com.tigo.cs.api.facade;

import java.util.Date;
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

public abstract class ServiceValueAPI extends AbstractAPI<ServiceValue> {

    public ServiceValueAPI() {
        super(ServiceValue.class);
    }

    @Override
    public ServiceValue edit(ServiceValue entity) {

        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            if (getFacadeContainer().isEntityManagerTransactional()) {
                em.getTransaction().begin();
            }
            ServiceValue sv = em.find(ServiceValue.class,
                    entity.getServicevalueCod());

            sv.setColumn1Chr(entity.getColumn1Chr());
            sv.setColumn2Chr(entity.getColumn2Chr());
            sv.setColumn3Chr(entity.getColumn3Chr());
            sv.setColumn4Chr(entity.getColumn4Chr());
            sv.setColumn5Chr(entity.getColumn5Chr());
            sv.setColumn6Chr(entity.getColumn6Chr());
            sv.setColumn7Chr(entity.getColumn7Chr());
            sv.setColumn8Chr(entity.getColumn8Chr());
            sv.setColumn9Chr(entity.getColumn9Chr());
            sv.setColumn10Chr(entity.getColumn10Chr());

            sv.setEnabledChr(entity.getEnabledChr());
            sv.setMessage(entity.getMessage());
            sv.setRecorddateDat(entity.getRecorddateDat());
            sv.setService(entity.getService());
            sv.setUserphone(entity.getUserphone());
            sv.setServiceValueDetailCollection(entity.getServiceValueDetailCollection());

            sv = em.merge(sv);
            if (getFacadeContainer().isEntityManagerTransactional()) {
                em.getTransaction().commit();
            }
            return sv;
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
        return null;
    }

    public String getVisitOrderTotal(Userphone userphone, Service service, ServiceValue registroInicio) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery(" SELECT SUM(s.column6Chr) FROM ServiceValue s "
                + " WHERE    s.userphone = :userphone "
                + " AND      s.service = :service "
                + " AND      s.column4Chr = :registroInicio "
                + " AND      s.enabledChr = true  ");
            q.setParameter("userphone", userphone);
            q.setParameter("service", service);
            q.setParameter("registroInicio",
                    registroInicio.getServicevalueCod().toString());
            return (String) q.getSingleResult();
        } catch (Exception e) {
            return "0";
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public List<String> getGuardsWithMarkings(String shiftPeriodCod, String shiftConfigurationCod, java.util.Date from, java.util.Date to) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            String select = " SELECT DISTINCT svd.serviceValue.column1Chr "
                + " FROM ServiceValueDetail svd "
                + " WHERE svd.serviceValue.service.serviceCod = 15 "
                + " AND svd.enabledChr = true "
                + " AND svd.serviceValue.column2Chr = :shiftPeriodCod "
                + " AND svd.serviceValue.column3Chr = :shiftConfigurationCod "
                + " AND svd.recorddateDat BETWEEN :from " + "AND :to";
            Query q = em.createQuery(select);
            q.setParameter("shiftPeriodCod", shiftPeriodCod);
            q.setParameter("shiftConfigurationCod", shiftConfigurationCod);
            q.setParameter("from", from);
            q.setParameter("to", to);
            return q.getResultList();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public ServiceValue getCurrentAttendanceServiceValue(String employee, Service service, Client client) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery(" SELECT s FROM ServiceValue s "
                + " WHERE s.recorddateDat = :today "
                + " AND   s.column1Chr = :employee "
                + " AND   s.service = :service "
                + " AND   s.userphone.client = :client "
                + " AND   s.enabledChr = true "
                + " ORDER BY s.servicevalueCod DESC");
            q.setParameter("today", new Date(), TemporalType.DATE);
            q.setParameter("employee", employee);
            q.setParameter("service", service);
            q.setParameter("client", client);
            q.setMaxResults(1);
            ServiceValue result = (ServiceValue) q.getSingleResult();
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

    public ServiceValue getLastAttendanceServiceValue(String employee, Service service, Client client) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = null;
            ServiceValue result = null;
            q = em.createQuery(" SELECT s FROM ServiceValue s "
                + " WHERE s.column1Chr = :employee "
                + " AND   s.service = :service "
                + " AND   s.userphone.client = :client "
                + " AND   s.enabledChr = true "
                + " ORDER BY s.servicevalueCod DESC");
            q.setParameter("employee", employee);
            q.setParameter("service", service);
            q.setParameter("client", client);
            q.setMaxResults(1);
            result = (ServiceValue) q.getSingleResult();
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

    public ServiceValue getCurrentGuardServiceValue(Service guardService, Userphone userphone) throws FacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery(" SELECT s FROM ServiceValue s "
                + " WHERE  s.service = :service "
                + " AND    s.enabledChr = true "
                + " AND    s.userphone = :userphone "
                + " AND    s.column2Chr IS NOT NULL "
                + " AND    s.column5Chr IS NULL ");
            q.setParameter("service", guardService);
            q.setParameter("userphone", userphone);
            ServiceValue result = (ServiceValue) q.getSingleResult();
            return result;
        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException e) {
            throw new FacadeException(this.getClass(), e);
        } catch (Exception e) {
            throw new FacadeException(this.getClass(), e);
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public ServiceValue getCurrentServiceValue(Userphone userphone, Service service) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = null;
            ServiceValue result = null;
            if (service.getServiceCod().toString().equals("3")) {
                q = em.createQuery(""
                    + "SELECT s FROM ServiceValue s LEFT JOIN FETCH s.serviceValueDetailCollection "
                    + " WHERE s.recorddateDat = :today "
                    + " AND   s.userphone = :userphone "
                    + " AND   s.service = :service "
                    + " AND   s.enabledChr = true "
                    + " AND   s.column1Chr = 'APERTURA' ");
                q.setParameter("today", new Date(), TemporalType.DATE);
                q.setParameter("userphone", userphone);
                q.setParameter("service", service);
                result = (ServiceValue) q.getSingleResult();
            } else if (service.getServiceCod().toString().equals("2")) {
                q = em.createQuery(""
                    + "SELECT s FROM ServiceValue s LEFT JOIN FETCH s.serviceValueDetailCollection "
                    + " WHERE  s.userphone = :userphone "
                    + " AND   s.service = :service "
                    + " AND   s.enabledChr = true "
                    + " AND   s.column9Chr <> '1' "
                    + " ORDER BY s.servicevalueCod desc");
                q.setParameter("userphone", userphone);
                q.setParameter("service", service);
                q.setMaxResults(1);
                result = (ServiceValue) q.getSingleResult();
            } else {
                q = em.createQuery(""
                    + "SELECT s FROM ServiceValue s LEFT JOIN FETCH s.serviceValueDetailCollection "
                    + " WHERE s.recorddateDat = :today "
                    + " AND   s.userphone = :userphone "
                    + " AND   s.service = :service "
                    + " AND   s.enabledChr = true ");
                q.setParameter("today", new Date(), TemporalType.DATE);
                q.setParameter("userphone", userphone);
                q.setParameter("service", service);
                result = (ServiceValue) q.getSingleResult();
            }
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

    public ServiceValue getCurrentFleetServiceValue(Service fleetService, Userphone userphone) throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            /*
             * creamos la consulta que nos retorne la ultima cabecera que tenga
             * como valor un "KM.INICIAL" y no posea "KM.FINAL"
             */
            Query q = em.createQuery(" SELECT s FROM ServiceValue s "
                + " WHERE  s.service = :service "
                + " AND    s.userphone = :userphone "
                + " AND    s.column3Chr IS NOT NULL "
                + " AND    s.column4Chr IS NULL "
                + " AND    s.enabledChr = true ");
            q.setParameter("service", fleetService);
            q.setParameter("userphone", userphone);
            ServiceValue result = (ServiceValue) q.getSingleResult();
            return result;
        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException e) {
            throw new GenericFacadeException(this.getClass(), e);
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public ServiceValue getDriverCurrentlyWithVehicle(Service fleetService, String driver, Client client) throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            /*
             * obtenemos el ultimo registro que nos indica que el chofer tiene
             * aun un vehiculo en estado "NO DEVUELTO"
             */
            Query q = em.createQuery(" SELECT s FROM ServiceValue s "
                + " WHERE  s.service = :service "
                + " AND  s.column1Chr = :driver "
                + " AND  s.column3Chr IS NOT NULL "
                + " AND  s.column4Chr IS NULL "
                + " AND  s.userphone.client = :client "
                + " AND  s.enabledChr = true "
                + " AND  s.userphone.enabledChr = '1'");
            q.setParameter("service", fleetService);
            q.setParameter("driver", driver);
            q.setParameter("client", client);
            ServiceValue result = (ServiceValue) q.getSingleResult();
            return result;
        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException e) {
            throw new GenericFacadeException(this.getClass(), e);
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public ServiceValue getVehicleCurrentlyRetired(Service fleetService, String vehicle, Client client) throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            /*
             * obtenemos el ultimo registro que nos indica que el vehiculo un se
             * encuentra en estado "NO DEVUELTO"
             */
            Query q = em.createQuery(" SELECT s FROM ServiceValue s "
                + " WHERE  s.service = :service "
                + " AND  s.column2Chr = :vehicle "
                + " AND  s.column3Chr IS NOT NULL "
                + " AND  s.column4Chr IS NULL "
                + " AND  s.userphone.client = :client "
                + " AND  s.enabledChr = true "
                + " AND  s.userphone.enabledChr = '1'");
            q.setParameter("service", fleetService);
            q.setParameter("vehicle", vehicle);
            q.setParameter("client", client);
            ServiceValue result = (ServiceValue) q.getSingleResult();
            return result;
        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException e) {
            throw new GenericFacadeException(this.getClass(), e);
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public ServiceValue getLastServiceValue(Userphone userphone, Service service) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery(" SELECT s FROM ServiceValue s "
                + " WHERE  s.service= :service "
                + " AND s.userphone = :userphone "
                + " AND s.enabledChr = true "
                + " ORDER BY s.servicevalueCod desc ");
            q.setParameter("service", service);
            q.setParameter("userphone", userphone);
            q.setMaxResults(1);
            ServiceValue result = (ServiceValue) q.getSingleResult();
            return result;
        } catch (NoResultException e) {
            return null;
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

    public ServiceValue getOpenCollection(Userphone userphone, Service service) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            /*
             * obtenemos el ultimo registro que nos indica que el vehiculo un se
             * encuentra en estado "ABIERTO"
             */
            Query q = em.createQuery(" SELECT s FROM ServiceValue s "
                + " WHERE  s.service= :service "
                + " AND  s.userphone = :userphone "
                + " AND  s.enabledChr = true " + " AND  s.column3Chr = '1' ");
            q.setParameter("service", service);
            q.setParameter("userphone", userphone);
            q.setMaxResults(1);
            ServiceValue result = (ServiceValue) q.getSingleResult();
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

    public ServiceValue closeOpenCollection(ServiceValue serviceValue) {
        ServiceValue sv = find(serviceValue.getServicevalueCod());
        sv.setColumn3Chr("0");
        return edit(sv);
    }

    public void markOrder(ServiceValue serviceValue, boolean marcar) {
        ServiceValue managedServiceValue = this.find(serviceValue.getServicevalueCod());
        if (marcar) {
            managedServiceValue.setColumn5Chr("1");
        } else {
            managedServiceValue.setColumn5Chr("0");
        }
        super.edit(managedServiceValue);

    }

    public Long getMaxServicevalue(Userphone userphone) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createNamedQuery("ServiceValue.findMaxServicevalue");
            query.setParameter("userphone", userphone);
            query.setParameter("service", 4L);
            return (Long) query.getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public Long getMaxServicevalueToday(Userphone userphone) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createNamedQuery("ServiceValue.findMaxServicevalueToday");
            query.setParameter("userphone", userphone);
            query.setParameter("service", 4L);
            query.setParameter("recorddateDat", new Date(), TemporalType.DATE);
            return (Long) query.getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public Long getMaxNextServicevalue(Userphone userphone, Long nextValue) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createNamedQuery("ServiceValue.findMaxNextServicevalueToday");
            query.setParameter("userphone", userphone);
            query.setParameter("service", 4L);
            query.setParameter("servicevalueCod", nextValue);
            query.setParameter("recorddateDat", new Date(), TemporalType.DATE);
            return (Long) query.getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public ServiceValue getServicevalue(Long servicevalueCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createNamedQuery("ServiceValue.findByServicevalueCod");
            query.setParameter("servicevalueCod", servicevalueCod);
            return (ServiceValue) query.getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<ServiceValue> getAllScheduleServiceValueList(String fromDateStr, String toDateStr, String dateFormat) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            String sql = " SELECT sv.* " + " FROM SERVICE_VALUE sv "
                + " INNER JOIN SERVICE s on (sv.COD_SERVICE = s.SERVICE_COD) "
                + " WHERE s.SERVICE_COD = 14 "
                + " and TO_TIMESTAMP(sv.COLUMN2_CHR, '%s') "
                + " between TO_TIMESTAMP('%s', '%s') "
                + " AND TO_TIMESTAMP('%s', '%s') "
                + " AND sv.ENABLED_CHR = '1' ";

            sql = String.format(sql, dateFormat, fromDateStr, dateFormat,
                    toDateStr, dateFormat);
            Query q = em.createNativeQuery(sql, ServiceValue.class);
            return q.getResultList();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public ServiceValue getMarkOnShift(Long clientCod, String codGuard, Long shiftPeriodCod, Long shiftConfigurationCod, Date startTime, Date endTime) {

        Client client = getFacadeContainer().getClientAPI().find(clientCod);
        ShiftConfigurationPK pk = new ShiftConfigurationPK();
        pk.setCodShiftPeriod(shiftPeriodCod);
        pk.setShiftConfigurationCod(shiftConfigurationCod);
        ShiftConfiguration shiftConfiguration = getFacadeContainer().getShiftConfigurationAPI().find(
                pk);
        return getMarkOnShift(client, codGuard, shiftConfiguration, startTime,
                endTime);
    }

    public ServiceValue getMarkOnShift(Client client, String codGuard, ShiftConfiguration shiftConfiguration, Date startDate, Date endDate) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            String jpql = " SELECT sv from ServiceValue sv "
                + " WHERE sv.service = 15 " + " AND sv.column1Chr = :codGuard "
                + " AND sv.column2Chr = :codPeriod "
                + " AND sv.column3Chr = :codShiftConfiguration "
                /*
                 * se recupera el cliente de esta manera, porque el userphone no
                 * necesariamente puede pertenecer a ese cliente, ya que si el
                 * supervisor no esta provisionado en la plataforma, se utiliza
                 * un userphone asignado a un cliente generico 0
                 */
                + " AND exists ( "
                + "    SELECT m.metaDataPK.codeChr from MetaData m "
                + "    WHERE m.metaDataPK.codClient = :client "
                + "    AND m.metaDataPK.codMeta = 4 "
                + "    AND m.metaDataPK.codMember = 1 "
                + "    AND m.metaDataPK.codeChr = sv.column1Chr ) "
                + " AND sv.message.dateinDat between :startDate and :endDate "
                + " AND sv.enabledChr = true ";

            Query q = em.createQuery(jpql);

            q.setParameter("codGuard", codGuard);
            q.setParameter(
                    "codPeriod",
                    shiftConfiguration.getShiftConfigurationPK().getCodShiftPeriod().toString());
            q.setParameter(
                    "codShiftConfiguration",
                    shiftConfiguration.getShiftConfigurationPK().getShiftConfigurationCod().toString());
            q.setParameter("client", client.getClientCod());
            q.setParameter("startDate", startDate);
            q.setParameter("endDate", endDate);
            /*
             * si retorna un valor es porque el guardia la realizo la marcacion
             * en su turno y en hora
             */
            return (ServiceValue) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }

    }

    public List<ServiceValue> findByMessage(Message message) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery(" SELECT s FROM ServiceValue s "
                + " WHERE s.message = :message " + " AND  s.enabledChr = true ");
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

    public ServiceValue findByQuery(String query) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery("SELECT o FROM ServiceValue o " + query);
            return (ServiceValue) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }

    }

    /*----------------------------------------------------------------
     * SECCION DE RECUPERACION DE DATOS PARA EL SERVICIO VISITADORES
     ----------------------------------------------------------------*/

    public ServiceValue findCurrentMedicalVisitorServiceValue(Userphone userphone, Service service) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery(""
                + "SELECT s FROM ServiceValue s "
                + " WHERE s.userphone = :userphone "
                + " AND   s.service = :service "
                // MEDICAL VISITOR
                + " AND s.column1Chr = 'MV' "
                + " AND s.recorddateDat = :today "
                + " AND s.enabledChr = true " + " ORDER BY recorddateDat DESC ");
            q.setParameter("today", new Date(), TemporalType.DATE);
            q.setParameter("userphone", userphone);
            q.setParameter("service", service);
            return (ServiceValue) q.getSingleResult();
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

    /**
     * 
     * @param userphone
     * @param service
     * @return
     */
    public ServiceValue findCurrentClinic(Userphone userphone, Service service) {
        return findCurrentHeader(findCurrentMedicalVisitorServiceValue(
                userphone, service));
    }

    /**
     * 
     * @param userphone
     * @param service
     * @return
     */
    public ServiceValue findCurrentMedic(Userphone userphone, Service service) {
        return findCurrentHeader(findCurrentClinic(userphone, service));
    }

    /**
     * 
     * @param userphone
     * @param service
     * @return
     */
    public ServiceValue findCurrentProduct(Userphone userphone, Service service) {
        return findCurrentHeader(findCurrentMedic(userphone, service));
    }

    /**
     * 
     * @param userphone
     * @param service
     * @return
     */
    public ServiceValue findCurrentHeader(ServiceValue currentServiceValue) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            String jpql = " SELECT sv from ServiceValue sv "
                + " WHERE sv.column1Chr = :currentServiceValueCod "
                + " AND  sv.enabledChr = true "
                + " ORDER by sv.servicevalueCod DESC";

            Query q = em.createQuery(jpql);

            q.setParameter("currentServiceValueCod",
                    currentServiceValue.getServicevalueCod().toString());
            q.setMaxResults(1);

            return (ServiceValue) q.getSingleResult();
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
    public ServiceValue create(ServiceValue entity) {
        entity.setEnabledChr(true);
        return super.create(entity);
    }

    public List<ServiceValue> findSD(ServiceValue serviceValue, ServiceValueDetail svd) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery(" SELECT s FROM ServiceValue s "
                + " WHERE s.column5Chr = :serviceValueDetailCod "
                + " AND   s.enabledChr = true ");

            // q.setParameter("serviceValueCod",
            // "'".concat(serviceValue.getServicevalueCod().toString()).concat("'"));
            q.setParameter("serviceValueDetailCod",
                    svd.getServicevaluedetailCod().toString());
            List<ServiceValue> result = q.getResultList();
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

    public List<ServiceValue> findProductHeaderForVisitMedic(String medicCode) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery(" SELECT s FROM ServiceValue s "
                + " WHERE  s.column1Chr = :medicCod "
                + " AND    s.column3Chr = 'PR' "
                + " AND    s.enabledChr = true ");

            q.setParameter("medicCod", medicCode);
            List<ServiceValue> result = q.getResultList();
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

    public Integer getCountMedicsForClinic(String clinicCode) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            String jpql = " SELECT count(*) " + " FROM ServiceValue sv "
                + " WHERE sv.column1Chr = :clinicCode "
                + " AND   sv.column3Chr = 'ME' "
                + " AND   sv.enabledChr = true";

            Query q = em.createQuery(jpql);
            q.setParameter("clinicCode", clinicCode);
            return ((Long) q.getSingleResult()).intValue();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public ServiceValue getCurrentTerportServiceValue(Service terportService, Userphone userphone, String manager, String operator, String machine) throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            /*
             * creamos la consulta que nos retorne la ultima cabecera que tenga
             * como valor un "KM.INICIAL" y no posea "KM.FINAL"
             */
            Query q = em.createQuery(" SELECT s FROM ServiceValue s "
                + " WHERE s.recorddateDat = :today "
                + " AND    s.service = :service "
                + " AND    s.userphone = :userphone "
                + " AND    s.column1Chr = :manager "
                + " AND    s.column2Chr = :operator "
                + " AND    s.column3Chr = :machine ");
            q.setParameter("today", new Date(), TemporalType.DATE);
            q.setParameter("service", terportService);
            q.setParameter("userphone", userphone);
            q.setParameter("manager", manager);
            q.setParameter("operator", operator);
            q.setParameter("machine", machine);
            ServiceValue result = (ServiceValue) q.getSingleResult();
            return result;
        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException e) {
            throw new GenericFacadeException(this.getClass(), e);
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

}
