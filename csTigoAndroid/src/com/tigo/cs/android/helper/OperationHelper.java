package com.tigo.cs.android.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.exception.FlyModeException;
import com.tigo.cs.android.helper.domain.OperationEntity;

public class OperationHelper extends AbstractHelper<OperationEntity> {

    private OperationEntity operation;

    public OperationHelper(Context context) {
        super(context, "operation");
    }

    /**
     * 
     * @return la informacion del operador configurado para usar la aplicacion
     * @throws Exception
     */
    public OperationEntity findOperationData() throws FlyModeException {

        /*
         * se obtiene siempre la informacion directamente del movil, para el
         * caso en que el usuario cambie su simcard.
         * 
         * con esto forzamos al usuario a que utilice siempre su simcard del
         * operador habilitado.
         */
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String networkOperator = telephonyManager.getNetworkOperator();

        String production = CsTigoApplication.getGlobalParameterHelper().findByParameterCode(
                "device.production").getParameterValue();

        if (networkOperator != null && networkOperator.trim().length() == 0) {

            boolean isEnabled = Settings.System.getInt(
                    context.getContentResolver(),
                    Settings.System.AIRPLANE_MODE_ON, 0) == 1;

            if (isEnabled) {
                throw new FlyModeException("No network available. Fly mode.");
            }

        }

        String mcc = null;
        String mnc = null;
        if (networkOperator != null) {
            mcc = networkOperator.substring(0, 3);
            mnc = networkOperator.substring(3);
        }
        operation = executeQuery("mcc = ? AND mnc = ? AND production = ?",
                new String[] { mcc, mnc, production });

        return operation;
    }

    @Override
    protected ContentValues getContentValues(OperationEntity operationEntity) {

        ContentValues values = new ContentValues();
        values.put("_id", operationEntity.getId());
        values.put("operation_name", operationEntity.getOperationName());
        values.put("mcc", operationEntity.getMcc());
        values.put("mnc", operationEntity.getMnc());
        values.put("production", operationEntity.getProduction());
        values.put("short_number", operationEntity.getShortNumber());
        values.put("rest_ws_location", operationEntity.getRestWsLocation());
        values.put("rest_ws_port", operationEntity.getRestWsPort());
        return values;

    }

    @Override
    protected OperationEntity cursorToEntity(Cursor cursor) {
        OperationEntity operationEntity = new OperationEntity();
        operationEntity.setId(cursor.getLong(0));
        operationEntity.setOperationName(cursor.getString(1));
        operationEntity.setMcc(cursor.getString(2));
        operationEntity.setMnc(cursor.getString(3));
        operationEntity.setProduction(cursor.getString(4));
        operationEntity.setShortNumber(cursor.getString(5));
        operationEntity.setRestWsLocation(cursor.getString(6));
        operationEntity.setRestWsPort(cursor.getInt(7));
        return operationEntity;
    }

}
