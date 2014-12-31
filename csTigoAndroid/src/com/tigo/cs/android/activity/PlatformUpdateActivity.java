package com.tigo.cs.android.activity;

import java.text.MessageFormat;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.serviceoperation.ServiceOptionActivity;
import com.tigo.cs.android.helper.domain.GlobalParameterEntity;
import com.tigo.cs.android.helper.domain.ServiceEventEntity;
import com.tigo.cs.android.util.Notifier;
import com.tigo.cs.api.entities.PermissionService;

public class PlatformUpdateActivity extends AbstractActivity {

    private static final Integer SERVICECOD = 0;
    private static final String SERVICEEVENTCOD = "CS";

    private OnClickListener serviceOnClickListener;

    private OnClickListener internetOnClickListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.platform_update_main);
        onSetContentViewFinish();

        Button serviceUpdate = (Button) findViewById(R.id.service_update_button);
        Button enableDevice = (Button) findViewById(R.id.enable_device_button);
        Button internetOption = (Button) findViewById(R.id.internet_option_button);
        Button serviceOption = (Button) findViewById(R.id.service_option_button);
        CheckBox serviceDisabledCheck = (CheckBox) findViewById(R.id.serviceDisabledCheck);
        CheckBox platformVibrateCheck = (CheckBox) findViewById(R.id.platformVibrateCheck);
        CheckBox platformSmsShowAllHistory = (CheckBox) findViewById(R.id.smsShowAllCheck);
        Button iconUpdate = (Button) findViewById(R.id.icon_update_button);

        serviceDisabledCheck.setChecked(CsTigoApplication.getGlobalParameterHelper().getServiceShowDisabled());
        platformVibrateCheck.setChecked(CsTigoApplication.getGlobalParameterHelper().getPlatformVibrate());
        platformSmsShowAllHistory.setChecked(CsTigoApplication.getGlobalParameterHelper().getSmsShowAllHistory());

        /*
         * cada vez que exista un cambio en la opcion de visualizacion de
         * servicios deshabilitados lo persistimos a la base de datos
         * directamente para al recargar la lista de servicios
         */
        serviceDisabledCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (verifyDeviceEnabled()) {

                    GlobalParameterEntity globalParameterEntity = CsTigoApplication.getGlobalParameterHelper().getServiceShowDisabledEntity();
                    updateBoolean(globalParameterEntity, isChecked);
                } else {
                    buttonView.setChecked(!isChecked);
                }

            }
        });

        platformVibrateCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (verifyDeviceEnabled()) {
                    GlobalParameterEntity globalParameterEntity = CsTigoApplication.getGlobalParameterHelper().getPlatformVibrateEntity();
                    updateBoolean(globalParameterEntity, isChecked);
                } else {
                    buttonView.setChecked(!isChecked);
                }

            }
        });

        platformSmsShowAllHistory.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (verifyDeviceEnabled()) {
                    GlobalParameterEntity globalParameterEntity = CsTigoApplication.getGlobalParameterHelper().getSmsShowAllHistoryEntity();
                    updateBoolean(globalParameterEntity, isChecked);
                } else {
                    buttonView.setChecked(!isChecked);
                }

            }
        });

        serviceUpdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (verifyDeviceEnabled()) {
                    Notifier.info(getClass(),
                            "Se deshabilitaron todos los servicios del dispositivo.");

                    Notifier.info(getClass(),
                            "Se creara el mensaje a enviar a la plataforma");
                    updatePlatform(CsTigoApplication.getServiceEventHelper().findByServiceCodServiceEventCod(
                            0, "CS"));
                }
            }
        });

        iconUpdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (verifyDeviceEnabled()) {
                    updateIcons(CsTigoApplication.getServiceEventHelper().findByServiceCodServiceEventCod(
                            0, "icon.update.request.ondemand"));
                }
            }
        });

        enableDevice.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                enableDevice(CsTigoApplication.getServiceEventHelper().findByServiceCodServiceEventCod(
                        0, "DEV"));
            }
        });

        serviceOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyDeviceEnabled()) {
                    startEventActivity(ServiceOptionActivity.class);
                }
            }
        };
        internetOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyDeviceEnabled()) {
                    startEventActivity(InternetOptionActivity.class);
                }
            }
        };

        internetOption.setOnClickListener(internetOnClickListener);
        serviceOption.setOnClickListener(serviceOnClickListener);
    }

    private boolean verifyDeviceEnabled() {
        /*
         * verificamos si el dispositivo se encuentra habilitado antes de
         * solicitar la actualizacion de servicios
         */

        if (!CsTigoApplication.getGlobalParameterHelper().getDeviceEnabled()) {

            /*
             * notificamos que el dispositivo no esta habilitado
             */
            Toast.makeText(
                    PlatformUpdateActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.no_enabled_device), Toast.LENGTH_LONG).show();
            return false;

        }
        return true;
    }

    private void updateBoolean(GlobalParameterEntity globalParameterEntity, boolean isChecked) {
        globalParameterEntity.setParameterValue(isChecked ? "1" : "0");
        CsTigoApplication.getGlobalParameterHelper().update(
                globalParameterEntity);
    }

    private void enableDevice(ServiceEventEntity event) {

        Notifier.info(getClass(),
                "Validando informacion antes de enviar mensaje.");

        if (!startUserMark()) {
            return;
        }

        Notifier.info(getClass(),
                "Se obtiene patron de mensajes y se crea el mensaje de texto");
        String msg = MessageFormat.format(event.getMessagePattern(),
                SERVICECOD, telephonyManager.getDeviceId(),
                telephonyManager.getSubscriberId());

        Notifier.info(getClass(),
                "Se crean los datos de la entidad a ser enviada.");
        entity = new PermissionService();
        entity.setServiceCod(SERVICECOD);
        entity.setEvent(event.getServiceEventCod());
        entity.setRequiresLocation(event.getRequiresLocation());

        Notifier.info(getClass(), "Se creo la entidad a ser enviada");

        endUserMark(msg);
    }

    private void updateIcons(ServiceEventEntity event) {

        Notifier.info(getClass(),
                "Validando informacion antes de enviar mensaje.");

        if (!startUserMark()) {
            return;
        }

        Notifier.info(getClass(),
                "Se obtiene patron de mensajes y se crea el mensaje de texto");
        String msg = "";

        Notifier.info(getClass(),
                "Se crean los datos de la entidad a ser enviada.");
        entity = new PermissionService();
        entity.setServiceCod(SERVICECOD);
        entity.setEvent(event.getServiceEventCod());
        entity.setRequiresLocation(event.getRequiresLocation());

        Notifier.info(getClass(), "Se creo la entidad a ser enviada");

        CsTigoApplication.manage(entity);
        goToMain();
    }

    private void updatePlatform(ServiceEventEntity event) {

        Notifier.info(getClass(),
                "Validando informacion antes de enviar mensaje.");

        if (!startUserMark()) {
            return;
        }

        Notifier.info(getClass(),
                "Se obtiene patron de mensajes y se crea el mensaje de texto");
        String msg = MessageFormat.format(event.getMessagePattern(), SERVICECOD);

        Notifier.info(getClass(),
                "Se crean los datos de la entidad a ser enviada.");
        entity = new PermissionService();
        entity.setServiceCod(SERVICECOD);
        entity.setEvent(event.getServiceEventCod());
        entity.setRequiresLocation(event.getRequiresLocation());

        Notifier.info(getClass(), "Se creo la entidad a ser enviada");

        endUserMark(msg);
    }

    @Override
    protected boolean startUserMark() {
        return true;
    }

    @Override
    public Integer getServicecod() {
        return SERVICECOD;
    }

    @Override
    public String getServiceEventCod() {
        return SERVICEEVENTCOD;
    }

}
