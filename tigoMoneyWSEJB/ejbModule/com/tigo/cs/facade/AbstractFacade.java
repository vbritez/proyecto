package com.tigo.cs.facade;

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

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;

import com.tigo.cs.commons.jpa.EmptyResultException;
import com.tigo.cs.commons.jpa.MoreThanOneResultException;
import com.tigo.cs.commons.jpa.PrimarySortedField;
import com.tigo.cs.commons.jpa.PrimarySortedFieldNotFoundException;
import com.tigo.cs.security.Notifier;

public abstract class AbstractFacade<T> {

    protected static String GLOBAL_JNDI = "java:global/{projectName}/tigoMoneyWSEJB/";
    private final Class<T> entityClass;
    private String primarySortedField;
    protected HashMap<String, Object> finderParams = new HashMap<String, Object>();
    
    @EJB
    protected Notifier notifier;
    
    @PersistenceContext(unitName = "csTigoMTSPU")
    private EntityManager em;
     
    public EntityManager getEntityManager() {
		return em;
	}

	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	protected AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
        TimeZone.setDefault(TimeZone.getTimeZone("America/Asuncion"));
        Locale.setDefault(new Locale("es", "py"));
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
            em = getEntityManager();
            em.persist(entity);
            return entity;
        } catch (Exception e) {
            notifier.error(null, getClass(), null,
                    e.getMessage(), e);
            return null;
        } 
    }

    // public T edit(T entity) {
    // throw new InvalidOperationException("Esta entidad no es editable)");
    // }

    public T edit(T entity) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.merge(entity);
            return entity;
        } catch (Exception e) {
            notifier.error(null, getClass(), null,
                    e.getMessage(), e);
            return null;
        } 
    }

    public void remove(T entity) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.remove(em.merge(entity));
        } catch (Exception e) {
            notifier.error(null, getClass(), null,
                    e.getMessage(), e);
        }
    }

    public T find(Object id) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            T entity = em.find(entityClass, id);
            return entity;
        } catch (Exception e) {
            // TODO: handle exception
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
                        Logger.getLogger(AbstractFacade.class.getName()).log(
                                Level.SEVERE, null, ex);
                    } catch (IllegalArgumentException ex) {
                        Logger.getLogger(AbstractFacade.class.getName()).log(
                                Level.SEVERE, null, ex);
                    } catch (InvocationTargetException ex) {
                        Logger.getLogger(AbstractFacade.class.getName()).log(
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
                    Logger.getLogger(AbstractFacade.class.getName()).log(
                            Level.SEVERE, null, nse);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(AbstractFacade.class.getName()).log(
                            Level.SEVERE, null, ex);
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(AbstractFacade.class.getName()).log(
                            Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex) {
                    Logger.getLogger(AbstractFacade.class.getName()).log(
                            Level.SEVERE, null, ex);
                } catch (Exception e) {
                    Logger.getLogger(AbstractFacade.class.getName()).log(
                            Level.SEVERE,
                            "Error",
                            e);
                }
            }
        } else {
            entity = find(id, true);
            String warMsg = MessageFormat.format(
                    "Ha cargado un objeto eager",
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
            em = getEntityManager();

            field = getPrimarySortedField();

            String sql = "SELECT O FROM ".concat(entityClass.getSimpleName()).concat(
                    " O");
            if (field != null) {
                sql = sql.concat(" ORDER BY ").concat(field);
            }
            Query q = em.createQuery(sql);
            return q.getResultList();
        } catch (PrimarySortedFieldNotFoundException ex) {
            Logger.getLogger(AbstractFacade.class.getName()).log(
                    Level.SEVERE,
                    "Error"
                        + ex.getMessage(), ex);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public List<T> findRange(int[] range) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            CriteriaQuery<T> cq = em.getCriteriaBuilder().createQuery(
                    entityClass);
            cq.select(cq.from(entityClass));
            javax.persistence.Query q = em.createQuery(cq);
            q.setMaxResults(range[1] - range[0]);
            q.setFirstResult(range[0]);
            return q.getResultList();
        } catch (Exception e) {
            Logger.getLogger(AbstractFacade.class.getName()).log(
                    Level.SEVERE,
                    "Error"
                        + e.getMessage(), e);
        } 
        return null;
    }

    


    public List<T> findWithNamedQuery(String namedQueryName) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            return em.createNamedQuery(namedQueryName).getResultList();
        } catch (Exception e) {
            return null;
        } 
    }

    public List<T> findWithNamedQuery(String queryName, int resultLimit) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            return em.createNamedQuery(queryName).setMaxResults(resultLimit).getResultList();
        } catch (Exception e) {
            return null;
        } 
    }

    public List<T> findListWithNamedQuery(String namedQueryName) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            return em.createNamedQuery(namedQueryName).getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public List<T> findListWithNamedQuery(String queryName, int resultLimit) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            return em.createNamedQuery(queryName).setMaxResults(resultLimit).getResultList();
        } catch (Exception e) {
            return null;
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
            em = getEntityManager();
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
        } 

    }

    public int executeUpdateWithNamedQuery(String namedQuery, Map<String, Object> parameters) throws Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Set<Entry<String, Object>> rawParameters = parameters.entrySet();
            Query query = em.createNamedQuery(namedQuery);

            for (Entry<String, Object> entry : rawParameters) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
            return query.executeUpdate();
        } catch (Exception e) {
            // notifier.log(getClass(), Action.ERROR,
            // e.getMessage());
            throw new Exception(e.getMessage());
        } 
    }

    public List<T> findListWithNamedQuery(String namedQueryName, Map<String, Object> parameters, int resultLimit) {
        EntityManager em = null;
        try {
            em = getEntityManager();
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

    public static AbstractFacade getLocalInstance(String facadeName) throws NamingException {
        return (AbstractFacade) (new InitialContext()).lookup(getGlobalJNDI().concat(
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
            em = getEntityManager();
            Query q = em.createNativeQuery("SELECT " + seqName
                + ".nextval FROM DUAL");
            BigDecimal nextVal = (BigDecimal) q.getSingleResult();
            return nextVal.longValue();
        } catch (Exception e) {
            return 0L;
        } 
    }

}
