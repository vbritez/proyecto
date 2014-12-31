package com.tigo.cs.android.helper;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.tigo.cs.android.helper.domain.SessionEntity;

public class SessionHelper extends AbstractHelper<SessionEntity> {

    public SessionHelper(Context context) {
        super(context, "session");
    }

    @Override
    protected ContentValues getContentValues(SessionEntity entity) {
        ContentValues values = new ContentValues();
        values.put("_id", entity.getId());
        values.put("cellphone_number", entity.getCellphoneNumber());
        values.put("imsi", entity.getImsi());
        values.put("expiration_date", entity.getExpirationDate().getTime());
        return values;
    }

    @Override
    protected SessionEntity cursorToEntity(Cursor cursor) {
    	SessionEntity entity = new SessionEntity();
        entity.setId(cursor.getLong(0));
        entity.setCellphoneNumber(cursor.getString(1) != null ? String.valueOf(cursor.getString(1)) : null);
        entity.setImsi(cursor.getString(2) != null ? String.valueOf(cursor.getString(2)) : null);
        entity.setExpirationDate(new Date(cursor.getLong(3)));
        return entity;
    }
        
    public void deleteSessions() {
    	deleteAll();
    }
    
    public void deleteSession(Long id) { 
    	SessionEntity s = find(id);
    	if (s != null)
    		delete(s);
    }
    
    public SessionEntity findByImsi(String imsi) {
        return executeQuery("imsi = ?", new String[] { imsi });
    }

}
