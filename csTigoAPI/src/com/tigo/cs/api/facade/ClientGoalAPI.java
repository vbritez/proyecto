package com.tigo.cs.api.facade;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.Classification;
import com.tigo.cs.domain.ClientGoal;
import com.tigo.cs.domain.Userphone;
import com.tigo.cs.domain.Userweb;

public abstract class ClientGoalAPI extends AbstractAPI<ClientGoal> {

    public ClientGoalAPI() {
        super(ClientGoal.class);
    }

    public List<ClientGoal> getAvailableGoals(Long clientCod, Long serviceCod, Date date1, Date date2, List<Userphone> userphoneList) throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery(" SELECT distinct(cg) FROM ClientGoal cg, " 
            	+ " IN (cg.userphones) u "	
            	+ " WHERE cg.service.serviceCod = :serviceCod AND "
                + " cg.client.clientCod = :clientCod AND "
                + " ((:validityDateFrom <= TRUNC(cg.validityDateTo) AND "
                + " TRUNC(cg.validityDateFrom) <= :validityDateTo) "
                + " OR ( TRUNC(cg.validityDateFrom) <= :validityDateTo "
                + " AND cg.validityDateTo is NULL)) "
                + " AND u in (:userphoneList) ");
            query.setParameter("clientCod", clientCod);
            query.setParameter("serviceCod", serviceCod);
            query.setParameter("validityDateFrom", date1);
            query.setParameter("validityDateTo", date2);
            query.setParameter("userphoneList", userphoneList);
            return (List<ClientGoal>) query.getResultList();
        }catch (NoResultException e) {
			return new ArrayList<ClientGoal>();
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }

    }
    
    public List<ClientGoal> getAvailableGoals(Long clientCod, Long serviceCod, Date date1, Date date2) throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery(" SELECT cg FROM ClientGoal cg " 
            	+ " WHERE cg.service.serviceCod = :serviceCod AND "
                + " cg.client.clientCod = :clientCod AND "
                + " ((:validityDateFrom <= TRUNC(cg.validityDateTo) AND "
                + " TRUNC(cg.validityDateFrom) <= :validityDateTo) "
                + " OR ( TRUNC(cg.validityDateFrom) <= :validityDateTo "
                + " AND cg.validityDateTo is NULL)) ");
            query.setParameter("clientCod", clientCod);
            query.setParameter("serviceCod", serviceCod);
            query.setParameter("validityDateFrom", date1);
            query.setParameter("validityDateTo", date2);
            return (List<ClientGoal>) query.getResultList();
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }

    }

    private String getDescription(ClientGoal entityDetail) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String desc = " (".concat(
                sdf.format(entityDetail.getValidityDateFrom())).concat(" - ").concat(
                sdf.format(entityDetail.getValidityDateTo())).concat(")");

        return desc;
    }

    public String saveEdit(ClientGoal entity, Userweb user) throws Exception {
        try {
            if (entity.getClientGoalCod() == null) {
                super.create(entity);
            } else {
                edit(entity);
            }
        } catch (Exception e) {
            throw new Exception(MessageFormat.format(
                    getFacadeContainer().getI18nAPI().iValue(
                            "web.admin.backingBean.commons.message.AnErrorHasOcurred"),
                    e));
        }
        return null;
    }

    private ClientGoal getLastClietGoalByServiceClient(Long clientCod, Long serviceCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("" + " SELECT c "
                + " FROM ClientGoal c "
                + " WHERE c.client.clientCod = :clientCod "
                + " AND c.service.serviceCod = :serviceCod "
                + " AND c.validityDateTo is null");
            query.setParameter("clientCod", clientCod);
            query.setParameter("serviceCod", serviceCod);
            return (ClientGoal) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<Userphone> getUserphoneListByClientGoal(Long clientGoalCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            Query query = em.createQuery("" + " SELECT c.userphones "
                + " FROM ClientGoal c "
                + " WHERE c.clientGoalCod = :clientGoalCod");
            query.setParameter("clientGoalCod", clientGoalCod);
            return (List<Userphone>) query.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<Userphone>();
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<ClientGoal> getClietGoalListByUserphone(Long userphoneCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("" + " SELECT c "
                + " FROM ClientGoal c, " + " IN (c.userphones) u"
                + " WHERE u.userphoneCod = :userphoneCod");
            query.setParameter("userphoneCod", userphoneCod);
            return (List<ClientGoal>) query.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<ClientGoal>();
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<String> getDateRanges(String dayFrom, String dayTo, Date dateFrom, Date dateTo) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String periodFrom = sdf.format(dateFrom);

            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(dateTo);
            int lastDayOfMonth = gc.getActualMaximum(Calendar.DAY_OF_MONTH);
            gc.set(Calendar.DAY_OF_MONTH, lastDayOfMonth);

            String periodTo = sdf.format(gc.getTime());

            String sql = " SELECT DISTINCT to_date('"
                + dayFrom
                + "' || '/' || to_Char(d, 'MM/YYYY'), 'dd/MM/yyyy') fecha_ini, "
                + " add_months(to_date('"
                + dayFrom
                + "' || '/' || to_Char(d, 'MM/YYYY'),'dd/mm/yyyy'),1) - 1 fecha_fin "
                + " FROM (SELECT TO_DATE('" + periodFrom
                + "', 'dd/MM/yyyy') + rownum AS d "
                + "          FROM all_objects " + "         WHERE TO_DATE('"
                + periodFrom + "', 'dd/MM/yyyy') + rownum <= "
                + "               TO_DATE('" + periodTo
                + "', 'dd/MM/yyyy')) a " + " order by fecha_ini desc";

            Query q = em.createNativeQuery(sql);
            List<Object[]> ret = (List<Object[]>) q.getResultList();
            if (ret != null && ret.size() > 0) {
                List<String> retornar = new ArrayList<String>();
                for (Object[] obj : ret) {
                    Timestamp t1 = (Timestamp) obj[0];
                    Timestamp t2 = (Timestamp) obj[1];
                    String value = sdf.format(new Date(t1.getTime())).concat(
                            " - ").concat(sdf.format(new Date(t2.getTime())));
                    retornar.add(value);
                }
                return retornar;
            }
            return new ArrayList<String>();
        } catch (Exception e) {
            return new ArrayList<String>();
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public void deleteEntities(List<ClientGoal> entitylist) throws GenericFacadeException {
        try {
            for (ClientGoal clientGoal : entitylist) {
                ClientGoal clientGoal_ = find(clientGoal.getClientGoalCod());
                if (clientGoal_ != null) {
                    clientGoal_.setValidityDateTo(new Date());
                    // clientGoal_.setDescription(clientGoal_.getDescription().concat(getDescription(clientGoal_)));
                    edit(clientGoal_);
                }
            }

        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), MessageFormat.format(
                    getFacadeContainer().getI18nAPI().iValue(
                            "web.client.backingBean.message.Error"), e));
        }
    }
    
    public List<Userphone> getUserphoneListByClientGoalList(List<ClientGoal> clientGoalList) {
        try {
        	List<Userphone> uList = new ArrayList<Userphone>();
            for (ClientGoal clientGoal : clientGoalList) {
            	List<Userphone> aux = getUserphoneListByClientGoal(clientGoal.getClientGoalCod());
            	if (aux != null && aux.size() > 0){
            		for (Userphone u : aux) {
						if (!uList.contains(u))
							uList.add(u);
					}
            	}
			}
            return uList;
        } catch (Exception e) {
            return new ArrayList<Userphone>();
        } 
    }
    
    public List<Userphone> getUserphoneList(Long clientCod, List<Classification> classif) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery(" SELECT distinct(u) " +
            							 " FROM Userphone u, "  +
            							 " IN (u.clientGoals) cg, " +
            							 " IN (u.classificationList ) cl " +
            							 " WHERE cg IS NOT NULL " +
            							 " AND u.client.clientCod = :clientCod " +
            							 " AND cl in (:classifications)" +
            							 " AND u.enabledChr = true ");
            query.setParameter("clientCod", clientCod);
            query.setParameter("classifications", classif);
            return (List<Userphone>) query.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<Userphone>();
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }
    
//    @Override  in (:classifications)
//    public ClientGoal edit(ClientGoal entity) {
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