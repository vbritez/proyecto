package com.tigo.cs.android.helper;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

import com.tigo.cs.android.helper.domain.IconEntity;

public class IconHelper extends AbstractHelper<IconEntity> {

    public IconHelper(Context context) {
        super(context, "icon");
    }

    @Override
    protected ContentValues getContentValues(IconEntity entity) {
        ContentValues values = new ContentValues();
        values.put("_id", entity.getId());
        values.put("code", entity.getCode());
        values.put("url", entity.getUrl());
        values.put("description", entity.getDescription());
        values.put("image", entity.getImage());
        values.put("defaultIcon", entity.getDefaultIcon() ? 1 : 0);
        return values;
    }

    @Override
    protected IconEntity cursorToEntity(Cursor cursor) {
        IconEntity entity = new IconEntity();
        entity.setId(cursor.getLong(0));
        entity.setCode(cursor.getString(1));
        entity.setUrl(cursor.getString(2) != null ? String.valueOf(cursor.getString(2)) : null);
        entity.setDescription(cursor.getString(3) != null ? String.valueOf(cursor.getString(3)) : null);
        byte[] imageByte = cursor.getBlob(4);
        entity.setImage(imageByte);
        entity.setDefaultIcon(cursor.getLong(5) == 1 ? true : false);
        return entity;
    }

    public List<IconEntity> findAll(Boolean enabled) {
        boolean foundLocation = false;
        SQLiteDatabase database = getReadableDatabase();

        Cursor result = database.query(tableName, null, null, null, null,
                "_id", null);

        List<IconEntity> entityList = new ArrayList<IconEntity>();
        while (result.moveToNext()) {
            foundLocation = true;
            IconEntity entity = cursorToEntity(result);
            entityList.add(entity);
        }
        result.close();

        if (!foundLocation) {
            return null;
        }
        return entityList;
    }

    public IconEntity findByUrl(String url) {
        return executeQuery("url = ?", new String[] { url });
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

}
