package com.tigo.cs.api.facade;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.tigo.cs.domain.MenuMovilPeriod;
import com.tigo.cs.domain.MenuMovilUser;

public abstract class MenuMovilPeriodAPI extends AbstractAPI<MenuMovilPeriod> implements Serializable {

    protected MenuMovilPeriodAPI() {
        super(MenuMovilPeriod.class);
    }

    /*
     * Recuperamos el registro MenuMovilPeriod que este habilitado
     */
    public synchronized MenuMovilPeriod getMenuMovilPeriodByPeriod(Date periodo, MenuMovilUser menuMovilUser) {
        EntityManager em = null;
        DateFormat sqlSdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("select mp from MenuMovilPeriod mp where mp.enabledChr = true and mp.menuMovilUser.menuMovilUserCod = :menuMovilUserCod");
            query.setParameter("menuMovilUserCod",
                    menuMovilUser.getMenuMovilUserCod());
            query.setMaxResults(1);
            MenuMovilPeriod mp = (MenuMovilPeriod) query.getSingleResult();

            if (mp == null) {
                return null;
            } else {

                if (periodo.after(mp.getMenuMovilUser().getExpirationDate())) {
                    return new MenuMovilPeriod();
                }

                Query query2 = em.createQuery("select mp from MenuMovilPeriod mp where mp.enabledChr = true and mp.menuMovilUser.menuMovilUserCod = :menuMovilUserCod"
                        + " and :periodo BETWEEN mp.startDatePeriod and mp.endDatePeriod");
                query2.setParameter("menuMovilUserCod",
                        menuMovilUser.getMenuMovilUserCod());
                query2.setParameter("periodo", periodo);
                query2.setMaxResults(1);
                MenuMovilPeriod mp2 = null;
                try{
                    mp2 = (MenuMovilPeriod) query2.getSingleResult();
                }catch(Exception e) {

                }


                if (mp2 == null) {
                    MenuMovilPeriod menuMovil = new MenuMovilPeriod();
                    Calendar fechaInicio = Calendar.getInstance();
                    fechaInicio.setTime(mp.getStartDatePeriod());
                    fechaInicio.add(Calendar.MONTH, 1);
                    menuMovil.setStartDatePeriod(fechaInicio.getTime());
                    fechaInicio.setTime(mp.getEndDatePeriod());
                    fechaInicio.add(Calendar.MONTH, 1);
                    menuMovil.setEndDatePeriod(fechaInicio.getTime());
                    menuMovil.setQuotaTotal(mp.getQuotaTotal());
                    menuMovil.setQuotaUsed(0L);
                    menuMovil.setEnabledChr(true);
                    menuMovil.setMenuMovilUser(menuMovilUser);
                    mp.setEnabledChr(false);
                    edit(mp);
                    return create(menuMovil);
                } else {
                    return mp2;
                }
            }


        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(MenuMovilPeriodAPI.class,
                    null, e.getMessage(), e);
            em.getTransaction().rollback();
            return null;
        } finally {
            if (em != null
                    && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

}
