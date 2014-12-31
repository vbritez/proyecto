package com.tigo.cs.android.helper;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.tigo.cs.android.helper.domain.DynamicFormElementEntity;

public class DynamicFormElementHelper extends AbstractHelper<DynamicFormElementEntity> {

    public DynamicFormElementHelper(Context context) {
        super(context, "dynamic_form_element");
    }

    public DynamicFormElementEntity findByCod(Long dynamicFormElementCod) {
        return executeQuery("dynamic_form_element_cod = ?",
                new String[] { dynamicFormElementCod.toString() });
    }

    public List<DynamicFormElementEntity> findAllEnabled() {
        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);

        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Long todayLong = calendar.getTime().getTime();

        return findAll("finish_date >= ?",
                new String[] { todayLong.toString() }, false);
    }

    @Override
    protected ContentValues getContentValues(DynamicFormElementEntity entity) {
        ContentValues values = new ContentValues();
        values.put("_id", entity.getId());
        values.put("dynamic_form_element_cod",
                entity.getDynamicFormElementCod());
        values.put("description", entity.getDescription());
        values.put("start_date", entity.getStartDate().getTime());
        values.put(
                "finish_date",
                entity.getFinishDate() != null ? entity.getFinishDate().getTime() : null);
        return values;
    }

    @Override
    protected DynamicFormElementEntity cursorToEntity(Cursor cursor) {
        DynamicFormElementEntity entity = new DynamicFormElementEntity();
        entity.setId(cursor.getLong(0));
        entity.setDynamicFormElementCod(cursor.getLong(1));
        entity.setDescription(cursor.getString(2));
        entity.setStartDate(new Date(cursor.getLong(3)));
        Long finishLong = cursor.getLong(4);
        if (finishLong != null) {
            entity.setStartDate(new Date(finishLong));
        }

        return entity;
    }

}
