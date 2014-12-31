package com.tigo.cs.android.helper;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tigo.cs.android.helper.domain.ServiceEntity;
import com.tigo.cs.android.util.Notifier;

public class ServiceHelper extends AbstractHelper<ServiceEntity> {

    public ServiceHelper(Context context) {
        super(context, "service");
    }

    public ServiceEntity findByServiceCod(Integer serviceCod) {
        return executeQuery("servicecod = ?",
                new String[] { serviceCod.toString() });
    }

    @Override
    protected ContentValues getContentValues(ServiceEntity service) {
        ContentValues values = new ContentValues();
        values.put("_id", service.getId());
        values.put("servicecod", service.getServicecod());
        values.put("servicename", service.getServicename());
        values.put("class_name", service.getClassName().getName());
        values.put("platform_service", service.getPlatformService() ? 1 : 0);
        values.put("enabled", service.getEnabled() ? 1 : 0);
        values.put("can_delete", service.getCanDelete() ? 1 : 0);
        if (service.getServiceClass() != null) {
            values.put("service_class", service.getServiceClass().getName());
        }
        return values;
    }

    @Override
    protected ServiceEntity cursorToEntity(Cursor cursor) {
        ServiceEntity service = new ServiceEntity();
        service.setId(cursor.getLong(0));
        service.setServicecod(cursor.getInt(1));
        service.setServicename(cursor.getString(2));
        service.setClassName(getClass(cursor.getString(3)));
        service.setPlatformService(cursor.getInt(4) == 1 ? true : false);
        service.setEnabled(cursor.getInt(5) == 1 ? true : false);
        service.setCanDelete(cursor.getInt(6) == 1 ? true : false);
        String className = cursor.getString(7);
        if (className != null) {
            service.setServiceClass(getClass(className));
        }
        return service;
    }

    public List<ServiceEntity> findAllDeletable(Boolean enabled) {
        if (enabled == null) {
            return null;
        }
        boolean foundLocation = false;
        SQLiteDatabase database = getReadableDatabase();

        Cursor result = database.query(tableName, null, "can_delete = 1", null,
                null, null, "_id", null);

        List<ServiceEntity> entityList = new ArrayList<ServiceEntity>();
        while (result.moveToNext()) {
            foundLocation = true;
            ServiceEntity entity = cursorToEntity(result);
            entityList.add(entity);
        }
        result.close();

        if (!foundLocation) {
            return null;
        }
        return entityList;
    }

    public List<ServiceEntity> findAllEnabled(Boolean enabled) {
        if (enabled == null) {
            return null;
        }
        boolean foundLocation = false;
        SQLiteDatabase database = getReadableDatabase();

        Cursor result = database.query(tableName, null,
                "enabled = 1 AND platform_service = 1", null, null, null,
                "_id", null);

        List<ServiceEntity> entityList = new ArrayList<ServiceEntity>();
        while (result.moveToNext()) {
            foundLocation = true;
            ServiceEntity entity = cursorToEntity(result);
            entityList.add(entity);
        }
        result.close();

        if (!foundLocation) {
            return null;
        }
        return entityList;
    }

    public List<ServiceEntity> findAllPlatformService(Boolean showDisabled) {
        boolean foundLocation = false;
        SQLiteDatabase database = getReadableDatabase();

        String where = "platform_service = 1";
        if (!showDisabled) {
            where = where.concat(" AND enabled = 1");
        }

        Cursor result = database.query(tableName, null, where, null, null,
                null, "servicecod asc", null);

        List<ServiceEntity> entityList = new ArrayList<ServiceEntity>();
        while (result.moveToNext()) {
            foundLocation = true;
            ServiceEntity entity = cursorToEntity(result);
            entityList.add(entity);
        }
        result.close();

        if (!foundLocation) {
            return new ArrayList<ServiceEntity>();
        }
        return entityList;
    }

    public void disableAllService() {
        Notifier.info(getClass(),
                "Se deshabilitaron todos los servicios del dispositivo.");
        List<ServiceEntity> serviceList = findAllPlatformService(true);
        for (ServiceEntity service : serviceList) {
            service.setEnabled(false);
            update(service);
        }
        Notifier.info(getClass(),
                "Todos los servicios del dispositivo han sido deshablitados.");
    }

    public void disableAllServiceDeletable() {
        List<ServiceEntity> serviceList = findAllDeletable(true);
        if (serviceList != null) {
            for (ServiceEntity service : serviceList) {
                service.setCanDelete(false);
                update(service);
            }
        }
    }

}
