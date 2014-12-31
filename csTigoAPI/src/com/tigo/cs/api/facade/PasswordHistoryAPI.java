package com.tigo.cs.api.facade;

import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.jpa.MoreThanOneResultException;
import com.tigo.cs.domain.PasswordHistory;
import com.tigo.cs.domain.Useradmin;

public abstract class PasswordHistoryAPI extends AbstractAPI<PasswordHistory> {

    public PasswordHistoryAPI() {
        super(PasswordHistory.class);
    }

    public PasswordHistory findByUseradminAndPasswordChr(Long useradminCod, String password) throws MoreThanOneResultException, GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            Query query = em.createNamedQuery("PasswordHistory.findByUseradminAndPasswordChr");
            query.setParameter("passwordChr", password);
            query.setParameter("useradminCod", useradminCod);
            return (PasswordHistory) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException e) {
            throw new MoreThanOneResultException(e);
        } catch (Exception e) {
            throw new GenericFacadeException(getClass());
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<PasswordHistory> findByUseradmin(Long useradminCod) {
        return findListWithNamedQuery("PasswordHistory.findByUseradmin",
                addSingleParam("useradminCod", useradminCod));
    }

    /**
     * Valida que el password del usuario administrativo no coincidan con los
     * últimas N contraseñas ingresadas anteriormente.
     * 
     * @param useradmin
     *            el usuario administrativo a ser validado.
     * @param password
     *            la contraseña encriptada del usuario para ser validada.
     * @return true si la contraseña no está en la lista de las últimas N
     *         ingresadas anteriormente, false en caso contrario.
     */
    public boolean validatePasswordHistory(Useradmin useradmin, String password) throws GenericFacadeException {
        try {
            // En primer lugar, se verifica que no haya coincidencia en ninguna
            // circunstancia
            // con las contraseñas anteriores.
            PasswordHistory ph = findByUseradminAndPasswordChr(
                    useradmin.getUseradminCod(), password);
            if (ph != null) {
                // Se encontró una ocurrencia
                return false;
            } else {
                ph = new PasswordHistory();
                ph.setPasswordChr(password);
                ph.setUseradmin(useradmin);
                ph.setCreateddate(Calendar.getInstance().getTime());
                super.create(ph);
            }

            // Aquí se mantiene el historial de contraseñas.
            int maxPwdHistory = Integer.parseInt(getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "passwordhistory.maxrows"));
            // Se validan n cantidad de registros en el historial, si existen
            // más de los parámetrizados
            // estos registros se eliminan en orden a su tiempo de vida, los más
            // nuevos permanecen
            List<PasswordHistory> phs = findByUseradmin(useradmin.getUseradminCod());
            for (int i = maxPwdHistory; i < phs.size(); i++) {
                PasswordHistory passwordHistory = phs.get(i);
                super.remove(passwordHistory);
            }

        } catch (MoreThanOneResultException ex) {
            return false;
        } catch (GenericFacadeException ex) {
            throw ex;
        }

        return true;
    }
}
