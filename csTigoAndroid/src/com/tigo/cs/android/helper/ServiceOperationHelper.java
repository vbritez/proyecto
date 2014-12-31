package com.tigo.cs.android.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.tigo.cs.android.helper.domain.ServiceOperationEntity;

public class ServiceOperationHelper extends AbstractHelper<ServiceOperationEntity> {

    public ServiceOperationHelper(Context context) {
        super(context, "service_operation");
    }

    @Override
    protected ContentValues getContentValues(ServiceOperationEntity service) {
        ContentValues values = new ContentValues();
        values.put("_id", service.getId());
        values.put("cod_service", service.getServicecod());
        values.put("operation_name", service.getOperationname());
        return values;
    }

    @Override
    protected ServiceOperationEntity cursorToEntity(Cursor cursor) {
        ServiceOperationEntity serviceOperationEntity = new ServiceOperationEntity();

        serviceOperationEntity.setId(cursor.getLong(0));
        serviceOperationEntity.setServicecod(cursor.getInt(1));
        serviceOperationEntity.setOperationname(cursor.getString(2));

        return serviceOperationEntity;
    }

}
