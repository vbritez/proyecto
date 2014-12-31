package com.tigo.cs.android.activity.visit;

import java.text.MessageFormat;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.AbstractActivity;
import com.tigo.cs.android.helper.domain.ServiceEventEntity;
import com.tigo.cs.android.util.Notifier;
import com.tigo.cs.api.entities.MetadataCrudService;

public class VisitActivity extends AbstractActivity {

    private OnClickListener geolocalizeVisitOnClickListener;
    private OnClickListener normalVisitOnClickListener;

    @Override
    public Integer getServicecod() {
        return 1;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visit_main);
        onSetContentViewFinish();
        Button geolocalizeVisitButton = (Button) findViewById(R.id.geoLocalizeButton);
        Button normalVisitButton = (Button) findViewById(R.id.normalVisitButton);

        geolocalizeVisitOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * Recuperamos el service_event GEOLOCATION. A la tabla
                 * service_event se le agrego un nuevo campo require wait, para
                 * saber si va a esperar recuperar la localizacion. Esta
                 * condicion es preguntada en el LocationService
                 */
                ServiceEventEntity eventUpdate = CsTigoApplication.getServiceEventHelper().findByServiceCodServiceEventCod(
                        99, "GEOLOCATION");
                if (!validateInternetOn(eventUpdate)) {
                    return;
                }

                Notifier.info(getClass(),
                        "Validando informacion antes de enviar mensaje.");

                Notifier.info(getClass(),
                        "Se obtiene patron de mensajes y se crea el mensaje de texto");
                String msg = MessageFormat.format(
                        eventUpdate.getMessagePattern(),
                        eventUpdate.getService().getServicecod());

                Notifier.info(getClass(),
                        "Se crean los datos de la entidad a ser enviada.");

                entity = new MetadataCrudService();
                entity.setServiceCod(eventUpdate.getService().getServicecod());
                entity.setEvent(eventUpdate.getServiceEventCod());
                entity.setRequiresLocation(eventUpdate.getRequiresLocation());
                ((MetadataCrudService) entity).setMetaCode(1);
                ((MetadataCrudService) entity).setMemberCode(1);
                ((MetadataCrudService) entity).setActivityToOpen("com.tigo.cs.android.activity.visit.VisitGeolocalizeActivity");

                Notifier.info(getClass(), "Se creo la entidad a ser enviada");

                endUserMark(msg);

            }
        };
        normalVisitOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                startEventActivity(VisitNormalActivity.class);

            }
        };

        geolocalizeVisitButton.setOnClickListener(geolocalizeVisitOnClickListener);
        normalVisitButton.setOnClickListener(normalVisitOnClickListener);

    }

    private boolean validateInternetOn(ServiceEventEntity eventUpdate) {
        if (eventUpdate.getForceInternet()) {
            if (!CsTigoApplication.getGlobalParameterHelper().getInternetEnabled()) {
                Toast.makeText(
                        this,
                        CsTigoApplication.getContext().getString(
                                R.string.internet_not_enabled_for_update),
                        Toast.LENGTH_LONG).show();
                return false;
            }
        }

        return true;
    }

}