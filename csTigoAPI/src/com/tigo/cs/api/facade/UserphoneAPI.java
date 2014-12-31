package com.tigo.cs.api.facade;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.tigo.cs.commons.jpa.EmptyResultException;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.jpa.MoreThanOneResultException;
import com.tigo.cs.commons.jpa.WarningTypeException;
import com.tigo.cs.commons.log.Action;
import com.tigo.cs.commons.web.fileupload.UploadFile;
import com.tigo.cs.domain.Classification;
import com.tigo.cs.domain.Client;
import com.tigo.cs.domain.ClientServiceFunctionality;
import com.tigo.cs.domain.ServiceFunctionality;
import com.tigo.cs.domain.Userphone;
import com.tigo.cs.domain.UserphoneMeta;
import com.tigo.cs.domain.Userweb;
import com.tigo.cs.domain.ussd.UssdMenuEntryUssdUser;
import com.tigo.cs.domain.ussd.UssdMenuEntryUssdUserPK;
import com.tigo.cs.domain.ussd.UssdUser;

public abstract class UserphoneAPI extends AbstractAPI<Userphone> {

    public UserphoneAPI() {
        super(Userphone.class);
    }

    @Override
    public Userphone create(Userphone entity) {
        if (entity.getEnabledChr() == null) {
            entity.setEnabledChr(true);
        }
        if (entity.getEnabledChr()) {
            Userphone userphoneActive = null;
            try {
                userphoneActive = findActiveByCellphoneNum(entity.getCellphoneNum());
                if (userphoneActive != null) {
                    throw new WarningTypeException(MessageFormat.format(
                            getFacadeContainer().getI18nAPI().iValue(
                                    "ejb.admin.userPhoneFacade.exception.AlreadyActiveClient"),
                            entity.getCellphoneNum().toString(),
                            userphoneActive.getClient().getNameChr()));
                }
            } catch (Exception ex) {
                throw new WarningTypeException(MessageFormat.format(
                        getFacadeContainer().getI18nAPI().iValue(
                                "ejb.admin.userPhoneFacade.exception.AlreadyActiveClient"),
                        entity.getCellphoneNum().toString(),
                        userphoneActive.getClient().getNameChr()));
            }
        }

        /*
         * 
         * Agregamos la clasificacion raiz del cliente
         */
        Classification rootClassification = null;
        try {
            Userphone userphone = (entity);
            rootClassification = getFacadeContainer().getClassificationAPI().getRootClassification(
                    userphone.getClient());

            if (userphone.getClassificationList() == null) {
                List<Classification> classificationList = new ArrayList<Classification>();
                classificationList.add(rootClassification);
            }

        } catch (EmptyResultException e1) {
            getFacadeContainer().getNotifier().error(ClassificationAPI.class,
                    null, e1.getMessage(), e1);
        } catch (MoreThanOneResultException e1) {
            getFacadeContainer().getNotifier().error(ClassificationAPI.class,
                    null, e1.getMessage(), e1);
        }

        Userphone userphone = super.create(entity);
        return userphone;
    }

    public Userphone createByProvisioning(Userphone userphoneToCreate) {
        Userphone userphone = create(userphoneToCreate);
        try {
            // Se crea el usuario en la plataforma USSD
            UssdUser ussdUser = new UssdUser();
            ussdUser.setId(userphone.getUserphoneCod());
            ussdUser.setCode(userphone.getUserphoneCod().toString());
            ussdUser.setName(userphone.getNameChr());
            ussdUser.setDescription(userphone.getDescriptionChr());
            ussdUser.setUssdApplication(null);
            ussdUser = getFacadeContainer().getUssdUserAPI().create(ussdUser);
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(ClassificationAPI.class,
                    null, e.getMessage(), e);
        }
        return userphone;
    }

    public Userphone predit(Userphone entity) {
        Userphone userphoneActive = null;
        try {
            if (entity.getEnabledChr()) {
                userphoneActive = findActiveByCellphoneNum(entity.getCellphoneNum());
                if (userphoneActive != null
                    && !userphoneActive.getUserphoneCod().equals(
                            entity.getUserphoneCod())) {
                    throw new WarningTypeException(MessageFormat.format(
                            getFacadeContainer().getI18nAPI().iValue(
                                    "ejb.admin.userPhoneFacade.exception.AlreadyActiveClient"),
                            entity.getCellphoneNum().toString(),
                            userphoneActive.getClient().getNameChr()));
                }
            }
        } catch (Exception ex) {
            getFacadeContainer().getNotifier().error(UseradminAPI.class, null,
                    ex.getMessage(), ex);
            throw new WarningTypeException(MessageFormat.format(
                    getFacadeContainer().getI18nAPI().iValue(
                            "ejb.admin.userPhoneFacade.exception.AlreadyActiveClient"),
                    entity.getCellphoneNum().toString(),
                    userphoneActive.getClient().getNameChr()));
        }

        Userphone userphone = super.edit(entity);
        return userphone;
    }

    public Userphone addDeviceInfo(Userphone entity) {

        EntityManager em = null;
        Userphone userphoneManaged = null;
        try {
            em = getFacadeContainer().getEntityManager();
            if (getFacadeContainer().isEntityManagerTransactional()) {
                em.getTransaction().begin();
            }

            userphoneManaged = find(entity.getUserphoneCod());
            userphoneManaged.setCurrentImei(entity.getCurrentImei());
            userphoneManaged.setCurrentImsi(entity.getCurrentImsi());
            em.merge(userphoneManaged);

            if (getFacadeContainer().isEntityManagerTransactional()) {
                em.getTransaction().commit();
            }

            getFacadeContainer().getNotifier().info(getClass(),
                    entity.getCellphoneNum().toString(),
                    "BMA - ANDROID - Se modifico la informacion del dispositivo permitido.");

        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(UserphoneAPI.class, null,
                    e.getMessage(), e);
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
        return userphoneManaged;
    }

    @Override
    public Userphone edit(Userphone entity) {
        EntityManager em = null;
        try {
            if (!getFacadeContainer().isEntityManagerTransactional()) {
                if (entity.getEnabledChr() == null) {
                    entity.setEnabledChr(true);
                }

                em = getFacadeContainer().getEntityManager();
                if (entity.getEnabledChr()) {
                    Userphone userphoneActive = null;
                    try {
                        userphoneActive = findActiveByCellphoneNum(entity.getCellphoneNum());
                        // em.refresh(userphoneActive);
                        if (userphoneActive != null
                            && !userphoneActive.getUserphoneCod().equals(
                                    entity.getUserphoneCod())) {
                            throw new WarningTypeException(MessageFormat.format(
                                    getFacadeContainer().getI18nAPI().iValue(
                                            "ejb.admin.userPhoneFacade.exception.AlreadyActiveClient"),
                                    entity.getCellphoneNum().toString(),
                                    userphoneActive.getClient().getNameChr()));
                        }
                    } catch (GenericFacadeException ex) {
                        Logger.getLogger(UserphoneAPI.class.getName()).log(
                                null, ex);
                        throw new WarningTypeException(MessageFormat.format(
                                getFacadeContainer().getI18nAPI().iValue(
                                        "ejb.admin.userPhoneFacade.exception.AlreadyActiveClient"),
                                entity.getCellphoneNum().toString(),
                                userphoneActive.getClient().getNameChr()));

                    } catch (Exception ex) {
                        Logger.getLogger(UserphoneAPI.class.getName()).log(
                                null, ex);
                    }

                }

                Userphone userphoneManaged = find(entity.getUserphoneCod(),
                        "clientServiceFunctionalityList", "trackingPeriods",
                        "clientGoals", "metaData", "userphoneMetaList");
                userphoneManaged.setNameChr(entity.getNameChr());
                userphoneManaged.setUserphoneMetaList(entity.getUserphoneMetaList());
                userphoneManaged.setClassificationList(entity.getClassificationList());
                userphoneManaged.setClientServiceFunctionalityList(entity.getClientServiceFunctionalityList());
                userphoneManaged.setClientGoals(entity.getClientGoals());
                userphoneManaged.setTrackingPeriods(entity.getTrackingPeriods());
                userphoneManaged.setMetaData(entity.getMetaData());
                userphoneManaged.setUserphoneMetaList(entity.getUserphoneMetaList());
                userphoneManaged.setEnabledChr(entity.getEnabledChr());
                userphoneManaged.setDescriptionChr(entity.getDescriptionChr());
                userphoneManaged.setDisabledDate(entity.getDisabledDate());
                userphoneManaged.setCurrentImei(entity.getCurrentImei());
                userphoneManaged.setCurrentImsi(entity.getCurrentImsi());
                em.merge(userphoneManaged);
                return entity;
            }
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(UserphoneAPI.class, null,
                    e.getMessage(), e);
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }

        return null;

    }

    @Override
    public void remove(Userphone entity) {
        super.remove(find(entity.getUserphoneCod(), true));
    }

    public List<Userphone> findAllEnabled() throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createNamedQuery("Userphone.findByEnabledChr");
            query.setParameter("enabledChr", true);
            return query.getResultList();
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(ClassificationAPI.class,
                    null, e.getMessage(), e);
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<Userphone> getEnabledUserphonesByClient(Client client) throws MoreThanOneResultException, GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createNamedQuery("Userphone.findByEnabledAndClient");
            query.setParameter("enabledChr", true);
            query.setParameter("client", client);
            return query.getResultList();
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(ClassificationAPI.class,
                    null, e.getMessage(), e);
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<Userphone> findByUserwebAndClassification(Userweb userweb) {
        return findByUserwebAndClassificationAndService(userweb, null);
    }

    public List<Userphone> findByUserwebAndClassificationAndService(Userweb userweb, Long codService) {
        EntityManager em = null;

        String serviceSql = " AND f.serviceFunctionality.service.serviceCod = :codService ";

        String sql = "SELECT DISTINCT u FROM Userphone u,"
            + " IN (u.classificationList ) cl ,"
            + " IN (u.clientServiceFunctionalityList) f "
            + " WHERE u.enabledChr = true AND cl in (:classifications) {0} ORDER BY u.nameChr ";

        sql = MessageFormat.format(sql, codService != null ? serviceSql : "");
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery(sql);

            List<Classification> classificationList = getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                    userweb);

            if (classificationList == null) {
                return null;
            }

            q.setParameter("classifications", classificationList);
            if (codService != null) {
                q.setParameter("codService", codService);
            }
            return q.getResultList();
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(ClassificationAPI.class,
                    null, e.getMessage(), e);
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<Userphone> findByFeatureServiceFunctionality(Long codFeatureElement, Long codService, Long codFunctionality) {
        EntityManager em = null;

        String sql = "SELECT DISTINCT u "
            + " FROM Userphone u, "
            + " IN (u.featureElements) fe ,"
            + " IN (u.clientServiceFunctionalityList) csf "
            + " WHERE u.enabledChr = true "
            + " AND fe.featureElementCod = :codFeatureElement "
            + " AND csf.clientServiceFunctionalityPK.codService = :codService "
            + " AND csf.clientServiceFunctionalityPK.codFunctionality = :codFunctionality "
            + " ORDER BY u.nameChr ";

        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery(sql);
            q.setParameter("codFeatureElement", codFeatureElement);
            q.setParameter("codService", codService);
            q.setParameter("codFunctionality", codFunctionality);
            return q.getResultList();
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(ClassificationAPI.class,
                    null, e.getMessage(), e);
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public Userphone findActiveByCellphoneNum(BigInteger cellphoneNum) throws Exception {
        try {
            return findByMsisdnAndActive(cellphoneNum, true);
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(ClassificationAPI.class,
                    null, e.getMessage(), e);
            throw e;
        }
    }

    public Userphone findByMsisdnAndActive(BigInteger cellphoneNum, boolean active) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT up FROM Userphone up "
                + "WHERE up.cellphoneNum = :cellphoneNum "
                + "AND up.enabledChr = :active "
                + "ORDER BY up.userphoneCod DESC");
            query.setParameter("cellphoneNum", cellphoneNum);
            query.setParameter("active", active);
            query.setMaxResults(1);
            return (Userphone) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(UserphoneAPI.class, null,
                    e.getMessage(), e);
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public Userphone findByMsisdnAndActiveAndClient(BigInteger cellphoneNum, Long clientCod, boolean active) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT up FROM Userphone up "
                + "WHERE up.cellphoneNum = :cellphoneNum "
                + "AND up.enabledChr = :active "
                + "AND up.client.clientCod = :clientCod "
                + "ORDER BY up.userphoneCod DESC");
            query.setParameter("cellphoneNum", cellphoneNum);
            query.setParameter("active", active);
            query.setParameter("clientCod", clientCod);
            query.setMaxResults(1);
            return (Userphone) query.getSingleResult();
        } catch (NoResultException e) {
            getFacadeContainer().getNotifier().error(ClassificationAPI.class,
                    null, e.getMessage(), e);
            return null;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(UseradminAPI.class, null,
                    e.getMessage(), e);
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public Userphone findByMsisdnAndClient(BigInteger msisdn, String bcssClientCod) throws MoreThanOneResultException, GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT up " + "FROM Userphone up "
                + "WHERE up.cellphoneNum = :cellphoneNum "
                + "AND up.client.systemcodeChr = :systemcodeChr "
                + "ORDER BY up.userphoneCod DESC");
            query.setParameter("cellphoneNum", msisdn);
            query.setParameter("systemcodeChr", bcssClientCod);
            query.setMaxResults(1);
            return (Userphone) query.getSingleResult();
        } catch (NoResultException e) {
            getFacadeContainer().getNotifier().error(ClassificationAPI.class,
                    null, e.getMessage(), e);
            return null;
        } catch (NonUniqueResultException e) {
            getFacadeContainer().getNotifier().error(ClassificationAPI.class,
                    null, e.getMessage(), e);
            throw new MoreThanOneResultException(e);
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(ClassificationAPI.class,
                    null, e.getMessage(), e);
            throw new GenericFacadeException(getClass(), e);
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }

    }

    public Userphone findByCellphoneNum(BigInteger cellphoneNum) throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT up " + "FROM Userphone up "
                + "WHERE up.cellphoneNum = :cellphoneNum "
                + "AND up.enabledChr = true");
            query.setParameter("cellphoneNum", cellphoneNum);
            return (Userphone) query.getSingleResult();
        } catch (NoResultException e) {
            getFacadeContainer().getNotifier().error(ClassificationAPI.class,
                    null, e.getMessage(), e);
            return null;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(ClassificationAPI.class,
                    null, e.getMessage(), e);
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<ServiceFunctionality> getServiceFunctionalitiesByUserphone(Long userphoneCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery(""
                + "SELECT DISTINCT csfL.serviceFunctionality "
                + "FROM Userphone u, "
                + "IN (u.clientServiceFunctionalityList) csfL "
                + "WHERE u.userphoneCod = :userphonecod");
            query.setParameter("userphonecod", userphoneCod);
            return query.getResultList();
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(ClassificationAPI.class,
                    null, e.getMessage(), e);
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<ClientServiceFunctionality> getClientServiceFunctionalitiesByUserphone(Long userphoneCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("" + "SELECT DISTINCT csfL "
                + "FROM Userphone u, "
                + "IN (u.clientServiceFunctionalityList) csfL "
                + "WHERE u.userphoneCod = :userphonecod");
            query.setParameter("userphonecod", userphoneCod);
            return query.getResultList();
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(ClassificationAPI.class,
                    null, e.getMessage(), e);
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<Userphone> getUserphonesWithTrackingServiceFunc(Long functionalityCod) throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT DISTINCT u "
                + "FROM Userphone u, "
                + "IN (u.clientServiceFunctionalityList) csfL "
                + "WHERE u.enabledChr = true "
                + "AND csfL.serviceFunctionality.service.serviceCod = 4 "
                + "AND csfL.serviceFunctionality.functionality.functionalityCod = :functionalityCod");
            query.setParameter("functionalityCod", functionalityCod);
            return query.getResultList();
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(ClassificationAPI.class,
                    null, e.getMessage(), e);
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public boolean getAndroidEnabledTracking(Long userphoneCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery(""
                + "SELECT DISTINCT csfL.serviceFunctionality "
                + "FROM Userphone u, "
                + "IN (u.clientServiceFunctionalityList) csfL "
                + "WHERE u.userphoneCod = :userphonecod "
                + "AND csfL.clientServiceFunctionalityPK.codFunctionality = 5 "
                + "AND csfL.clientServiceFunctionalityPK.codService = 4 ");
            query.setParameter("userphonecod", userphoneCod);
            if (query.getSingleResult() != null) {
                return true;
            }
        } catch (NoResultException e) {
            getFacadeContainer().getNotifier().debug(getClass(), null,
                    "La linea no tiene rastreo android");
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
        return false;
    }

    public List<Classification> getClassificationsByUserphone(Long userphoneCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("" + "SELECT u.classificationList "
                + "FROM Userphone u " + "WHERE u.userphoneCod = :userphonecod");
            query.setParameter("userphonecod", userphoneCod);
            List<Classification> lista = query.getResultList();
            return lista;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(ClassificationAPI.class,
                    null, e.getMessage(), e);
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    private static String getDateOfEvent() {
        return (new SimpleDateFormat("dd/MM/yyyy HH:mm").format(Calendar.getInstance().getTime()));
    }

    public String saveEdit(Userphone entity, String savehistory) throws GenericFacadeException {
        try {
            if (entity.getUserphoneCod() != null) {
                if (savehistory != null && savehistory.equalsIgnoreCase("true")) {
                    Userphone oldEntity = find(entity.getUserphoneCod());
                    oldEntity.setEnabledChr(false);
                    oldEntity.setDescriptionChr(oldEntity.getDescriptionChr().concat(
                            ". Deshabilitado por renombramiento en fecha ".concat(getDateOfEvent())));
                    this.edit(oldEntity);

                    Userphone newUserphone = new Userphone();
                    newUserphone.setCellphoneNum(entity.getCellphoneNum());
                    newUserphone.setClassificationList(entity.getClassificationList());
                    newUserphone.setClient(entity.getClient());
                    newUserphone.setClientServiceFunctionalityList(entity.getClientServiceFunctionalityList());
                    newUserphone.setCreatedDate(entity.getCreatedDate());
                    newUserphone.setDescriptionChr(entity.getDescriptionChr().concat(
                            ". Renombrado en fecha ".concat(getDateOfEvent())));
                    newUserphone.setEnabledChr(true);
                    newUserphone.setMessageCollection(entity.getMessageCollection());
                    newUserphone.setNameChr(entity.getNameChr());
                    newUserphone.setServiceValueCollection(entity.getServiceValueCollection());
                    newUserphone.setOffroadMarkoption(entity.getOffroadMarkoption());
                    newUserphone.setCurrentImei(entity.getCurrentImei());
                    newUserphone.setCurrentImsi(entity.getCurrentImsi());

                    newUserphone = this.create(newUserphone);

                    /*
                     * creamos el nuevo usuario correspondiente apra su
                     * utilizacion vis USSD
                     */
                    UssdUser ussdUser = new UssdUser();
                    ussdUser.setId(newUserphone.getUserphoneCod());
                    ussdUser.setCode(newUserphone.getUserphoneCod().toString());
                    ussdUser.setDescription(newUserphone.getDescriptionChr());
                    ussdUser.setName(newUserphone.getNameChr());
                    ussdUser.setUserphone(newUserphone);
                    ussdUser.setUssdApplication(null);

                    ussdUser = getFacadeContainer().getUssdUserAPI().create(
                            ussdUser);
                    /*
                     * reasociamos las entradas USSD asignadas al usuario
                     * anterior al nuevo y eliminamos las entradas
                     * correspondientes al usuario anterior
                     */
                    List<UssdMenuEntryUssdUser> ussdMenuEntryUssdUsers = getFacadeContainer().getUssdMenuEntryUssdUserAPI().findByUssduserId(
                            entity.getUserphoneCod());

                    if (ussdMenuEntryUssdUsers != null
                        && ussdMenuEntryUssdUsers.size() > 0) {
                        for (UssdMenuEntryUssdUser entryUser : ussdMenuEntryUssdUsers) {
                            UssdMenuEntryUssdUser newMenuEntry = new UssdMenuEntryUssdUser();
                            newMenuEntry.setUssdUser(ussdUser);
                            newMenuEntry.setUssdMenuEntryUssdUserPK(new UssdMenuEntryUssdUserPK(entryUser.getUssdMenuEntryUssdUserPK().getUssdmenuentriesId(), ussdUser.getId()));
                            getFacadeContainer().getUssdMenuEntryUssdUserAPI().create(
                                    newMenuEntry);
                            getFacadeContainer().getUssdMenuEntryUssdUserAPI().remove(
                                    entryUser);
                        }
                    }

                } else {
                    this.edit(entity);

                    /*
                     * cambiamos del lado USSD los posibles valores que pudieron
                     * ser modificados
                     */
                    UssdUser ussdUser = getFacadeContainer().getUssdUserAPI().find(
                            entity.getUserphoneCod());

                    ussdUser.setDescription(entity.getDescriptionChr());
                    ussdUser.setName(entity.getNameChr());
                    getFacadeContainer().getUssdUserAPI().edit(ussdUser);

                }
            } else {
                this.create(entity);
            }

        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(ClassificationAPI.class,
                    null, e.getMessage(), e);
            throw new GenericFacadeException(UseradminAPI.class, getFacadeContainer().getI18nAPI().iValue(
                    "web.client.backingBean.message.Error"), e);
        }
        return null;
    }

    public List<UserphoneMeta> findUserphoneMeta(Client client, Userphone userphone) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT DISTINCT um.userphoneMetaList FROM Userphone um "
                + " WHERE um.client.clientCod = :clientCod"
                + " AND um.userphoneCod = :userphoneCod");

            query.setParameter("clientCod", client.getClientCod());
            query.setParameter("userphoneCod", userphone.getUserphoneCod());
            List<UserphoneMeta> lista = query.getResultList();
            return lista;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(ClassificationAPI.class,
                    null, e.getMessage(), e);
            return new ArrayList<UserphoneMeta>();
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public Long getUserphoneNextval() {

        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createNativeQuery("SELECT CSTIGO.USERPHONE_SEQ.nextval FROM DUAL");
            BigDecimal nextVal = (BigDecimal) q.getSingleResult();
            return nextVal.longValue();
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(UserphoneAPI.class, null,
                    e.getMessage(), e);
            return 0L;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<Userphone> findByClientAndClassification(Long clientCod, Long classifCod) throws GenericFacadeException {
        EntityManager em = null;
        try {
            String whereCriteria = "WHERE up.client.clientCod = :clientCod AND up.classification.classificationCod = :classificationCod  AND up.enabledChr = true ";
            if (classifCod == null || classifCod.longValue() == 0L) {
                whereCriteria = "WHERE up.client.clientCod = :clientCod AND up.enabledChr = true ";
            }
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT up FROM Userphone up ".concat(
                    whereCriteria).concat(" ORDER BY up.nameChr ASC"));
            query.setParameter("clientCod", clientCod);
            if (classifCod != null && classifCod.longValue() != 0L) {
                query.setParameter("classificationCod", classifCod);
            }
            return query.getResultList();
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(ClassificationAPI.class,
                    null, e.getMessage(), e);
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<Classification> findClassificationUserweb(Userphone userphone) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery("SELECT u.classificationClist from "
                + "Userweb u where u.userwebCod = :userwebCod");
            q.setParameter("userwebCod", userphone.getUserphoneCod());
            return q.getResultList();
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(ClassificationAPI.class,
                    null, e.getMessage(), e);
            return new ArrayList<Classification>();
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public Userphone findByMsisdnAndClientCod(BigInteger cellphoneNum, Long clientCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT up " + "FROM Userphone up "
                + "WHERE up.cellphoneNum = :cellphoneNum "
                + "AND up.enabledChr = true "
                + "AND up.client.clientCod = :clientCod "
                + "ORDER BY up.userphoneCod DESC");
            query.setParameter("cellphoneNum", cellphoneNum);
            query.setParameter("clientCod", clientCod);
            query.setMaxResults(1);
            return (Userphone) query.getSingleResult();
        } catch (NonUniqueResultException nue) {
            getFacadeContainer().getNotifier().error(UserphoneAPI.class, null,
                    nue.getMessage(), nue);
            throw nue;
        } catch (NoResultException nre) {
            getFacadeContainer().getNotifier().error(UserphoneAPI.class, null,
                    nre.getMessage(), nre);
            return null;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().signal(UserphoneAPI.class,
                    Action.ERROR, e.getMessage());
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<Userphone> getUserphonesWithCustodianServiceFunc(Long functionalityCod) throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT DISTINCT u "
                + "FROM Userphone u, "
                + "IN (u.clientServiceFunctionalityList) csfL "
                + "WHERE u.enabledChr = true "
                + "AND csfL.serviceFunctionality.service.serviceCod = 6 "
                + "AND csfL.serviceFunctionality.functionality.functionalityCod = :functionalityCod");
            query.setParameter("functionalityCod", functionalityCod);
            return query.getResultList();
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(UserphoneAPI.class, null,
                    e.getMessage(), e);
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<Userphone> getUserphonesWithOpenedCustodianService() throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT DISTINCT u "
                + "FROM Userphone u, "
                + "IN (u.clientServiceFunctionalityList) csfL, "
                + "IN (u.serviceValueCollection) svc "
                + "WHERE u.enabledChr = true "
                + "AND csfL.serviceFunctionality.service.serviceCod = 6 "
                + "AND svc.column2Chr is not null "
                + "AND svc.column5Chr is null "
                + "AND svc.service.serviceCod = 6");
            return query.getResultList();
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(UserphoneAPI.class, null,
                    e.getMessage(), e);
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public int userphoneBulkUpload(UploadFile file, Client client, String delimeter) {
        if (file == null || client == null) {
            return 1;
        }
        if (delimeter == null) {
            delimeter = ";";
        }

        boolean withError = false;
        boolean notActive = false;

        try {
            BufferedReader reader = new BufferedReader(new StringReader(new String(file.getFileData())));
            String line = null;
            while ((line = reader.readLine()) != null) {
                Userphone userphone = new Userphone();
                userphone.setClient(client);
                userphone.setDescriptionChr(getFacadeContainer().getI18nAPI().iValue(
                        "ejb.admin.userPhoneFacade.title.MassLoadedFromFile").concat(
                        file.getFileName()).concat("."));
                try {
                    StringTokenizer stk = new StringTokenizer(line, delimeter);
                    for (int i = 0; i < 2 && stk.hasMoreTokens(); i++) {
                        if (i == 0) {
                            userphone.setCellphoneNum(new BigInteger(stk.nextToken().trim()));
                        } else if (i == 1) {
                            userphone.setNameChr(stk.nextToken().trim());
                        }
                    }
                    create(userphone);
                } catch (Exception ejbe) {
                    if (ejbe.getCause() != null
                        && ejbe.getCause() instanceof WarningTypeException) {
                        try {
                            userphone.setEnabledChr(false);
                            create(userphone);
                            notActive = true;
                        } catch (Exception e) {
                            withError = true;
                        }
                    } else {
                        withError = true;
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(UserphoneAPI.class.getName()).log(null, ex);
            getFacadeContainer().getNotifier().signal(
                    this.getClass(),
                    Action.ERROR,
                    getFacadeContainer().getI18nAPI().iValue(
                            "ejb.admin.userPhoneFacade.signal.FileLoadError"),
                    ex);
            return 2;
        }

        if (notActive && withError) {
            return 5;
        } else if (notActive) {
            return 3;
        } else if (withError) {
            return 4;
        }
        return 0;
    }

    public Userphone getUserphoneByClient(Userweb userweb, Long clientCod, Long userphoneCod) throws GenericFacadeException {
        EntityManager em = null;
        try {
            List<Classification> list = getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                    userweb);
            String classificationCod = "";
            for (Classification c : list) {
                classificationCod = classificationCod.concat(
                        c.getClassificationCod().toString()).concat(",");
            }

            if (classificationCod != null && !classificationCod.equals("")) {
                classificationCod = classificationCod.substring(0,
                        classificationCod.length() - 1);
            }

            String whereCriteria = MessageFormat.format(
                    "WHERE up.client.clientCod = :clientCod AND classi.classificationCod in ({0})  AND up.enabledChr = true "
                        + " AND up.userphoneCod = :userphoneCod",
                    classificationCod);
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT up FROM Userphone up, IN (up.classificationList) classi ".concat(whereCriteria));
            query.setParameter("clientCod", clientCod);
            query.setParameter("userphoneCod", userphoneCod);
            query.setMaxResults(1);
            return (Userphone) query.getSingleResult();
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(UserphoneAPI.class, null,
                    e.getMessage(), e);
            // throw new GenericFacadeException(this.getClass(), e);
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public Userphone getUserphoneByClient(Long clientCod, Long userphoneCod) throws GenericFacadeException {
        EntityManager em = null;
        try {

            String whereCriteria = "WHERE up.client.clientCod = :clientCod AND up.client.enabledChr = true AND up.enabledChr = true "
                + " AND up.userphoneCod = :userphoneCod";
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT up FROM Userphone up ".concat(whereCriteria));
            query.setParameter("clientCod", clientCod);
            query.setParameter("userphoneCod", userphoneCod);
            query.setMaxResults(1);
            return (Userphone) query.getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<Userphone> findByUserphonesWithTrackingService(Userphone userphone) throws GenericFacadeException {
        EntityManager em = null;
        try {
            List<Classification> list

            = getFacadeContainer().getClassificationAPI().findByUserphoneWithChilds(
                    userphone);
            if (list == null) {
                getFacadeContainer().getNotifier().warn(getClass(),
                        userphone.getCellphoneNum().toString(),
                        "El usuario no tiene clasificacion asignada");
            }
            String classificationCod = "";
            for (Classification c : list) {
                classificationCod = classificationCod.concat(
                        c.getClassificationCod().toString()).concat(",");
            }

            if (classificationCod != null && !classificationCod.equals("")) {
                classificationCod = classificationCod.substring(0,
                        classificationCod.length() - 1);
            }

            String whereCriteria = MessageFormat.format(
                    "WHERE up.client.clientCod = :clientCod AND classi.classificationCod in ({0}) "
                        + "AND up.enabledChr = true "
                        + "AND csfL.serviceFunctionality.service.serviceCod = 4 ",
                    classificationCod);
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT up FROM Userphone up, IN (up.classificationList) classi, "
                + " IN (up.clientServiceFunctionalityList) csfL ".concat(
                        whereCriteria).concat(" ORDER BY up.nameChr"));
            query.setParameter("clientCod",
                    userphone.getClient().getClientCod());
            return query.getResultList();
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(ClassificationAPI.class,
                    null, e.getMessage(), e);
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

}
