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

public class VisitStartVisitActivity extends AbstractActivity {

    @Override
    public Integer getServicecod() {
        return 1;
    }

    @Override
    public String getServiceEventCod() {
        return "ENT";
    }

    private EditText visitStartClientCode;
    private EditText visitStartObservation;
    private Button visitStartVisitButton;
    private String clientCod;
    private Bundle extras;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visit_start_main);
        onSetContentViewFinish();

        extras = getIntent().getExtras();
        if (extras != null) {
            clientCod = (String) extras.get("client_cod");
        }

        /*
         * obtenemos el los componentes de la pantalla para validar sus valores
         */
        visitStartClientCode = (EditText) findViewById(R.id.visitStartVisitClientCode);
        visitStartObservation = (EditText) findViewById(R.id.visitStartVisitObservation);

        if (clientCod != null && !clientCod.equals("")) {
            visitStartClientCode.setText(clientCod);
        }

        visitStartVisitButton = (Button) findViewById(R.id.visitStartVisitButton);
        visitStartVisitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                entity = new VisitService();

                if (!startUserMark()) {
                    return;
                }

                String msg = MessageFormat.format(
                        serviceEvent.getMessagePattern(),
                        service.getServicecod(),
                        serviceEvent.getServiceEventCod(),
                        visitStartClientCode.getText(),
                        visitStartObservation.getText());

                ((VisitService) entity).setEvent(serviceEvent.getServiceEventCod());
                ((VisitService) entity).setClientCode(visitStartClientCode.getText().toString());
                if (!TextUtils.isEmpty(visitStartObservation.getText())) {
                    ((VisitService) entity).setObservation(visitStartObservation.getText().toString());
                }

                endUserMark(msg);
            }
        });

    }

    @Override
    protected boolean validateUserInput() {
        if (TextUtils.isEmpty(visitStartClientCode.getText())) {
            Toast.makeText(
                    VisitStartVisitActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.visit_start_code_not_empty),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
