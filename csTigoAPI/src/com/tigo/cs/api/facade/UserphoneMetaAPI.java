package com.tigo.cs.api.facade;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.tigo.cs.domain.Meta;
import com.tigo.cs.domain.Userphone;
import com.tigo.cs.domain.UserphoneMeta;

public abstract class UserphoneMetaAPI extends AbstractAPI<UserphoneMeta> {

    public UserphoneMetaAPI() {
        super(UserphoneMeta.class);
    }

    public UserphoneMeta getUserphoneMeta(Meta meta, Userphone userphone) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT DISTINCT um FROM UserphoneMeta um "
                + " WHERE um.userphone.userphoneCod = :userphoneCod"
                + " AND um.meta.metaCod = :metaCod");

            query.setParameter("userphoneCod", userphone.getUserphoneCod());
            query.setParameter("metaCod", meta.getMetaCod());
            UserphoneMeta um = (UserphoneMeta) query.getSingleResult();
            return um;
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
    public UserphoneMeta edit(UserphoneMeta entity) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            if (getFacadeContainer().isEntityManagerTransactional()) {
                em.getTransaction().begin();
            }
            UserphoneMeta userphoneMetaManaged = find(entity.getId());
            userphoneMetaManaged.setCreateChr(entity.getCreateChr());
            userphoneMetaManaged.setDeleteChr(entity.getDeleteChr());
            userphoneMetaManaged.setUpdateChr(entity.getUpdateChr());
            userphoneMetaManaged.setMeta(entity.getMeta());
            userphoneMetaManaged.setUserphone(entity.getUserphone());

            em.merge(userphoneMetaManaged);
            if (getFacadeContainer().isEntityManagerTransactional()) {
                em.getTransaction().commit();
            }
            return userphoneMetaManaged;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(UseradminAPI.class, null,
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

    public List<Meta> getUserphoneMetaList(Userphone userphone, Boolean create, Boolean update, Boolean delete) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery(" SELECT um.meta FROM UserphoneMeta um "
                + " WHERE um.userphone.userphoneCod = :userphoneCod "
                + " AND um.createChr = :create ");

            query.setParameter("userphoneCod", userphone.getUserphoneCod());
            query.setParameter("create", create);
            List<Meta> um = query.getResultList();

            if (um != null && um.size() > 0) {
                List<Meta> clientMetaList = getFacadeContainer().getMetaAPI().findMetaByClientAndEnabled(
                        userphone.getClient().getClientCod(), true);
                List<Meta> list = new ArrayList<Meta>();

                if (clientMetaList != null && clientMetaList.size() > 0) {
                    for (Meta meta : um) {
                        if (clientMetaList.contains(meta))
                            list.add(meta);
                    }
                }
                return list;
            }
            return um;
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }
}
