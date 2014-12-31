package com.tigo.cs.android.receiver;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.helper.domain.GlobalParameterEntity;
import com.tigo.cs.android.helper.domain.MessageEntity;
import com.tigo.cs.android.service.LocationService;
import com.tigo.cs.android.util.Notifier;
import com.tigo.cs.api.entities.BaseServiceBean;
import com.tigo.cs.api.entities.TigoMoneyPhotoService;
import com.tigo.cs.api.entities.TigoMoneyService;

public class ResendSmsReceiver extends BroadcastReceiver {

    private static Gson gson;
    private static boolean initialize = false;

    private void init() {
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (!initialize) {
            init();
        }

        List<MessageEntity> messageList = CsTigoApplication.getMessageHelper().findAllRetry();

        GlobalParameterEntity globalParameterEntity = CsTigoApplication.getGlobalParameterHelper().getCiAlphaNumericEntity();
        globalParameterEntity.setParameterValue("0");
        CsTigoApplication.getGlobalParameterHelper().update(
                globalParameterEntity);

        for (MessageEntity message : messageList) {
            try {
                BaseServiceBean serviceEntity = null;

                if (message.getJsonData() != null) {
                    String hash = message.getJsonData();
                    serviceEntity = gson.fromJson(hash,
                            message.getEntityClass());
                    Notifier.info(getClass(),
                            "JSON DE REINTENTO: " + message.getJsonData());

                    if (serviceEntity instanceof TigoMoneyService) {
                        if (((TigoMoneyService) serviceEntity).getPhotoEntity() != null) {
                            TigoMoneyPhotoService tigoMoneyPhotoService = gson.fromJson(
                                    ((TigoMoneyService) serviceEntity).getPhotoEntity(),
                                    TigoMoneyPhotoService.class);
                            if (tigoMoneyPhotoService.getBackPhoto() != null
                                && tigoMoneyPhotoService.getFrontPhoto() != null) {

                                // message.setGsonRequest(serviceEntity.toString());
                                // CsTigoApplication.getMessageHelper().update(message);
                                Intent locationIntent = new Intent(context, LocationService.class);
                                locationIntent.putExtra("service",
                                        serviceEntity);
                                context.startService(locationIntent);

                            }
                        }
                    } else {

                        // message.setJsonData(serviceEntity.toString());
                        // CsTigoApplication.getMessageHelper().update(message);
                        Intent locationIntent = new Intent(context, LocationService.class);
                        locationIntent.putExtra("service", serviceEntity);
                        context.startService(locationIntent);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}