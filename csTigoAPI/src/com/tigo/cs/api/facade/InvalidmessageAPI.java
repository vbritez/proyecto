package com.tigo.cs.api.facade;

import javax.persistence.EntityManager;

import com.tigo.cs.domain.Cell;
import com.tigo.cs.domain.Invalidmessage;

public abstract class InvalidmessageAPI extends AbstractAPI<Invalidmessage>  {

    public InvalidmessageAPI() {
        super(Invalidmessage.class);
    }
    
    public Invalidmessage create(Invalidmessage invalidmessage) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Cell cell = invalidmessage.getCell();
            if (cell != null) {
                cell = em.getReference(cell.getClass(), cell.getCellCod());
                invalidmessage.setCell(cell);
            }
            em.persist(invalidmessage);
            if (cell != null) {
                cell.getInvalidmessageCollection().add(invalidmessage);
                cell = em.merge(cell);
            }
            return invalidmessage;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }
}
