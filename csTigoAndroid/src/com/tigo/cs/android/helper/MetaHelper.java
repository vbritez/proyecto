package com.tigo.cs.android.helper;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tigo.cs.android.helper.domain.MetaEntity;

public class MetaHelper extends AbstractHelper<MetaEntity> {

    public MetaHelper(Context context) {
        super(context, "meta");
    }

    public MetaEntity findByMetaCod(Long metaCod) {
        return executeQuery("meta_cod = ?", new String[] { metaCod.toString() });
    }

    public MetaEntity findByMetaName(String metaName) {
        return executeQuery("meta_name = ?", new String[] { metaName });
    }

    public List<MetaEntity> findAllCreatable(Boolean enabled) {
        return findAll(true, "can_create");
    }

    public List<MetaEntity> findAllReadable(Boolean enabled) {
        return findAll(true, "can_read");
    }

    public List<MetaEntity> findAll(Boolean enabled, String column) {
        if (enabled == null) {
            return null;
        }
        boolean foundLocation = false;
        SQLiteDatabase database = getReadableDatabase();

        Cursor result = database.query(tableName, null, column.concat(" = ? "),
                new String[] { enabled ? "1" : "0" }, null, null, "_id", null);

        List<MetaEntity> entityList = new ArrayList<MetaEntity>();
        while (result.moveToNext()) {
            foundLocation = true;
            MetaEntity entity = cursorToEntity(result);
            entityList.add(entity);
        }
        result.close();

        if (!foundLocation) {
            return null;
        }
        return entityList;
    }

    @Override
    protected ContentValues getContentValues(MetaEntity meta) {

        ContentValues values = new ContentValues();
        values.put("_id", meta.getId());
        values.put("meta_cod", meta.getMetaCod());
        values.put("meta_name", meta.getMetaname());
        values.put("can_create", meta.getCanCreate() ? 1 : 0);
        values.put("can_read", meta.getCanRead() ? 1 : 0);
        values.put("can_update", meta.getCanUpdate() ? 1 : 0);
        values.put("can_delete", meta.getCanDelete() ? 1 : 0);
        return values;

    }

    @Override
    protected MetaEntity cursorToEntity(Cursor cursor) {
        MetaEntity meta = new MetaEntity();
        meta.setId(cursor.getLong(0));
        meta.setMetaCod(cursor.getLong(1));
        meta.setMetaname(cursor.getString(2));
        meta.setCanCreate(cursor.getInt(3) == 1 ? true : false);
        meta.setCanRead(cursor.getInt(4) == 1 ? true : false);
        meta.setCanUpdate(cursor.getInt(5) == 1 ? true : false);
        meta.setCanDelete(cursor.getInt(6) == 1 ? true : false);
        return meta;
    }

    public void disableAllCreatableMeta() {
        List<MetaEntity> metaList = findAll();
        for (MetaEntity meta : metaList) {
            meta.setCanCreate(false);
            update(meta);
        }
    }

    public void disableAllReadableMeta() {
        List<MetaEntity> metaList = findAll();
        for (MetaEntity meta : metaList) {
            meta.setCanRead(false);
            update(meta);
        }
    }
}
