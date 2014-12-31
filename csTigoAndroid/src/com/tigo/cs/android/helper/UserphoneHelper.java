package com.tigo.cs.android.helper;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tigo.cs.android.helper.domain.MetaEntity;
import com.tigo.cs.android.helper.domain.UserphoneEntity;

public class UserphoneHelper extends AbstractHelper<UserphoneEntity> {

    public UserphoneHelper(Context context) {
        super(context, "userphone");
    }

    @Override
    protected ContentValues getContentValues(UserphoneEntity entity) {
        ContentValues values = new ContentValues();
        values.put("_id", entity.getId());
        values.put("cellphone_number", entity.getCellphoneNumber());
        values.put("name", entity.getName());
        values.put("userphone_cod", entity.getUserphoneCod());
        values.put("enabled", entity.getEnabled() ? 1 : 0);
        return values;
    }

    @Override
    protected UserphoneEntity cursorToEntity(Cursor cursor) {
        UserphoneEntity entity = new UserphoneEntity();
        entity.setId(cursor.getLong(0));
        entity.setCellphoneNumber(cursor.getString(1) != null ? String.valueOf(cursor.getString(1)) : null);
        entity.setName(cursor.getString(2) != null ? String.valueOf(cursor.getString(2)) : null);
        entity.setUserphoneCod(cursor.getLong(3));
        entity.setEnabled(cursor.getLong(4) == 1 ? true : false);
        return entity;
    }
    
    public List<UserphoneEntity> findAllEnabled(Boolean enabled) {
        return findAll(true, "enabled");
    }
    
    public List<UserphoneEntity> findAll(Boolean enabled, String column) {
        boolean foundLocation = false;
        SQLiteDatabase database = getReadableDatabase();

        Cursor result = database.query(tableName, null, column.concat(" = ? "),
                new String[] { enabled ? "1" : "0" }, null, null, "_id", null);

        List<UserphoneEntity> entityList = new ArrayList<UserphoneEntity>();
        while (result.moveToNext()) {
            foundLocation = true;
            UserphoneEntity entity = cursorToEntity(result);
            entityList.add(entity);
        }
        result.close();

        if (!foundLocation) {
            return null;
        }
        return entityList;
    }
    
    public void disableAllUserphones() {
        List<UserphoneEntity> userphoneList = findAllEnabled(true);
        for (UserphoneEntity u : userphoneList) {
            u.setEnabled(false);
            update(u);
        }
    }
    
    public UserphoneEntity findByUserphoneCod(Long userphoneCod) {
        return executeQuery("userphone_cod = ?", new String[] { userphoneCod.toString() });
    }

}
