package com.tigo.cs.api.facade;

import java.math.BigDecimal;
import java.text.FieldPosition;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.hibernate.exception.ConstraintViolationException;

import com.tigo.cs.api.entities.MetadataCrudService;
import com.tigo.cs.commons.ejb.ConstraintException;
import com.tigo.cs.commons.ejb.MassivePersistenceException;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.log.Action;
import com.tigo.cs.domain.MapMark;
import com.tigo.cs.domain.Meta;
import com.tigo.cs.domain.MetaClient;
import com.tigo.cs.domain.MetaClientPK;
import com.tigo.cs.domain.MetaData;
import com.tigo.cs.domain.MetaDataPK;
import com.tigo.cs.domain.MetaMember;
import com.tigo.cs.domain.MetaMemberPK;
import com.tigo.cs.domain.Userphone;
import com.tigo.cs.domain.Userweb;

public abstract class MetaDataAPI extends AbstractAPI<MetaData> {

    public MetaDataAPI() {
        super(MetaData.class);
    }

    @Override
    public MetaData edit(MetaData entity) {
        MetaData md = entity;
        md.setMetaMember(getFacadeContainer().getMetaMemberAPI().find(
                new MetaMemberPK(md.getMetaDataPK().getCodMeta(), md.getMetaDataPK().getCodMember())));
        md.setMetaClient(getFacadeContainer().getMetaClientAPI().find(
                new MetaClientPK(md.getMetaDataPK().getCodMeta(), md.getMetaDataPK().getCodClient())));

        return super.edit(entity);
    }

    public void processMetadatas(Userweb userweb, HashMap<MetaDataPK, String> mdPks, HashMap<MetaDataPK, List<Long>> mdUserphone) throws MassivePersistenceException, GenericFacadeException, ConstraintException {
        Logger.getLogger(MetaDataAPI.class.getName()).log(Level.INFO,
                "INICIO DE CARGA DE METADATA " + new Date());
        for (Entry<MetaDataPK, String> entry : mdPks.entrySet()) {
            try {
                MetaDataPK mdPK = entry.getKey();
                MetaData md = new MetaData(mdPK);
                md.setMetaClient(getFacadeContainer().getMetaClientAPI().findByMetaAndClient(
                        mdPK.getCodClient(), mdPK.getCodMeta()));
                md.setMetaMember(getFacadeContainer().getMetaMemberAPI().findByCodMetaAndMemberCod(
                        mdPK.getCodMeta(), mdPK.getCodMember()));
                md.setValueChr(entry.getValue());

                if (mdUserphone != null) {
                    processUserphoneMeta(userweb, md, mdUserphone);
                } else {

                    /* Recuperamos el metadata de la BD y su lista de Userphones */
                    MetaData persistedMeta = getFacadeContainer().getMetaDataAPI().find(
                            md.getMetaDataPK());

                    if (persistedMeta == null) {
                        create(md);
                    } else {
                        getFacadeContainer().getNotifier().signal(
                                getClass(),
                                Action.WARNING,
                                MessageFormat.format(
                                        getFacadeContainer().getI18nAPI().iValue(
                                                "ejb.client.metaDatafacade.duplicate.meta"),
                                        md.getMetaDataPK().getCodClient().toString(),
                                        md.getMetaDataPK().getCodMeta().toString(),
                                        md.getMetaDataPK().getCodMember().toString(),
                                        md.getMetaDataPK().getCodeChr()));
                    }
                }

            } catch (GenericFacadeException ex) {
                Logger.getLogger(MetaDataAPI.class.getName()).log(
                        Level.SEVERE,
                        getFacadeContainer().getI18nAPI().iValue(
                                "ejb.client.metaDatafacade.log.MassProcess"),
                        ex);
                throw ex;
            } catch (PersistenceException pe) {
                throw new MassivePersistenceException(MessageFormat.format(
                        getFacadeContainer().getI18nAPI().iValue(
                                "ejb.client.metaDatafacade.exception.MassProcessCreateClientMetaData"),
                        entry.getKey().getCodClient().toString()), entry.getKey().getCodeChr());
            } catch (ConstraintViolationException ex) {
                throw new ConstraintException(getFacadeContainer().getI18nAPI().iValue(
                        "web.client.backingBean.abstractCrudBean.message.ConstraintErrorMassLoad"));
            } catch (ConstraintException e) {
                getFacadeContainer().getNotifier().signal(getClass(),
                        Action.ERROR, e.getMessage(), e);
                throw new ConstraintException(e.getMessage());
            } catch (Exception e) {
                getFacadeContainer().getNotifier().signal(getClass(),
                        Action.ERROR, e.getMessage(), e);
                throw new MassivePersistenceException(MessageFormat.format(
                        getFacadeContainer().getI18nAPI().iValue(
                                "ejb.client.metaDatafacade.exception.MassProcessCreateClientMetaData"),
                        entry.getKey().getCodClient().toString()), entry.getKey().getCodeChr());
            }
        }
        Logger.getLogger(MetaDataAPI.class.getName()).log(Level.INFO,
                "FIN DE CARGA DE METADATA " + new Date());
    }

    public MetaData persistMetaData(MetaData md) throws Exception {
        return super.create(md);
    }

    @Override
    public void remove(MetaData entity) {
        super.remove(find(entity.getMetaDataPK(), true));
    }

    /**
     * Obtiene una colecci�n de Metas dado un cliente.
     * 
     * @param codClient
     *            C�digo del cliente
     * @return una lista de metas asignado al cliente.
     */
    public MetaData findByClientMetaMemberAndCode(Long codClient, Long codMeta, Long codMember, String code) {
        try {
            finderParams = prepareParams();
            finderParams.put("codClient", codClient);
            finderParams.put("codMeta", codMeta);
            finderParams.put("codMember", codMember);
            finderParams.put("codeChr", code);
            return findEntityWithNamedQuery(
                    "MetaData.findByCodClientCodMetaCodMemberCodeChr",
                    finderParams);
        } catch (Exception e) {
            return null;
        }

    }

    public MetaData findByClientMetaMemberAndValue(Long codClient, Long codMeta, Long codMember, String value) {
        try {
            finderParams = prepareParams();
            finderParams.put("codClient", codClient);
            finderParams.put("codMeta", codMeta);
            finderParams.put("codMember", codMember);
            finderParams.put("valueChr", value);
            return findEntityWithNamedQuery(
                    "MetaData.findByCodClientCodMetaCodMemberValueChr",
                    finderParams);
        } catch (Exception e) {
            return null;
        }

    }

    public List<MetaData> findByClientMetaCode(Long codClient, Long codMeta, String code) {
        finderParams = prepareParams();
        finderParams.put("codClient", codClient);
        finderParams.put("codMeta", codMeta);
        finderParams.put("codeChr", code);
        return findListWithNamedQuery("MetaData.findByCodClientCodMetaCodeChr",
                finderParams);
    }

    public List<MetaData> findByClientMemberValue(Long codClient, Long codMember, String value) {
        try {
            finderParams = prepareParams();
            finderParams.put("codClient", codClient);
            finderParams.put("codMember", codMember);
            finderParams.put("valueChr", value);
            return findListWithNamedQuery("MetaData.findByClientMemberValue",
                    finderParams);
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * Obtiene una colecci�n de MetaDatas.
     * 
     * @param codClient
     *            C�digo del cliente
     * @param codMeta
     *            C�digo del meta
     * @param codMember
     *            C�digo del miembro del metadata
     * @return una lista de metadatas filtrando el retorno de acuerdo al
     *         cliente, meta y miembro.
     */
    public List<MetaData> findByClientMetaMember(Long codClient, Long codMeta, Long codMember) {
        finderParams = prepareParams();
        finderParams.put("codClient", codClient);
        finderParams.put("codMeta", codMeta);
        finderParams.put("codMember", codMember);
        return findListWithNamedQuery(
                "MetaData.findByCodClientCodMetaCodMember", finderParams);
    }

    /**
     * Obtiene una colecci�n de MetaDatas.
     * 
     * @param codClient
     *            C�digo del cliente
     * @param codMeta
     *            C�digo del meta
     * @return una lista de metadatas filtrando el retorno de acuerdo al
     *         cliente, meta y miembro.
     */
    public List<MetaData> findByClientMeta(Long codClient, Long codMeta) {
        finderParams = prepareParams();
        finderParams.put("codClient", codClient);
        finderParams.put("codMeta", codMeta);
        return findListWithNamedQuery("MetaData.findByCodClientCodMeta",
                finderParams);
    }

    public int deleteMetaDataClient(Long codClient, Long codMeta) throws Exception {
        finderParams = prepareParams();
        finderParams.put("codClient", codClient);
        finderParams.put("codMeta", codMeta);
        return executeUpdateWithNamedQuery("MetaData.deleteEntitiesMass",
                finderParams);
    }

    public int deleteMetaDatasByCodeChr(Long codClient, Long codMeta, String codeChr) throws Exception {
        finderParams = prepareParams();
        finderParams.put("codClient", codClient);
        finderParams.put("codMeta", codMeta);
        finderParams.put("codeChr", codeChr);
        return executeUpdateWithNamedQuery("MetaData.deleteMetaDataByCodeChr",
                finderParams);
    }

    public int deleteMetaDataByMember(Long codClient, Long codMeta, Long codMember, String codeChr) throws Exception {
        finderParams = prepareParams();
        finderParams.put("codClient", codClient);
        finderParams.put("codMeta", codMeta);
        finderParams.put("codeChr", codeChr);
        finderParams.put("codMember", codMember);
        return executeUpdateWithNamedQuery("MetaData.deleteMetaDataByMember",
                finderParams);
    }

    @Override
    public Integer count(String whereCriteria) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            String JPQL = ("SELECT COUNT( DISTINCT o.metaDataPK.codeChr) FROM MetaData o ");
            if (whereCriteria != null && whereCriteria.trim().length() > 0) {
                JPQL += whereCriteria.trim();
            }

            Query q = em.createQuery(JPQL);
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

    public List<MetaData> findByClientMetaMemberAndValue(Long codClient, Long codMeta, String where) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            String sql = "SELECT md from MetaData md "
                + "WHERE md.metaDataPK.codClient = :codClient "
                + "AND md.metaDataPK.codMeta = :codMeta "
                + "AND md.metaMember.searchableChr = true "
                + "AND md.metaDataPK.codeChr in ("
                + "     SELECT m.metaDataPK.codeChr FROM MetaData m "
                + "     WHERE m.metaDataPK.codClient = :codClient "
                + "     AND m.metaDataPK.codMeta = :codMeta "
                + "AND ".concat(where)
                + "     AND m.metaMember.searchableChr = true )"
                + "ORDER BY md.metaDataPK.codeChr, md.metaDataPK.codMember ";
            Query query = em.createQuery(sql);
            query.setParameter("codClient", codClient);
            query.setParameter("codMeta", codMeta);
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
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

    public boolean findMetaAssociation(Long codClient, Long codMeta, Long codMember, String codeChr, Long userphoneCode) {

        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            Query q = null;
            if (userphoneCode != null) {
                q = em.createNamedQuery("MetaData.associatedMetaUserphone");
                q.setParameter("userphoneCod", userphoneCode);
            } else {
                q = em.createNamedQuery("MetaData.associatedMeta");
            }

            q.setParameter("codClient", codClient);
            q.setParameter("codMeta", codMeta);
            q.setParameter("codMember", codMember);
            q.setParameter("codeChr", codeChr);

            List<Userphone> result = q.getResultList();
            return result != null && result.size() > 0 ? true : false;
        } catch (Exception e) {
            return false;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    /**
     * Obtiene la lista de c�digos de meta-datas habilitados para un cliente.
     * */
    public List<Long> getMetadataCodByUserphone(Long clientCod) throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery("" + " SELECT DISTINCT mc.meta.metaCod "
                + " FROM MetaClient mc "
                + " WHERE mc.client.clientCod = :clientCod"
                + "   AND mc.enabledChr = true");
            q.setParameter("clientCod", clientCod);
            List<Long> result = q.getResultList();

            return result;
        } catch (NoResultException e) {
            return new ArrayList<Long>();
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), "Cannot obtain list of metadata by client from facade.", e);
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    @Override
    public MetaData create(MetaData metadata) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            em.persist(metadata);
            return em.find(MetaData.class, metadata.getMetaDataPK());
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public void create(MetaClient client, Meta meta, MetaMember member, String code, String value, MapMark mark) throws Exception {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            if (getFacadeContainer().isEntityManagerTransactional()) {
                em.getTransaction().begin();
            }

            MetaData m = findByClientMetaMemberAndCode(
                    client.getMetaClientPK().getCodClient(), meta.getMetaCod(),
                    member.getMetaMemberPK().getMemberCod(), code);

            if (m != null) {
                m.setValueChr(value);
                m.setMapMark(mark);
                m = edit(m);
            } else {
                m = new MetaData();
                m.setMetaClient(getFacadeContainer().getMetaClientAPI().find(
                        client.getMetaClientPK()));
                m.setMetaMember(getFacadeContainer().getMetaMemberAPI().find(
                        member.getMetaMemberPK()));

                MetaDataPK pk = new MetaDataPK();
                pk.setCodClient(client.getClient().getClientCod());
                pk.setCodMeta(meta.getMetaCod());
                pk.setCodMember(member.getMetaMemberPK().getMemberCod());
                pk.setCodeChr(code);

                m.setMetaDataPK(pk);
                m.setValueChr(value);
                m.setMapMark(mark);
                m = persistMetaData(m);
            }
            em.flush();
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

    // SELECT m FROM MetaData m WHERE m.metaDataPK.codClient = :codClient AND
    // m.metaDataPK.codMeta = :codMeta AND m.metaDataPK.codMember = :codMember

    public MetaData getLastMetadataClientMetaMember(Long codClient, Long codMeta, Long codMember) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery(" SELECT m FROM MetaData m left join fetch m.mapMark "
                + " WHERE m.metaDataPK.codClient = :codClient "
                + " AND m.metaDataPK.codMeta = :codMeta "
                + " AND m.metaDataPK.codMember = :codMember "
                + " ORDER BY to_number(DECODE(replace(translate(m.metaDataPK.codeChr,'-.,1234567890','#############'),'#'),NULL,m.metaDataPK.codeChr,'1')) DESC ");

            q.setParameter("codClient", codClient);
            q.setParameter("codMeta", codMeta);
            q.setParameter("codMember", codMember);
            q.setMaxResults(1);
            MetaData result = (MetaData) q.getSingleResult();

            return result;
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

    public List<MetaData> findByUserphone(Long codMeta, Userphone u) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery("SELECT md FROM MetaData md, IN (md.userphones) u "
                + "WHERE md.metaDataPK.codMeta = :codMeta "
                + "AND u.userphoneCod = :userphoneCod  ");

            q.setParameter("codMeta", codMeta);
            q.setParameter("userphoneCod", u.getUserphoneCod());

            return q.getResultList();
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

    public String deleteUserphoneMetadataRelation(Long codClient, Long codMeta) throws Exception {
        try {
            List<MetaData> list = getFacadeContainer().getMetaDataAPI().findByClientMeta(
                    codClient, codMeta);
            if (list != null) {
                for (MetaData md : list) {
                    if (md.getUserphones() != null) {
                        for (Userphone u : md.getUserphones()) {
                            u.getMetaData().remove(md);
                            getFacadeContainer().getUserphoneAPI().edit(u);
                        }
                    }
                    md.setUserphones(null);
                    getFacadeContainer().getMetaDataAPI().edit(md);
                }
            }
        } catch (Exception e) {
            getFacadeContainer().getNotifier().signal(getClass(), Action.ERROR,
                    e.getMessage(), e);
        }

        return null;
    }

    public void processUserphoneMeta(Userweb userweb, MetaData md, HashMap<MetaDataPK, List<Long>> mdUserphone) throws ConstraintException, GenericFacadeException, ConstraintViolationException, MassivePersistenceException {

        try {
            /* Recuperamos el metadata de la BD y su lista de Userphones */
            MetaData persistedMeta = getFacadeContainer().getMetaDataAPI().find(
                    md.getMetaDataPK());

            if (persistedMeta == null) {
                create(md);
                persistedMeta = getFacadeContainer().getMetaDataAPI().find(
                        md.getMetaDataPK());
            } else {
                getFacadeContainer().getNotifier().signal(
                        getClass(),
                        Action.WARNING,
                        MessageFormat.format(
                                getFacadeContainer().getI18nAPI().iValue(
                                        "ejb.client.metaDatafacade.duplicate.meta"),
                                md.getMetaDataPK().getCodClient().toString(),
                                md.getMetaDataPK().getCodMeta().toString(),
                                md.getMetaDataPK().getCodMember().toString(),
                                md.getMetaDataPK().getCodeChr()));
            }

            /* Se recupera del hashmap el valor que concuerta con el MetadataPK */
            if (mdUserphone.get(md.getMetaDataPK()) != null) {

                /* Se convierte el valor a un tipo Long */
                for (Long u : mdUserphone.get(md.getMetaDataPK())) {
                    /*
                     * Recuperamos el userphone que concuerda con el codigo
                     * ingresado y que pertenezca al cliente
                     */
                    Userphone userphone = null;
                    try {
                        // Long userphoneCod = new Long(u);
                        userphone = getFacadeContainer().getUserphoneAPI().getUserphoneByClient(
                                userweb, md.getMetaDataPK().getCodClient(), u);
                    } catch (Exception e) {
                        getFacadeContainer().getNotifier().error(
                                MetaDataAPI.class, null, e.getMessage(), e);
                        throw new MassivePersistenceException(MessageFormat.format(
                                getFacadeContainer().getI18nAPI().iValue(
                                        "ejb.client.metaDatafacade.exception.MassProcessCreateClientMetaData"),
                                md.getMetaDataPK().getCodClient().toString()), md.getMetaDataPK().getCodeChr());
                    }

                    /*
                     * En caso que sea null significa que no existe y se muestra
                     * el mensaje correspondiente
                     */
                    if (userphone == null) {
                        // TODO: notificar userphone no existemte
                        // setUserphoneNotExist(true);
                        // if
                        // (!getListUserphoneNotExist().contains(u.toString()))
                        // {
                        // getListUserphoneNotExist().add(u.toString());
                        // }

                    } else {
                        /*
                         * Recuperamos la lista de metadatas asociados al
                         * userphone
                         */
                        List<MetaData> listMetadata = getFacadeContainer().getUserphoneAPI().find(
                                userphone.getUserphoneCod(), "metaData").getMetaData();

                        /*
                         * Si es nulo, es decir el userphone no esta asociado a
                         * ningun meta, creamos una lista
                         */
                        if (listMetadata == null) {
                            listMetadata = new ArrayList<MetaData>();
                        }

                        /* Si en la lista ya no existe el metadata, agregamos */
                        if (!listMetadata.contains(persistedMeta)) {
                            listMetadata.add(persistedMeta);
                        }

                        userphone.setMetaData(listMetadata);
                        getFacadeContainer().getUserphoneAPI().edit(userphone);

                        if (persistedMeta.getUserphones() == null) {
                            List<Userphone> list = new ArrayList<Userphone>();
                            list.add(userphone);
                            persistedMeta.setUserphones(list);
                        } else {
                            persistedMeta.getUserphones().add(userphone);
                        }

                        edit(persistedMeta);
                    }
                }
            }

        } catch (ConstraintViolationException e) {
            throw new ConstraintViolationException(e.getMessage(), null, null);

        } catch (Exception e) {
            throw new MassivePersistenceException(MessageFormat.format(
                    getFacadeContainer().getI18nAPI().iValue(
                            "ejb.client.metaDatafacade.exception.MassProcessCreateClientMetaData"),
                    md.getMetaDataPK().getCodClient().toString()), md.getMetaDataPK().getCodeChr());

        }

    }

    public void processUserphoneMeta(MetaData md, Long userphoneCod) throws ConstraintException, GenericFacadeException, ConstraintViolationException, MassivePersistenceException {

        try {

            /*
             * Recuperamos el userphone que concuerda con el codigo ingresado y
             * que pertenezca al cliente
             */
            Userphone userphone = null;
            try {
                // Long userphoneCod = new Long(u);
                userphone = getFacadeContainer().getUserphoneAPI().getUserphoneByClient(
                        md.getMetaDataPK().getCodClient(), userphoneCod);
            } catch (Exception e) {
                getFacadeContainer().getNotifier().error(MetaDataAPI.class,
                        null, e.getMessage(), e);
                throw new MassivePersistenceException(MessageFormat.format(
                        getFacadeContainer().getI18nAPI().iValue(
                                "ejb.client.metaDatafacade.exception.MassProcessCreateClientMetaData"),
                        md.getMetaDataPK().getCodClient().toString()), md.getMetaDataPK().getCodeChr());
            }

            /* Recuperamos el metadata de la BD y su lista de Userphones */
            MetaData persistedMeta = getFacadeContainer().getMetaDataAPI().find(
                    md.getMetaDataPK());

            if (persistedMeta == null) {
                create(md);
                persistedMeta = getFacadeContainer().getMetaDataAPI().find(
                        md.getMetaDataPK());
            }

            /* Se recupera del hashmap el valor que concuerta con el MetadataPK */

            /*
             * En caso que sea null significa que no existe y se muestra el
             * mensaje correspondiente
             */
            if (userphone == null) {
                // TODO: notificar uerphone no asignado

            } else {
                /*
                 * Recuperamos la lista de metadatas asociados al userphone
                 */
                List<MetaData> listMetadata = getFacadeContainer().getUserphoneAPI().find(
                        userphone.getUserphoneCod(), "metaData").getMetaData();

                /*
                 * Si es nulo, es decir el userphone no esta asociado a ningun
                 * meta, creamos una lista
                 */
                if (listMetadata == null) {
                    listMetadata = new ArrayList<MetaData>();
                }

                /* Si en la lista ya no existe el metadata, agregamos */
                if (!listMetadata.contains(persistedMeta)) {
                    listMetadata.add(persistedMeta);
                }

                userphone.setMetaData(listMetadata);
                getFacadeContainer().getUserphoneAPI().edit(userphone);

                if (persistedMeta.getUserphones() == null) {
                    List<Userphone> list = new ArrayList<Userphone>();
                    list.add(userphone);
                    persistedMeta.setUserphones(list);
                } else {
                    persistedMeta.getUserphones().add(userphone);
                }

                edit(persistedMeta);
            }

        } catch (ConstraintViolationException e) {
            throw new ConstraintViolationException(e.getMessage(), null, null);

        } catch (Exception e) {
            throw new MassivePersistenceException(MessageFormat.format(
                    getFacadeContainer().getI18nAPI().iValue(
                            "ejb.client.metaDatafacade.exception.MassProcessCreateClientMetaData"),
                    md.getMetaDataPK().getCodClient().toString()), md.getMetaDataPK().getCodeChr());

        }

    }

    public List<MetaData> findByClientMetaMemberEnabled(Long codClient, Long codMeta, Long codMember1, Long codMember2, String value) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            String sql = "SELECT md FROM MetaData md WHERE md.metaDataPK.codClient = :codClient "
                + " AND md.metaDataPK.codMeta = :codMeta "
                + " AND md.metaDataPK.codMember = :codMember1 "
                + " AND (SELECT lower(m.valueChr) FROM MetaData m WHERE m.metaDataPK.codClient = :codClient "
                + " AND m.metaDataPK.codMeta = :codMeta AND m.metaDataPK.codMember = :codMember2"
                + " AND m.metaDataPK.codeChr like md.metaDataPK.codeChr) like :value ";
            Query query = em.createQuery(sql);
            query.setParameter("codClient", codClient);
            query.setParameter("codMeta", codMeta);
            query.setParameter("codMember1", codMember1);
            query.setParameter("codMember2", codMember2);
            query.setParameter("value", value.toLowerCase());
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
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

    public List<MetadataCrudService> findMetadataGeoLocation(Long codClient, Integer codMeta, Integer codMember, Double latitude, Double longitude) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            String where = "SELECT c.map_mark_cod,"
                + " c.latitude_num, c.longitude_num, c.cod_meta, c.cod_member,"
                + " c.code_chr, c.value_chr,    6371*c.c * 1000 as metros"
                + " FROM ( SELECT a.map_mark_cod,a.LATITUDE_NUM as LATITUDE_NUM,"
                + " a.LONGITUDE_NUM  as LONGITUDE_NUM,"
                + " a.COD_META as COD_META,"
                + " a.COD_MEMBER  as COD_MEMBER,"
                + " a.CODE_CHR as CODE_CHR,"
                + " a.VALUE_CHR as VALUE_CHR,"
                + " (2*atan2(sqrt(a.a),sqrt(1-a.a))) AS c"
                + " FROM ( SELECT r.MAP_MARK_COD,r.LATITUDE_NUM as LATITUDE_NUM,"
                + " r.LONGITUDE_NUM  as LONGITUDE_NUM,"
                + " r.COD_META   as COD_META, r.COD_MEMBER   as COD_MEMBER,"
                + " r.CODE_CHR   as CODE_CHR, r.VALUE_CHR  as VALUE_CHR,"
                + " (sin(r.dlat/2)*(sin(r.dlat/2))+sin(r.dlon/2)*sin(r.dlon/2)*cos(r.lat1)*cos(r.lat2)) AS a"
                + " FROM ( SELECT  m.MAP_MARK_COD            as MAP_MARK_COD,"
                + " m.LATITUDE_NUM                           as LATITUDE_NUM, "
                + " m.LONGITUDE_NUM                          as LONGITUDE_NUM,"
                + " md.COD_META                              as COD_META,"
                + " md.COD_MEMBER                            as COD_MEMBER,"
                + " md.CODE_CHR                              as CODE_CHR,"
                + " md.VALUE_CHR                             as VALUE_CHR,"
                + " (ll_lat1 - (m.LATITUDE_NUM))* p.pi /180  AS dLat ,"
                + " (ll_lon1 - (m.LONGITUDE_NUM))* p.pi /180 AS dLon ,"
                + " (m.LATITUDE_NUM)* p.pi /180              AS lat1 ,"
                + " (ll_lat1 )* p.pi /180                    AS lat2"
                + " FROM  MAP_MARK m INNER JOIN META_DATA md ON"
                + " md.COD_MAP_MARK = m.MAP_MARK_COD, (SELECT 3.1415926535897932384626433832795028841971693993751058209749445923078164062862089986280348253421170679"
                + " AS pi FROM dual ) p, (SELECT {0,number,#.000000} AS ll_lat1, {1,number,#.000000} AS ll_lon1 FROM dual )ll WHERE md.COD_CLIENT ={2} AND md.COD_META = {3} "
                + " AND md.COD_MEMBER = {4}) r ) a)c where (6371*c.c * 1000) <= {5} ORDER BY 1 ASC";

            StringBuffer sb = new StringBuffer();

            new MessageFormat(where, Locale.US).format(
                    new Object[] { latitude, longitude, codClient, codMeta, codMember, getFacadeContainer().getGlobalParameterAPI().findByCode(
                            "metadata.geolocalize.precision") }, sb,
                    new FieldPosition(0));

            String sql = MessageFormat.format(
                    where,
                    latitude,
                    longitude,
                    codClient,
                    codMeta,
                    codMember,
                    getFacadeContainer().getGlobalParameterAPI().findByCode(
                            "metadata.geolocalize.precision"));
            Query q = em.createNativeQuery(sb.toString());
            List<Object[]> resultList = q.getResultList();

            List<MetadataCrudService> list = new ArrayList<MetadataCrudService>();
            for (Object[] obj : resultList) {
                MetadataCrudService mcs = new MetadataCrudService();
                mcs.setMapMarkCod(((BigDecimal) obj[0]).longValue());
                mcs.setLatitude(((BigDecimal) obj[1]).doubleValue());
                mcs.setLongitude(((BigDecimal) obj[2]).doubleValue());
                mcs.setMetaCode(((BigDecimal) obj[3]).intValue());
                mcs.setCode((String) obj[5]);
                mcs.setValue((String) obj[6]);
                list.add(mcs);
            }

            if (list.size() == 1) {
                list.add(new MetadataCrudService());
            }

            return list;

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

    public MetaData getMetaDataByMapMark(Long mapMarkCode) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            String sql = "SELECT md FROM MetaData md WHERE md.mapMark.mapMarkCod = :mapMarkCode ";
            Query query = em.createQuery(sql);
            query.setParameter("mapMarkCode", mapMarkCode);
            return (MetaData) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
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

    public List<MetaData> findWithMetadatas(MetaDataPK pk, Long codMeta) {

        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            String sql = "SELECT ms from MetaData md , in (md.metadatas) ms "
                + "WHERE md.metaDataPK = :pk "
                + "AND ms.metaDataPK.codMeta = :codMeta ";
            Query query = em.createQuery(sql);
            query.setParameter("pk", pk);
            query.setParameter("codMeta", codMeta);
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
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

    public List<MetaData> findWithMetadatasViceversa(MetaDataPK pk, Long codMeta) {

        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            String sql = "SELECT ms from MetaData md , in (md.metaData) ms "
                + "WHERE md.metaDataPK = :pk "
                + "AND ms.metaDataPK.codMeta = :codMeta ";
            Query query = em.createQuery(sql);
            query.setParameter("pk", pk);
            query.setParameter("codMeta", codMeta);
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
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

    public Long getMetadaDataSupervisorSeq() {

        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createNativeQuery("SELECT METADATA_SUPERVIDOR_SEQ.nextval FROM DUAL");
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

    // @Asynchronous
    // public void deleteEntitiesMass(Long codClient, Long codMeta,String
    // cellphoneNumber,Application application) {
    // try {
    //
    // } catch (Exception e) {
    // int j = 0;
    // class MetaDataDelete extends Thread {
    //
    // private Long codClient;
    // private Long codMeta;
    // private boolean lastThread;
    // private String cellphoneNum;
    // private Application application;
    // private int cantidadFilas;
    // private boolean running = false;
    // private List<MetaData> metaDataList;
    //
    // public MetaDataDelete(int id) {
    // super("MetaDataCreator-" + id);
    //
    // }
    //
    // public List<MetaData> getMetaDataList() {
    // if (metaDataList == null) {
    // metaDataList = new ArrayList<MetaData>();
    // }
    // return metaDataList;
    // }
    //
    // public Long getCodClient() {
    // return codClient;
    // }
    //
    // public void setCodClient(Long codClient) {
    // this.codClient = codClient;
    // }
    //
    // public Long getCodMeta() {
    // return codMeta;
    // }
    //
    // public void setCodMeta(Long codMeta) {
    // this.codMeta = codMeta;
    // }
    //
    // public boolean getLastThread() {
    // return lastThread;
    // }
    //
    // public void setLastThread(boolean lastThread) {
    // this.lastThread = lastThread;
    // }
    //
    // public void setCellphoneNum(String cellphoneNum) {
    // this.cellphoneNum = cellphoneNum;
    // }
    //
    // public void setApplication(Application application) {
    // this.application = application;
    // }
    //
    // public void setCantidadFilas(int cantidadFilas) {
    // this.cantidadFilas = cantidadFilas;
    // }
    //
    // @Override
    // public void run() {
    //
    // if (running) {
    // return;
    // }
    // running = true;
    // getFacadeContainer().getNotifier().info(getClass(), null,
    // "Coriendo proceso: " + this.getName());
    // if (metaDataList != null) {
    // for (final MetaData jmsBean : metaDataList) {
    //
    // try {
    // getFacadeContainer().getMetaDataAPI().processUserphoneMeta(
    // jmsBean.getMetaData(),
    // jmsBean.getUserphoneCode());
    // } catch (ConstraintViolationException e) {
    // getFacadeContainer().getNotifier().error(
    // getClass(), null, e.getMessage(), e);
    // } catch (ConstraintException e) {
    // getFacadeContainer().getNotifier().error(
    // getClass(), null, e.getMessage(), e);
    // } catch (GenericFacadeException e) {
    // getFacadeContainer().getNotifier().error(
    // getClass(), null, e.getMessage(), e);
    // } catch (MassivePersistenceException e) {
    // getFacadeContainer().getNotifier().error(
    // getClass(), null, e.getMessage(), e);
    // }
    //
    // }
    //
    // }
    // if (getLastThread()) {
    //
    // getFacadeContainer().getSmsQueueAPI().sendToJMS(
    // cellphoneNum,
    // application,
    // MessageFormat.format(
    // "El archivo se ha procesado exitosamente. Se procesaron {0} registros.",
    // cantidadFilas));
    // }
    //
    // }
    //
    // }
    //
    // MetaDataDelete currentMetaDataDelete = new MetaDataDelete(j);
    //
    // currentMetaDataDelete.setLastThread(true);
    // currentMetaDataDelete.setCellphoneNum(cellphoneNumber);
    // currentMetaDataDelete.setApplication(application);
    //
    // MetaDataDelete prevoiusMetaDataCreator = null;
    // }
    // }

}
