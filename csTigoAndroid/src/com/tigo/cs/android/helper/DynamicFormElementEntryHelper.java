package com.tigo.cs.android.helper;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.helper.domain.DynamicFormElementEntity;
import com.tigo.cs.android.helper.domain.DynamicFormElementEntryEntity;

public class DynamicFormElementEntryHelper extends AbstractHelper<DynamicFormElementEntryEntity> {

    public DynamicFormElementEntryHelper(Context context) {
        super(context, "dynamic_form_element_entry");
    }

    public DynamicFormElementEntryEntity findByCod(Long dynamicFormElementEntryCod) {
        return executeQuery("dynamic_form_element_entry_cod = ?",
                new String[] { dynamicFormElementEntryCod.toString() });
    }

    public List<DynamicFormElementEntryEntity> findByDynamicFormElement(DynamicFormElementEntity entity) {
        return findAll("cod_dynamic_form_element = ?",
                new String[] { entity.getDynamicFormElementCod().toString() },
                false);
    }

    public DynamicFormElementEntryEntity findRootByDynamicFormElement(DynamicFormElementEntity entity) {
        return executeQuery(
                "cod_dynamic_form_element = ? and cod_owner_entry is null",
                new String[] { entity.getDynamicFormElementCod().toString() });
    }

    public DynamicFormElementEntryEntity findChild(DynamicFormElementEntryEntity entry) {
        return executeQuery(
                "cod_owner_entry = ?",
                new String[] { entry.getDynamicFormElementEntryCod().toString() });
    }

    public List<DynamicFormElementEntryEntity> findChilds(Long owner) {
        return findAll("cod_owner_entry = ?",
                new String[] { owner.toString() }, false);
    }

    @Override
    protected ContentValues getContentValues(DynamicFormElementEntryEntity entity) {
        ContentValues values = new ContentValues();
        values.put("_id", entity.getId());
        values.put("dynamic_form_element_entry_cod",
                entity.getDynamicFormElementEntryCod());
        values.put("cod_dynamic_form_element",
                entity.getDynamicFormElement().getDynamicFormElementCod());
        values.put("title", entity.getTitle());
        values.put("description", entity.getDescription());
        values.put("final_entry", entity.getFinalEntry() ? 1 : 0);
        values.put(
                "multi_select_option",
                entity.getMultiSelectOption() != null ? entity.getMultiSelectOption() ? 1 : 0 : 0);
        values.put("order_num", entity.getOrderNum());
        if (entity.getOwnerEntry() != null) {
            values.put("cod_owner_entry",
                    entity.getOwnerEntry().getDynamicFormElementEntryCod());
        }
        values.put("cod_entry_type",
                entity.getEntryType().getDynamicFormEntryTypeCod());

        return values;
    }

    @Override
    protected DynamicFormElementEntryEntity cursorToEntity(Cursor cursor) {
        DynamicFormElementEntryEntity entity = new DynamicFormElementEntryEntity();
        entity.setId(cursor.getLong(0));
        entity.setDynamicFormElementEntryCod(cursor.getLong(1));
        entity.setDynamicFormElement(CsTigoApplication.getDynamicFormElementHelper().findByCod(
                cursor.getLong(2)));
        entity.setTitle(cursor.getString(3));
        entity.setDescription(cursor.getString(4));
        entity.setFinalEntry(cursor.getInt(5) == 1 ? true : false);
        entity.setMultiSelectOption(cursor.getInt(6) == 1 ? true : false);
        entity.setOrderNum(cursor.getInt(7));
        Long ownerCod = cursor.getLong(8);
        if (ownerCod != null) {
            entity.setOwnerEntry(findByCod(ownerCod));
        }
        entity.setEntryType(CsTigoApplication.getDynamicFormEntryTypeHelper().findByCod(
                cursor.getLong(9)));

        return entity;
    }

}
