package com.tigo.cs.android.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.helper.domain.MetaMemberEntity;

public class MetaMemberHelper extends AbstractHelper<MetaMemberEntity> {

    public MetaMemberHelper(Context context) {
        super(context, "meta_member");
    }

    public MetaMemberEntity findByMetaAndMember(Long metaCod, Long metaMember) {
        return executeQuery("cod_meta = ?  AND cod_member = ?",
                new String[] { metaCod.toString(), metaMember.toString() });
    }

    @Override
    protected ContentValues getContentValues(MetaMemberEntity metaMember) {
        ContentValues values = new ContentValues();
        values.put("_id", metaMember.getId());
        values.put("cod_meta", metaMember.getMeta().getMetaCod());
        values.put("member_cod", metaMember.getMemberCod());
        values.put("name", metaMember.getName());
        return values;
    }

    @Override
    protected MetaMemberEntity cursorToEntity(Cursor cursor) {
        MetaMemberEntity metaMember = new MetaMemberEntity();
        metaMember.setId(cursor.getLong(0));
        metaMember.setMeta(CsTigoApplication.getMetaHelper().findByMetaCod(
                cursor.getLong(1)));
        metaMember.setMemberCod(cursor.getLong(2));
        metaMember.setName(cursor.getString(3));
        return metaMember;
    }

}
