package com.tigo.cs.android.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.tigo.cs.android.helper.domain.DynamicFormEntryTypeEntity;

public class DynamicFormEntryTypeHelper extends AbstractHelper<DynamicFormEntryTypeEntity> {

    public DynamicFormEntryTypeHelper(Context context) {
        super(context, "dynamic_form_entry_type");
    }

    public DynamicFormEntryTypeEntity findByCod(Long dynamicFormEntryTypeCod) {
        return executeQuery("dynamic_form_entry_type_cod = ?",
                new String[] { dynamicFormEntryTypeCod.toString() });
    }

    @Override
    protected ContentValues getContentValues(DynamicFormEntryTypeEntity entity) {
        ContentValues values = new ContentValues();
        values.put("_id", entity.getId());
        values.put("dynamic_form_entry_type_cod",
                entity.getDynamicFormEntryTypeCod());
        values.put("name", entity.getName());
        values.put("description", entity.getDescription());
        values.put("persistible", entity.getPersistible() ? 1 : 0);
        return values;
    }

    @Override
    protected DynamicFormEntryTypeEntity cursorToEntity(Cursor cursor) {
        DynamicFormEntryTypeEntity entity = new DynamicFormEntryTypeEntity();
        entity.setId(cursor.getLong(0));
        entity.setDynamicFormEntryTypeCod(cursor.getLong(1));
        entity.setName(cursor.getString(2));
        entity.setDescription(cursor.getString(3));
        entity.setPersistible(cursor.getInt(4) == 1 ? true : false);
        return entity;
    }

}
