package com.tigo.cs.android.helper;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.tigo.cs.android.helper.domain.ServiceOperationDataEntity;
import com.tigo.cs.android.helper.domain.ServiceOperationEntity;

public class ServiceOperationDataHelper extends AbstractHelper<ServiceOperationDataEntity> {

    public ServiceOperationDataHelper(Context context) {
        super(context, "service_operation_data");
    }

    public List<ServiceOperationDataEntity> findAll(ServiceOperationEntity serviceOperationEntity) {
        return super.findAll("cod_service_operation = ? ",
                new String[] { serviceOperationEntity.getId().toString() },
                false);
    }

    @Override
    protected ContentValues getContentValues(ServiceOperationDataEntity service) {
        ContentValues values = new ContentValues();
        values.put("_id", service.getId());
        values.put("cod_service_operation", service.getCodServiceOperation());
        values.put("servicemsg", service.getServicemsg());
        values.put("pos", service.getPos());
        return values;
    }

    @Override
    protected ServiceOperationDataEntity cursorToEntity(Cursor cursor) {
        ServiceOperationDataEntity serviceOperationDataEntity = new ServiceOperationDataEntity();

        serviceOperationDataEntity.setId(cursor.getLong(0));
        serviceOperationDataEntity.setCodServiceOperation(cursor.getLong(1));
        serviceOperationDataEntity.setServicemsg(cursor.getString(2));
        serviceOperationDataEntity.setPos(cursor.getInt(3));

        return serviceOperationDataEntity;
    }

}
