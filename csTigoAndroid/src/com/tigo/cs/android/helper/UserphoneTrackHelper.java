package com.tigo.cs.android.helper;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.tigo.cs.android.helper.domain.UserphoneTrackEntity;

public class UserphoneTrackHelper extends AbstractHelper<UserphoneTrackEntity> {

    public UserphoneTrackHelper(Context context) {
        super(context, "userphone_track");
    }

    @Override
    protected ContentValues getContentValues(UserphoneTrackEntity entity) {
        ContentValues values = new ContentValues();
        values.put("_id", entity.getId());
        values.put("userphone_cod", entity.getUserphoneCod());
        values.put("return_message", entity.getReturnMessage());
        values.put("latitude", entity.getLatitude());
        values.put("longitude", entity.getLongitude());
        values.put("gps", entity.getGps() ? 1 : 0);
        values.put(
                "date_time",
                entity.getDateTime() != null ? entity.getDateTime().getTime() : null);
        return values;
    }

    @Override
    protected UserphoneTrackEntity cursorToEntity(Cursor cursor) {
        UserphoneTrackEntity entity = new UserphoneTrackEntity();
        entity.setId(cursor.getLong(0));
        entity.setUserphoneCod(cursor.getLong(1));
        entity.setReturnMessage(cursor.getString(2) != null ? String.valueOf(cursor.getString(2)) : null);
        entity.setLatitude(cursor.getDouble(3));
        entity.setLongitude(cursor.getDouble(4));
        entity.setGps(cursor.getInt(5) == 1 ? true : false);
        entity.setDateTime(new Date(cursor.getLong(6)));
        return entity;
    }

}
