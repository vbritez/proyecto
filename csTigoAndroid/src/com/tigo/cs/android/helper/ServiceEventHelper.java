package com.tigo.cs.android.helper;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.helper.domain.ServiceEntity;
import com.tigo.cs.android.helper.domain.ServiceEventEntity;

public class ServiceEventHelper extends AbstractHelper<ServiceEventEntity> {

    public ServiceEventHelper(Context context) {
        super(context, "service_event");
    }

    public ServiceEventEntity findById(Integer id) {
        return executeQuery("_id = ?", new String[] { id.toString() });
    }

    public List<ServiceEventEntity> findByService(ServiceEntity service) {
        return findAll("servicecod = ?",
                new String[] { service.getServicecod().toString() }, false);
    }

    public ServiceEventEntity findByServiceCodServiceEventCod(Integer serviceCod, String serviceEventCode) {
        return executeQuery("servicecod = ? and serviceeventcod = ?",
                new String[] { serviceCod.toString(), serviceEventCode });
    }

    @Override
    protected ContentValues getContentValues(ServiceEventEntity serviceEvent) {

        ContentValues values = new ContentValues();
        values.put("_id", serviceEvent.getId());
        values.put("servicecod", serviceEvent.getService().getServicecod());
        values.put("message_pattern", serviceEvent.getMessagePattern());
        values.put("internet", serviceEvent.getInternet() ? 1 : 0);
        values.put("ws_path", serviceEvent.getWsPath());
        values.put("force_internet", serviceEvent.getInternet() ? 1 : 0);
        values.put("requires_location",
                serviceEvent.getRequiresLocation() ? 1 : 0);
        values.put("encrypt_message", serviceEvent.getEncryptMessage() ? 1 : 0);
        values.put("notify_message", serviceEvent.getNotifyMessage() ? 1 : 0);
        values.put("ignore_gps_disabled",
                serviceEvent.getIgnoreGpsDisabled() ? 1 : 0);
        values.put("json_response", serviceEvent.getJsonResponse() ? 1 : 0);
        values.put("force_gps", serviceEvent.getForceGps() ? 1 : 0);
        values.put("activity_to_open",
                serviceEvent.getActivityToOpen().getName());
        values.put("requires_wait", serviceEvent.getRequiresLocation() ? 1 : 0);
        values.put("retry", serviceEvent.getRetry() ? 1 : 0);
        values.put("gson_datetime_format", serviceEvent.getGsonDatetimeFormat());
        return values;

    }

    @Override
    protected ServiceEventEntity cursorToEntity(Cursor cursor) {
        ServiceEventEntity serviceEvent = new ServiceEventEntity();
        serviceEvent.setId(cursor.getLong(0));
        serviceEvent.setService(CsTigoApplication.getServiceHelper().findByServiceCod(
                cursor.getInt(1)));
        serviceEvent.setServiceEventCod(cursor.getString(2));
        serviceEvent.setMessagePattern(cursor.getString(3));
        serviceEvent.setInternet(cursor.getInt(4) == 1 ? true : false);
        serviceEvent.setWsPath(cursor.getString(5));
        serviceEvent.setForceInternet(cursor.getInt(6) == 1 ? true : false);
        serviceEvent.setRequiresLocation(cursor.getInt(7) == 1 ? true : false);
        serviceEvent.setEncryptMessage(cursor.getInt(8) == 1 ? true : false);
        serviceEvent.setNotifyMessage(cursor.getInt(9) == 1 ? true : false);
        serviceEvent.setIgnoreGpsDisabled(cursor.getInt(10) == 1 ? true : false);
        serviceEvent.setJsonResponse(cursor.getInt(11) == 1 ? true : false);
        serviceEvent.setForceGps(cursor.getInt(12) == 1 ? true : false);
        serviceEvent.setActivityToOpen(getClass(cursor.getString(13)));
        serviceEvent.setRequiresWait(cursor.getInt(14) == 1 ? true : false);
        serviceEvent.setRetry(cursor.getInt(15) == 1 ? true : false);
        serviceEvent.setGsonDatetimeFormat(cursor.getString(16));
        return serviceEvent;
    }

}
