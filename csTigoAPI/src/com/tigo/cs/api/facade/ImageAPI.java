package com.tigo.cs.api.facade;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.tigo.cs.domain.Image;

public abstract class ImageAPI extends AbstractAPI<Image> {

    public ImageAPI() {
        super(Image.class);
    }

    public String saveEditing(Image entity) {
        Object obj = entity.getImageCod();
        if (obj == null) {
            create(entity);
        } else {
            edit(entity);
        }

        return null;
    }

    public List<Image> getImagesByNewCod(Long newCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("" + "SELECT images " + "FROM New n, "
                + "IN (n.images) images " + "WHERE n.newCod = :newCod");
            query.setParameter("newCod", newCod);
            return query.getResultList();
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public void deleteEntity(Image entity) throws Exception {
        this.remove(entity);
    }
}
