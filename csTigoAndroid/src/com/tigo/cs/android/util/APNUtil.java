package com.tigo.cs.android.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.telephony.TelephonyManager;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.messagehistory.MessageHistoryActivity;
import com.tigo.cs.android.helper.domain.GlobalParameterEntity;

/**
 * Clase utilitaria que maneja las configuraciones de las APN configuradas en el
 * dispositivo
 * 
 * @author Miguel Maciel
 */
public class APNUtil {
    /*
     * Información sobre las APN's configuradas en el dispositico
     * com.android.providers.telephony.TelephonyProvider
     */
    private static final Uri APN_TABLE_URI = Uri.parse("content://telephony/carriers");
    /*
     * Informacion sobre la ubicación de la APN configurada por defecto
     */
    private static final Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");

    /**
     * 
     * @return id de la apn configurada por defecto, -1 si ocurrio un error
     */
    private static int getDefaultAPN() {
        Cursor c = CsTigoApplication.getContext().getContentResolver().query(
                PREFERRED_APN_URI, new String[] { "_id", "name" }, null, null,
                null);
        int id = -1;
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    id = c.getInt(c.getColumnIndex("_id"));
                }
            } catch (SQLException e) {
                return -1;
            }
            c.close();
        }

        return id;

    }

    /**
     * 
     * @param context
     *            - Activity desde donde se esta invocando el cambio de la APN
     *            por defecto
     * @param id
     *            - identificador de la APN a ser configurada por defecto
     * @return - si se realizo la actualizacion sin problemas
     */
    private static boolean setDefaultAPN(int id) {
        boolean res = false;
        ContentResolver resolver = CsTigoApplication.getContext().getContentResolver();
        ContentValues values = new ContentValues();

        // See /etc/apns-conf.xml. The TelephonyProvider uses this file to
        // provide
        // content://telephony/carriers/preferapn URI mapping
        values.put("apn_id", id);
        try {
            resolver.update(PREFERRED_APN_URI, values, null, null);
            Cursor c = resolver.query(PREFERRED_APN_URI,
                    new String[] { "name", "apn" }, "_id=" + id, null, null);
            if (c != null) {
                res = true;
                c.close();
            }
        } catch (SQLException e) {
            return false;
        }
        return res;
    }

    /**
     * 
     * @return id del configurado en la aplicación cstigo
     */
    private static int checkCstigoApplicationAPN() {
        int id = -1;
        String apnName = CsTigoApplication.getGlobalParameterHelper().getInternetApnName();
        Cursor c = CsTigoApplication.getContext().getContentResolver().query(
                APN_TABLE_URI, new String[] { "_id", "name" }, "name=?",
                new String[] { apnName }, null);
        if (c == null) {
            id = -1;
        } else {
            int record_cnt = c.getCount();
            if (record_cnt == 0) {
                id = -1;
            } else if (c.moveToFirst()) {
                if (c.getString(c.getColumnIndex("name")).equalsIgnoreCase(
                        apnName)) {
                    id = c.getInt(c.getColumnIndex("_id"));
                }
            }
            c.close();
        }
        return id;
    }

    /**
     * agregamos la apn configurada en la aplicacion
     * 
     * @return
     */
    private static int addCstigoApplicationAPN() {
        int id = -1;
        ContentResolver resolver = CsTigoApplication.getContext().getContentResolver();
        ContentValues values = new ContentValues();

        values.put(
                "name",
                CsTigoApplication.getGlobalParameterHelper().getInternetApnName());
        values.put(
                "apn",
                CsTigoApplication.getGlobalParameterHelper().getInternetApnValue());

        /*
         * The following three field values are for testing in Android emulator
         * only The APN setting page UI will ONLY display APNs whose 'numeric'
         * filed is TelephonyProperties.PROPERTY_SIM_OPERATOR_NUMERIC. On
         * Android emulator, this value is 310260, where 310 is mcc, and 260
         * mnc. With these field values, the newly added apn will appear in
         * system UI.
         */
        TelephonyManager tel = (TelephonyManager) CsTigoApplication.getContext().getSystemService(
                Context.TELEPHONY_SERVICE);
        String networkOperator = tel.getNetworkOperator();

        if (networkOperator != null) {
            values.put("mcc", networkOperator.substring(0, 3));
            values.put("mnc", networkOperator.substring(3));
            values.put("numeric", networkOperator);
        }

        Cursor c = null;
        try {
            Uri newRow = resolver.insert(APN_TABLE_URI, values);
            if (newRow != null) {
                c = resolver.query(newRow, null, null, null, null);

                // Obtain the apn id
                int idindex = c.getColumnIndex("_id");
                c.moveToFirst();
                id = c.getShort(idindex);
            }
        } catch (SQLException e) {
            return -1;
        }

        if (c != null) {
            c.close();
        }

        return id;
    }

    public static void initApplication() {
        try {
            if (CsTigoApplication.getGlobalParameterHelper().getInternetApnChange()) {
                /*
                 * verificar si se utilizara internet y si se realizara el
                 * cambio de apn
                 */

                /*
                 * Obtenemos los datos de las apns configuradas en la aplicacion
                 */
                GlobalParameterEntity cstigoAppAPN = CsTigoApplication.getGlobalParameterHelper().getInternetApnCstigoIdEntity();
                GlobalParameterEntity defaultAppAPN = CsTigoApplication.getGlobalParameterHelper().getInternetApnDefaultIdEntity();

                /*
                 * verificamos el parametro de id de apn de la aplicación
                 */
                int cstigoAPN = checkCstigoApplicationAPN();
                if (cstigoAPN != -1) {
                    cstigoAppAPN.setParameterValue(String.valueOf(cstigoAPN));
                } else {
                    cstigoAPN = addCstigoApplicationAPN();
                    cstigoAppAPN.setParameterValue(String.valueOf(cstigoAPN));
                    CsTigoApplication.getGlobalParameterHelper().update(
                            cstigoAppAPN);
                }

                /*
                 * obtenemos la apn configurada por defecto y almacenamos el id
                 * para restaurar al cerrar la applicacion
                 */
                int defaultAPN = getDefaultAPN();
                if (defaultAPN != -1 && cstigoAPN != defaultAPN) {
                    defaultAppAPN.setParameterValue(String.valueOf(defaultAPN));
                    CsTigoApplication.getGlobalParameterHelper().update(
                            defaultAppAPN);
                }

                /*
                 * configuramos el dispositivo para que utilize el apn
                 * configurado
                 */
                if (cstigoAPN != defaultAPN) {
                    setDefaultAPN(cstigoAPN);
                }
            }
        } catch (Exception e) {
            if (CsTigoApplication.notificatApn) {
                CsTigoApplication.vibrate(false);
                CsTigoApplication.showNotification(
                        CSTigoNotificationID.APN_SETTING,
                        CsTigoApplication.getContext().getString(
                                R.string.warning),
                        CsTigoApplication.getContext().getString(
                                R.string.apn_change_notification),
                        MessageHistoryActivity.class);
            }
        }
    }

    public static void finishApplication() {
        try {
            if (CsTigoApplication.getGlobalParameterHelper().getInternetApnChange()) {
                /*
                 * no podemos verificar cual fue la configuracion de APN previa,
                 * pero de igual manera debemos setear la APN por defecto
                 * almacenada en el telefono
                 */

                GlobalParameterEntity defaultAppAPN = CsTigoApplication.getGlobalParameterHelper().getInternetApnDefaultIdEntity();

                int defaultAPN = getDefaultAPN();
                if (defaultAppAPN.getParameterValue() != null
                    && Integer.parseInt(defaultAppAPN.getParameterValue()) != defaultAPN) {
                    if (setDefaultAPN(Integer.parseInt(defaultAppAPN.getParameterValue()))) {
                        // TODO: salio bien la actualizacion
                    }

                    defaultAppAPN.setParameterValue(String.valueOf(defaultAPN));
                    CsTigoApplication.getGlobalParameterHelper().update(
                            defaultAppAPN);
                }

            }
        } catch (Exception e) {
            if (CsTigoApplication.notificatApn) {
                CsTigoApplication.vibrate(false);
                CsTigoApplication.showNotification(
                        CSTigoNotificationID.APN_SETTING,
                        CsTigoApplication.getContext().getString(
                                R.string.warning),
                        CsTigoApplication.getContext().getString(
                                R.string.apn_change_notification),
                        MessageHistoryActivity.class);
            }
        }
    }
}
