package com.tigo.cs.android.helper;

import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.helper.domain.MessageEntity;
import com.tigo.cs.android.util.MessageState;

public class MessageHelper extends AbstractHelper<MessageEntity> {

    public MessageHelper(Context context) {
        super(context, "message");
    }

    public Cursor findMessageInCursor() {
        SQLiteDatabase database = getReadableDatabase();
        return database.query(tableName, null, "message is not null", null,
                null, null, "_id DESC", null);
    }

    public List<MessageEntity> findAllRetry() {
        return super.findAll("state = ? ",
                new String[] { String.valueOf(MessageState.RETRY.ordinal()) },
                false);
    }

    @Override
    public Long insert(MessageEntity object) {
        return super.insert(object);
    }

    @Override
    public void update(MessageEntity object) {
        super.update(object);
    }

    @Override
    protected ContentValues getContentValues(MessageEntity message) {
        ContentValues values = new ContentValues();
        values.put("_id", message.getId());
        values.put("channel", message.getChannel());
        values.put("destination_number", message.getDestinationNumber());
        values.put("destination_url", message.getDestinationUrl());
        values.put("incoming", message.getIncoming());
        values.put(
                "event_date",
                message.getEventDate() != null ? message.getEventDate().getTime() : null);
        values.put("state", message.getState());
        values.put("cod_service", message.getService().getServicecod());
        values.put(
                "cod_service_event",
                message.getServiceEvent() != null ? message.getServiceEvent().getId() : null);

        values.put("message", message.getMessage());
        values.put("json_data", message.getJsonData());
        values.put(
                "entity_class",
                message.getEntityClass() != null ? message.getEntityClass().getName() : null);
        values.put("force_internet", message.getForceInternet() != null
            && message.getForceInternet() == true ? 1 : 0);

        return values;
    }

    @Override
    protected MessageEntity cursorToEntity(Cursor cursor) {
        MessageEntity message = new MessageEntity();
        message.setId(cursor.getLong(0));
        message.setChannel(cursor.getInt(1));
        message.setDestinationNumber(cursor.getString(2));
        message.setDestinationUrl(cursor.getString(3));
        message.setIncoming(cursor.getInt(4) == 1 ? true : false);
        message.setEventDate(new Date(cursor.getLong(5)));
        message.setState(cursor.getInt(6));
        message.setService(CsTigoApplication.getServiceHelper().findByServiceCod(
                cursor.getInt(7)));
        message.setServiceEvent(CsTigoApplication.getServiceEventHelper().findById(
                cursor.getInt(8)));
        message.setMessage(cursor.getString(9));
        message.setJsonData(cursor.getString(10));
        message.setEntityClass(getClass(cursor.getString(11)));
        message.setForceInternet(cursor.getInt(12) == 1 ? true : false);
        return message;
    }

}
