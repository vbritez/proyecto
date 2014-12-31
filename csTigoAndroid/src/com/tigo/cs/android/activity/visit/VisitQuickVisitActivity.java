package com.tigo.cs.android.activity.visit;

import java.text.MessageFormat;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.AbstractActivity;
import com.tigo.cs.api.entities.VisitService;

public class VisitQuickVisitActivity extends AbstractActivity {

    private static final Integer SERVICECOD = 1;
    private static final String SERVICEEVENTCOD = "ENTSAL";

    @Override
    public Integer getServicecod() {
        return SERVICECOD;
    }

    @Override
    public String getServiceEventCod() {
        return SERVICEEVENTCOD;
    }

    private EditText visitQuickClientCod;
    private EditText visitQuickMotive;
    private EditText visitQuickObservation;
    private String clientCod = "";
    private Bundle extras = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visit_quick_main);
        onSetContentViewFinish();

        extras = getIntent().getExtras();
        if (extras != null) {
            clientCod = (String) extras.get("client_cod");
        }

        /*
         * obtenemos el los componentes de la pantalla para validar sus valores
         */
        visitQuickClientCod = (EditText) findViewById(R.id.visitQuickVisitClientCod);
        visitQuickMotive = (EditText) findViewById(R.id.visitQuickVisitMotive);
        visitQuickObservation = (EditText) findViewById(R.id.visitQuickVisitObservation);

        if (clientCod != null && !clientCod.equals("")) {
            visitQuickClientCod.setText(clientCod);
        }

        Button visitQuickVisitButton = (Button) findViewById(R.id.visitQuickVisitButton);
        visitQuickVisitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                entity = new VisitService();

                if (!startUserMark()) {
                    return;
                }

                String msg = MessageFormat.format(
                        serviceEvent.getMessagePattern(), SERVICECOD,
                        SERVICEEVENTCOD, visitQuickClientCod.getText(),
                        visitQuickMotive.getText(),
                        visitQuickObservation.getText());

                ((VisitService) entity).setEvent(serviceEvent.getServiceEventCod());
                ((VisitService) entity).setClientCode(visitQuickClientCod.getText().toString());
                ((VisitService) entity).setMotiveCode(visitQuickMotive.getText().toString());
                if (!TextUtils.isEmpty(visitQuickObservation.getText())) {
                    ((VisitService) entity).setObservation(visitQuickObservation.getText().toString());
                }

                endUserMark(msg);
            }
        });

    }

    @Override
    protected boolean validateUserInput() {
        if (TextUtils.isEmpty(visitQuickClientCod.getText())) {
            Toast.makeText(
                    VisitQuickVisitActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.visit_quick_code_not_empty),
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if (TextUtils.isEmpty(visitQuickMotive.getText())) {
            Toast.makeText(
                    VisitQuickVisitActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.visit_quick_motive_not_empty),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
