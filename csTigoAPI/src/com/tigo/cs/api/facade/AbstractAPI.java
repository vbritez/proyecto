package com.tigo.cs.api.facade;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;

import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.commons.jpa.EmptyResultException;
import com.tigo.cs.commons.jpa.MoreThanOneResultException;
import com.tigo.cs.commons.jpa.PrimarySortedField;
import com.tigo.cs.commons.jpa.PrimarySortedFieldNotFoundException;
import com.tigo.cs.commons.log.Action;
import com.tigo.cs.domain.Classification;

public abstract class AbstractAPI<T> {

    protected static String GLOBAL_JNDI = "java:global/{projectName}/csTigoEJB/";
    private final Class<T> entityClass;
    private String primarySortedField;
    protected HashMap<String, Object> finderParams = new HashMap<String, Object>();

    protected FacadeContainer facadeContainer;

    protected AbstractAPI(Class<T> entityClass) {
        this.entityClass = entityClass;
        TimeZone.setDefault(TimeZone.getTimeZone("America/Asuncion"));
        Locale.setDefault(new Locale("es", "py"));
    }

    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    public void setFacadeContainer(FacadeContainer facadeContainer) {
        this.facadeContainer = facadeContainer;
    }

    protected HashMap<String, Object> getParameters() {
        return finderParams;
    }

    protected HashMap<String, Object> addSingleParam(String name, Object value) {
        finderParams = new HashMap<String, Object>();
        finderParams.put(name, value);
        return finderParams;
    }

    protected void addParam(String name, Object value) {
        if (finderParams == null) {
            finderParams = new HashMap<String, Object>();
        }
        finderParams.put(name, value);
    }

    protected HashMap<String, Object> prepareParams() {
        return new HashMap<String, Object>();
    }

    public T create(T entity) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            if (getFacadeContainer().isEntityManagerTransactional()) {
                em.getTransaction().begin();
            }
            em.persist(entity);
            if (getFacadeContainer().isEntityManagerTransactional()) {
                em.getTransaction().commit();
            }
            return entity;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(getClass(), null,
                    e.getMessage(), e);
            if (getFacadeContainer().isEntityManagerTransactional()) {
                em.getTransaction().rollback();
            }
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    // public T edit(T entity) {
    // throw new InvalidOperationException("Esta entidad no es editable)");
    // }

    public T edit(T entity) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            if (getFacadeContainer().isEntityManagerTransactional()) {
                em.getTransaction().begin();
            }
            em.merge(entity);
            if (getFacadeContainer().isEntityManagerTransactional()) {
                em.getTransaction().commit();
            }
            return entity;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(getClass(), null,
                    e.getMessage(), e);
            if (getFacadeContainer().isEntityManagerTransactional()) {
                em.getTransaction().rollback();
            }
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public void remove(T entity) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            if (getFacadeContainer().isEntityManagerTransactional()) {
                em.getTransaction().begin();
            }
            em.remove(em.merge(entity));
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

    public T find(Object id) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            T entity = em.find(entityClass, id);
            return entity;
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
        return null;
    }

    public T find(Object id, boolean eager) {
        T entity = find(id);
        if (eager) {
            Method methods[] = entityClass.getMethods();
            for (Method method : methods) {
                Class<?> returnClass = method.getReturnType();
                if (returnClass.equals(Collection.class)
                    || returnClass.equals(List.class)) {
                    try {
                        ((Collection<?>) method.invoke(entity, (Object[]) null)).size();
                    } catch (IllegalAccessException ex) {
                        Logger.getLogger(AbstractAPI.class.getName()).log(
                                Level.SEVERE, null, ex);
                    } catch (IllegalArgumentException ex) {
                        Logger.getLogger(AbstractAPI.class.getName()).log(
                                Level.SEVERE, null, ex);
                    } catch (InvocationTargetException ex) {
                        Logger.getLogger(AbstractAPI.class.getName()).log(
                                Level.SEVERE, null, ex);
                    }

                }
            }
        }
        return entity;
    }

    /*
     * verificar!!!!!!!!!!!! en JAVASE no va a funcionar
     */

    public T find(Object id, String... eagerCollections) {
        T entity = null;
        if (eagerCollections.length > 0) {
            entity = find(id);
            for (int i = 0; i < eagerCollections.length; i++) {
                String getterCollectionName = eagerCollections[i];
                getterCollectionName = "get".concat(getterCollectionName.substring(
                        0, 1).toUpperCase().concat(
                        getterCollectionName.substring(1)));

                try {
                    Method method = entityClass.getMethod(getterCollectionName,
                            (Class<?>[]) null);
                    ((Collection<?>) method.invoke(entity, (Object[]) null)).size();
                } catch (NoSuchMethodException nse) {
                    Logger.getLogger(AbstractAPI.class.getName()).log(
                            Level.SEVERE, null, nse);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(AbstractAPI.class.getName()).log(
                            Level.SEVERE, null, ex);
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(AbstractAPI.class.getName()).log(
                            Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex) {
                    Logger.getLogger(AbstractAPI.class.getName()).log(
                            Level.SEVERE, null, ex);
                } catch (Exception e) {
                    Logger.getLogger(AbstractAPI.class.getName()).log(
                            Level.SEVERE,
                            getFacadeContainer().getI18nAPI().iValue(
                                    "ejb.admin.abstractFacade.log.AbstractFacadeError"),
                            e);
                }
            }
        } else {
            entity = find(id, true);
            String warMsg = MessageFormat.format(
                    getFacadeContainer().getI18nAPI().iValue(
                            "ejb.admin.abstractFacade.log.EagerFecthWarning"),
                    entityClass.getSimpleName());
            Logger.getLogger(getClass().getSimpleName()).log(Level.WARNING,
                    warMsg);

        }
        return entity;
    }

    @SuppressWarnings("unchecked")
    public List<T> findAll() {
        String field = null;
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            field = getPrimarySortedField();

            String sql = "SELECT O FROM ".concat(entityClass.getSimpleName()).concat(
                    " O");
            if (field != null) {
                sql = sql.concat(" ORDER BY ").concat(field);
            }
            Query q = em.createQuery(sql);
            return q.getResultList();
        } catch (PrimarySortedFieldNotFoundException ex) {
            Logger.getLogger(AbstractAPI.class.getName()).log(
                    Level.SEVERE,
                    getFacadeContainer().getI18nAPI().iValue(
                            "ej.admin.commons.log.Error")
                        + ex.getMessage(), ex);
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public List<T> findRange(int[] range) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            CriteriaQuery<T> cq = em.getCriteriaBuilder().createQuery(
                    entityClass);
            cq.select(cq.from(entityClass));
            javax.persistence.Query q = em.createQuery(cq);
            q.setMaxResults(range[1] - range[0]);
            q.setFirstResult(range[0]);
            return q.getResultList();
        } catch (Exception e) {
            Logger.getLogger(AbstractAPI.class.getName()).log(
                    Level.SEVERE,
                    getFacadeContainer().getI18nAPI().iValue(
                            "ej.admin.commons.log.Error")
                        + e.getMessage(), e);
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
        return null;
    }

    public Integer count(String whereCriteria) {
        return count(whereCriteria, null);

    }

    public Integer count(String whereCriteria, List<Classification> classifications) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            String JPQL = String.format("SELECT COUNT(*) FROM %s o ",
                    entityClass.getSimpleName());
            if (whereCriteria != null && whereCriteria.trim().length() > 0) {
                JPQL += whereCriteria.trim();
            }

            Query q = em.createQuery(JPQL);
            if (classifications != null) {
                if (whereCriteria.contains("classifications")) {
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

    public List<T> findRange(int[] range, String whereCriteria, String orderbyCriteria, List<Classification> classifications) {

        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            String JPQL = "SELECT o FROM ".concat(entityClass.getSimpleName()).concat(
                    " o ");

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
                if (whereCriteria.contains("classifications")) {
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

    public List<T> findRange(int[] range, String whereCriteria) {
        return findRange(range, whereCriteria, null, null);
    }

    public List<T> findRange(int[] range, String whereCriteria, String orderBy) {
        return findRange(range, whereCriteria, orderBy, null);
    }

    public List<T> findWithNamedQuery(String namedQueryName) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            return em.createNamedQuery(namedQueryName).getResultList();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<T> findWithNamedQuery(String queryName, int resultLimit) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            return em.createNamedQuery(queryName).setMaxResults(resultLimit).getResultList();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<T> findListWithNamedQuery(String namedQueryName) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            return em.createNamedQuery(namedQueryName).getResultList();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<T> findListWithNamedQuery(String queryName, int resultLimit) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            return em.createNamedQuery(queryName).setMaxResults(resultLimit).getResultList();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<T> findListWithNamedQuery(String namedQueryName, Map<String, Object> parameters) {
        return findListWithNamedQuery(namedQueryName, parameters, 0);
    }

    public T findEntityWithNamedQuery(String namedQueryName, Map<String, Object> parameters) throws EmptyResultException, MoreThanOneResultException {
        return findEntityWithNamedQuery(namedQueryName, parameters, 0);
    }

    public T findEntityWithNamedQuery(String namedQueryName, Map<String, Object> parameters, int resultLimit) throws EmptyResultException, MoreThanOneResultException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Set<Entry<String, Object>> rawParameters = parameters.entrySet();
            Query query = em.createNamedQuery(namedQueryName);
            if (resultLimit > 0) {
                query.setMaxResults(resultLimit);
            }
            for (Entry<String, Object> entry : rawParameters) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
            return (T) query.getSingleResult();
        } catch (NoResultException e) {
            throw new EmptyResultException(e);
        } catch (NonUniqueResultException e) {
            throw new MoreThanOneResultException(e);
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }

    }

    public int executeUpdateWithNamedQuery(String namedQuery, Map<String, Object> parameters) throws Exception {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Set<Entry<String, Object>> rawParameters = parameters.entrySet();
            Query query = em.createNamedQuery(namedQuery);

            for (Entry<String, Object> entry : rawParameters) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
            return query.executeUpdate();
        } catch (Exception e) {
            // getFacadeContainer().getNotifier().log(getClass(), Action.ERROR,
            // e.getMessage());
            throw new Exception(e.getMessage());
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<T> findListWithNamedQuery(String namedQueryName, Map<String, Object> parameters, int resultLimit) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Set<Entry<String, Object>> rawParameters = parameters.entrySet();
            Query query = em.createNamedQuery(namedQueryName);
            if (resultLimit > 0) {
                query.setMaxResults(resultLimit);
            }
            for (Entry<String, Object> entry : rawParameters) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
            return query.getResultList();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public static String getGlobalJNDI() {
        String projectName = System.getProperty("Application.name");
        if (projectName == null) {
            projectName = "csTigoClient";
        }
        return GLOBAL_JNDI.replace("{projectName}", projectName);
    }

    public static <F> F getLocalInstance(Class<F> facadeClass) {
        try {
            return (F) (new InitialContext()).lookup(getGlobalJNDI().concat(
                    facadeClass.getSimpleName()));
        } catch (NamingException ex) {
            // Logger.getLogger(AbstractFacade.class.getName()).log(Level.SEVERE,
            // MessageFormat.format(I18nBeanEJB.iValueStatic("ejb.admin.abstractFacade.log.FacadeInstanceError"),
            // facadeClass.getSimpleName()), ex);
        }
        return null;
    }

    public static AbstractAPI getLocalInstance(String facadeName) throws NamingException {
        return (AbstractAPI) (new InitialContext()).lookup(getGlobalJNDI().concat(
                facadeName));
    }

    public String getPrimarySortedField() throws PrimarySortedFieldNotFoundException {
        if (primarySortedField == null) {
            Field[] fieds = entityClass.getDeclaredFields();
            for (Field field : fieds) {
                PrimarySortedField annotation = field.getAnnotation(PrimarySortedField.class);
                if (annotation != null) {
                    primarySortedField = field.getName();
                    break;
                }
            }
            if (primarySortedField == null) {
                throw new PrimarySortedFieldNotFoundException(entityClass);
            }
        }
        return primarySortedField;
    }

    public Long getNextval(String seqName) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createNativeQuery("SELECT " + seqName
                + ".nextval FROM DUAL");
            BigDecimal nextVal = (BigDecimal) q.getSingleResult();
            return nextVal.longValue();
        } catch (Exception e) {
            return 0L;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

}
