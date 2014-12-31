package com.tigo.cs.android.receiver;

import java.text.MessageFormat;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.helper.domain.MessageEntity;
import com.tigo.cs.android.helper.domain.ServiceEntity;
import com.tigo.cs.android.helper.domain.ServiceEventEntity;
import com.tigo.cs.android.service.LocationService;
import com.tigo.cs.android.util.MessageState;
import com.tigo.cs.api.entities.TrackingService;

public class AlarmReceiver extends BroadcastReceiver {

    private TrackingService entity = null;

    protected ServiceEventEntity serviceEvent = CsTigoApplication.getServiceEventHelper().findByServiceCodServiceEventCod(
            4, "REGISTER");

    @Override
    public void onReceive(Context context, Intent intent) {

        /*
         * verificamos que el servicio de tigo money no este activo y solamente
         * si no esta activo enviamos los rastreos persistentes
         */

        ServiceEntity serviceEntity = CsTigoApplication.getServiceHelper().findByServiceCod(
                -3);

        if (!serviceEntity.getEnabled()) {
            entity = new TrackingService();
            entity.setServiceCod(serviceEvent.getService().getServicecod());
            entity.setEvent(serviceEvent.getServiceEventCod());
            entity.setEncryptResponse(true);

            String message = MessageFormat.format(
                    serviceEvent.getMessagePattern(),
                    serviceEvent.getService().getServicecod());

            entity.setMessageId(persistMessage(message));
            Intent trackingIntent = new Intent(context, LocationService.class);
            trackingIntent.putExtra("service", entity);
            context.startService(trackingIntent);
        }
    }

    protected Long persistMessage(String msg) {
        MessageEntity message = new MessageEntity();
        message.setEventDate(new Date());
        message.setIncoming(false);
        message.setMessage(msg);
        message.setService(serviceEvent.getService());
        message.setServiceEvent(serviceEvent);
        message.setState(MessageState.PREPARED_TO_SEND.ordinal());
        return CsTigoApplication.getMessageHelper().insert(message);
    }

}