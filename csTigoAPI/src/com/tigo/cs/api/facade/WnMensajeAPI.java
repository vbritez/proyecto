package com.tigo.cs.api.facade;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.tigo.cs.domain.WnMensaje;
import com.tigo.cs.domain.WnOperadora;

public class WnMensajeAPI extends AbstractAPI<WnMensaje> {

    public WnMensajeAPI() {
        super(WnMensaje.class);
    }

    public WnMensaje findByActiveWnOperadora(WnOperadora wnOperadora) {
        return findByWnOperadora(wnOperadora, true);
    }

    public WnMensaje findByInactiveWnOperadora(WnOperadora wnOperadora) {
        return findByWnOperadora(wnOperadora, false);
    }

    public WnMensaje findByWnOperadora(WnOperadora wnOperadora, Boolean active) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery(" SELECT m FROM WnMensaje m "
                + " WHERE    m.wnOperadora= :wnOperadora "
                + " AND      m.activo = :active ");
            q.setParameter("wnOperadora", wnOperadora);
            q.setParameter("active", active);
            return (WnMensaje) q.getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public Boolean switchState(WnOperadora wnOperadora) {
        try {
            WnMensaje activeMensaje = findByActiveWnOperadora(wnOperadora);
            WnMensaje inactiveMensaje = findByInactiveWnOperadora(wnOperadora);

            activeMensaje.setActivo(false);
            inactiveMensaje.setActivo(true);

            edit(activeMensaje);
            edit(inactiveMensaje);
            return true;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(
                    getClass(),
                    null,
                    "No se puedo hacer el intercambio de estados entre las plataformas",
                    e);
            return false;
        }
    }

    public Boolean setAll2Problem() {
        List<WnOperadora> list = getFacadeContainer().getWnOperadoraAPI().findAll();

        for (WnOperadora wnOperadora : list) {
            WnMensaje wnMensaje = findByActiveWnOperadora(wnOperadora);
            if (wnMensaje.getTipomsj().compareToIgnoreCase("S") == 0) {
                switchState(wnOperadora);
            }
        }
        return true;
    }

}
