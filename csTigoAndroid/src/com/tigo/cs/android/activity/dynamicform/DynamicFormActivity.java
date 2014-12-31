package com.tigo.cs.android.activity.dynamicform;

import java.text.MessageFormat;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.AbstractActivity;
import com.tigo.cs.android.util.Notifier;
import com.tigo.cs.api.entities.DynamicFormService;

public class DynamicFormActivity extends AbstractActivity {

    private static final Integer SERVICECOD = 20;
    private static final String SERVICEEVENTCOD = "FIND";

    private OnClickListener updateOnClickListener;
    private OnClickListener selectionOnClickListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dynamicform_main);
        onSetContentViewFinish();
        Button updateButton = (Button) findViewById(R.id.dynamicFormUpdateButton);
        Button selectionButton = (Button) findViewById(R.id.dynamicFormSelectionButton);

        updateOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                Notifier.info(getClass(),
                        "Validando informacion antes de enviar mensaje.");

                entity = new DynamicFormService();

                if (!startUserMark()) {
                    return;
                }

                Notifier.info(getClass(),
                        "Se obtiene patron de mensajes y se crea el mensaje de texto");
                String msg = MessageFormat.format(
                        serviceEvent.getMessagePattern(), SERVICECOD);

                Notifier.info(getClass(),
                        "Se crean los datos de la entidad a ser enviada.");

                entity.setServiceCod(SERVICECOD);
                entity.setEvent(serviceEvent.getServiceEventCod());
                entity.setRequiresLocation(serviceEvent.getRequiresLocation());

                Notifier.info(getClass(), "Se creo la entidad a ser enviada");

                endUserMark(msg);
            }
        };

        selectionOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                startEventActivity(DynamicFormSelectionActivity.class);
            }
        };

        updateButton.setOnClickListener(updateOnClickListener);
        selectionButton.setOnClickListener(selectionOnClickListener);

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