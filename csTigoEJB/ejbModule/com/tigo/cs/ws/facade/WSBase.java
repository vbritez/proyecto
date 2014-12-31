/**
 * 
 */
package com.tigo.cs.ws.facade;

import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.tigo.cs.api.facade.ServiceValueAPI;
import com.tigo.cs.commons.util.Cryptographer;
import com.tigo.cs.domain.FeatureValue;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;
import com.tigo.cs.domain.Userweb;

public abstract class WSBase extends ServiceValueAPI {

    private static Map<Userweb, Map<String, Date>> userLastAccess = new HashMap<Userweb, Map<String, Date>>();

    public WSBase() {

    }

    protected void validateAccess(long id, Userweb userweb, String method, Boolean fromServiceValue) throws AuthenticationException {

        Date currentAcces = new Date();
        Long restrictionTime;
        Integer restrictionDays;
        try {

            String clientParameterValue = getFacadeContainer().getClientParameterValueAPI().findByCode(
                    userweb.getClient().getClientCod(),
                    "ws.dataimport.limit.time.restriction");
            if (clientParameterValue != null) {
                restrictionTime = Long.parseLong(clientParameterValue);
            } else {

                restrictionTime = Long.parseLong(getFacadeContainer().getGlobalParameterAPI().findByCode(
                        "ws.dataimport.limit.time.restriction"));
            }
        } catch (Exception e) {
            restrictionTime = 2 * 60 * 1000L;
        }

        Map<String, Date> lastAccesMethod = userLastAccess.get(userweb);
        if (lastAccesMethod == null) {
            lastAccesMethod = new HashMap<String, Date>();
            userLastAccess.put(userweb, lastAccesMethod);
        }
        Date lastAccess = lastAccesMethod.get(method);
        if (lastAccess != null) {

            long delta = currentAcces.getTime() - lastAccess.getTime();
            if (delta < restrictionTime) {

                String message = MessageFormat.format(
                        getFacadeContainer().getI18nAPI().iValue(
                                "web.client.login.message.LastAccessRestriction"),
                        restrictionTime, restrictionTime - delta);

                throw new AuthenticationException(message);
            }
        }
        lastAccesMethod.put(method, currentAcces);

        /*
         * Se agrega una nueva validacion en la importacion de datos del
         * cliente. El cliente solo podra acceder a datos de hasta una cantidad
         * de dias configurada en la base de datos
         */

        try {

            /*
             * obtenemos la configuracion del cliente sobre los dias de ma
             */

            String clientParameterValue = getFacadeContainer().getClientParameterValueAPI().findByCode(
                    userweb.getClient().getClientCod(),
                    "ws.dataimport.limit.days.restriction");
            if (clientParameterValue != null) {
                restrictionDays = Integer.parseInt(clientParameterValue);
            } else {

                restrictionDays = Integer.parseInt(getFacadeContainer().getGlobalParameterAPI().findByCode(
                        "ws.dataimport.limit.days.restriction"));
            }
        } catch (Exception e) {
            restrictionDays = 30;
        }

        /*
         * realizamos la validacion obteniendo el primer registro que sea menor
         * o igual id enviado
         */
        if (id <= 0) {
            id = 1;
        }
        String jpql = null;
        Date dateFrom = null;
        if (fromServiceValue == null) {
            jpql = "SELECT fv FROM FeatureValue fv WHERE fv.featureValueCod >= :id ORDER BY fv.featureValueCod";
        } else if (fromServiceValue) {
            jpql = "SELECT sv FROM ServiceValue sv WHERE sv.servicevalueCod >= :id ORDER BY sv.servicevalueCod";
        } else {
            jpql = "SELECT svd FROM ServiceValueDetail svd WHERE svd.servicevaluedetailCod >= :id ORDER BY svd.servicevaluedetailCod";
        }

        EntityManager em = getFacadeContainer().getEntityManager();
        Query query = em.createQuery(jpql);
        query.setParameter("id", id);
        query.setMaxResults(1);
        try {

            if (fromServiceValue == null) {
                dateFrom = ((FeatureValue) query.getSingleResult()).getMessage().getDateinDat();
            } else if (fromServiceValue) {
                dateFrom = ((ServiceValue) query.getSingleResult()).getRecorddateDat();
            } else {
                dateFrom = ((ServiceValueDetail) query.getSingleResult()).getRecorddateDat();
            }

        } catch (NoResultException e) {

        }

        if (dateFrom != null) {
            /*
             * 
             * validamos que la fecha del servicevalue a procesar no sea menor a
             * los dias configurados como limite de recuperacion de datos
             */

            Calendar svCalendar = Calendar.getInstance();
            svCalendar.setTime(dateFrom);
            svCalendar.set(Calendar.HOUR_OF_DAY, 0);
            svCalendar.set(Calendar.MINUTE, 0);
            svCalendar.set(Calendar.SECOND, 0);
            svCalendar.set(Calendar.MILLISECOND, 0);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            calendar.add(Calendar.DAY_OF_MONTH, -restrictionDays);

            if (svCalendar.getTime().getTime() < calendar.getTime().getTime()) {
                /*
                 * como la fecha del service value recuperado no es valido ya
                 * que esta antes de los dias configurados como maximos para
                 * recuperar informacion imprimimos un log y rechazamos la
                 * recuperacion con un mensaje de erro para el usuario
                 */

                String message = MessageFormat.format(
                        getFacadeContainer().getI18nAPI().iValue(
                                "web.client.login.message.MaxDaysWSRestriction"),
                        restrictionDays);

                throw new AuthenticationException(message);
            }

        }

    }

    /**
     * Valida un usuario web y su contrase�a.
     * 
     * @return Userweb en caso de que las credenciales sean correctas.
     * @param userName
     *            nombre del usuario.
     * @param pwd
     *            contrase�a del usuario.
     * @throws AuthenticationException
     *             En caso de que las credenciales no sean correctas.
     * */
    protected Userweb validateUser(String userName, String pwd) throws AuthenticationException {
        Userweb userweb = null;
        try {
            try {
                userweb = getFacadeContainer().getUserwebAPI().getUserwebByUsername(
                        userName);
            } catch (Exception e) {
                throw new AuthenticationException(getFacadeContainer().getI18nAPI().iValue(
                        "web.client.login.message.UserAndPasswordDoesntMatch"));
            }

            if (!userweb.getPasswordChr().equals(Cryptographer.sha256Doble(pwd))) {

                /*
                 * validamos si la contraseña del usuario es una contraseña
                 * temporal
                 */

                if (userweb.getChangepasswChr()) {
                    throw new AuthenticationException(getFacadeContainer().getI18nAPI().iValue(
                            "web.client.login.message.MustChangePassword"));
                }
                /*
                 * Obtenemos la cantidad maxima de intentos de conexion
                 */
                Integer retryLimit = Integer.parseInt(getFacadeContainer().getGlobalParameterAPI().findByCode(
                        "ws.userweb.loginRetryLimit"));

                Integer wrongpwd = userweb.getPwdRetryCount() == null ? 0 : userweb.getPwdRetryCount().intValue();
                wrongpwd++; // suma del actual intento fallido
                if (wrongpwd.intValue() >= retryLimit) {
                    userweb.setEnabledChr(false);
                    userweb.setPwdRetryCount(new BigInteger(wrongpwd.toString()));
                    getFacadeContainer().getUserwebAPI().edit(userweb);
                    if (wrongpwd.intValue() == retryLimit) {
                        throw new AuthenticationException(getFacadeContainer().getI18nAPI().iValue(
                                "web.client.login.message.UserBlockedByFailedAttemps"));
                    }
                } else {
                    userweb.setPwdRetryCount(new BigInteger(wrongpwd.toString()));
                    getFacadeContainer().getUserwebAPI().edit(userweb);
                }
                if (userweb.getEnabledChr()) {
                    throw new AuthenticationException(getFacadeContainer().getI18nAPI().iValue(
                            "web.client.login.message.UserAndPasswordDoesntMatch"));
                } else {
                    throw new AuthenticationException(getFacadeContainer().getI18nAPI().iValue(
                            "web.client.login.message.UserDisabled"));
                }
            }

            if (!userweb.getEnabledChr()
                || !userweb.getClient().getEnabledChr()) {
                throw new AuthenticationException(getFacadeContainer().getI18nAPI().iValue(
                        "web.client.login.message.UserDisabled"));
            }
        } catch (Exception e) {
            if (e instanceof AuthenticationException) {
                throw (AuthenticationException) e;
            }
        }
        return userweb;
    }
}
