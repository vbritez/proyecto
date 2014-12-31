package com.tigo.cs.android.helper;

import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.helper.domain.MetaDataEntity;
import com.tigo.cs.android.helper.domain.MetaEntity;

public class MetaDataHelper extends AbstractHelper<MetaDataEntity> {

    public MetaDataHelper(Context context) {
        super(context, "meta_member");
    }

    @Override
    protected ContentValues getContentValues(MetaDataEntity entity) {
        ContentValues values = new ContentValues();
        values.put("_id", entity.getId());
        values.put("cod_meta", entity.getMetaMember().getMeta().getMetaCod());
        values.put("cod_member", entity.getMetaMember().getMemberCod());
        values.put("code", entity.getCode());
        values.put("data_value", entity.getDataValue());
        values.put(
                "date_time",
                entity.getDateTime() != null ? entity.getDateTime().getTime() : null);
        return values;
    }

    @Override
    protected MetaDataEntity cursorToEntity(Cursor cursor) {
        MetaDataEntity entity = new MetaDataEntity();
        entity.setId(cursor.getLong(0));
        entity.setMetaMember(CsTigoApplication.getMetaMemberHelper().findByMetaAndMember(
                cursor.getLong(1), cursor.getLong(2)));
        entity.setCode(cursor.getString(3));
        entity.setDataValue(cursor.getString(4));
        entity.setDateTime(new Date(cursor.getLong(5)));
        return entity;
    }
    
    public List<MetaDataEntity> findByMetaMember(String codMeta, String codMember) {
        return findAll("cod_meta = ? AND cod_member = ? ", new String[] { codMeta ,codMember}, false);
    }
    
    public MetaDataEntity findByMetaMemberCode(String codMeta, String codMember, String code) {
        return executeQuery("cod_meta = ? AND cod_member = ? AND code = ? ", new String[] { codMeta ,codMember,code});
    }
}
