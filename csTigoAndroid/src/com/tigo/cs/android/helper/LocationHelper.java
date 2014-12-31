package com.tigo.cs.android.helper;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.tigo.cs.android.helper.domain.LocationEntity;

public class LocationHelper extends AbstractHelper<LocationEntity> {

    private static final long TWO_MINUTES = 2 * 60 * 1000;

    public LocationHelper(Context context) {
        super(context, "location");
    }

    public LocationEntity findLastValid() {
        LocationEntity entity = super.findLast();

        if (entity != null) {

            Long locationTime = entity.getDateTime().getTime();
        }

        return entity;
    }

    @Override
    protected ContentValues getContentValues(LocationEntity entity) {

        ContentValues values = new ContentValues();
        values.put("_id", entity.getId());
        values.put("provider", entity.getProvider());
        values.put("cell_id", entity.getCellId());
        values.put("lac", entity.getLac());
        values.put("latitude", entity.getLatitude());
        values.put("longitude", entity.getLongitude());
        values.put("accuracy", entity.getAccuracy());
        values.put("bearing", entity.getBearing());
        values.put("altitude", entity.getAltitude());
        values.put("speed", entity.getSpeed());
        values.put(
                "date_time",
                entity.getDateTime() != null ? entity.getDateTime().getTime() : null);
        return values;
    }

    @Override
    protected LocationEntity cursorToEntity(Cursor cursor) {
        LocationEntity entity = new LocationEntity();
        entity.setId(cursor.getLong(0));
        entity.setProvider(cursor.getString(1));
        entity.setCellId(cursor.getInt(2));
        entity.setLac(cursor.getInt(3));
        entity.setLatitude(cursor.getDouble(4));
        entity.setLongitude(cursor.getDouble(5));
        entity.setAccuracy(cursor.getFloat(6));
        entity.setBearing(cursor.getFloat(7));
        entity.setAltitude(cursor.getDouble(8));
        entity.setSpeed(cursor.getFloat(9));
        entity.setDateTime(new Date(cursor.getLong(10)));
        return entity;
    }

}
