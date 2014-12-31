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

public class VisitEndVisitActivity extends AbstractActivity {

    @Override
    public Integer getServicecod() {
        return 1;
    }

    @Override
    public String getServiceEventCod() {
        return "SAL";
    }

    private Button visitEndVisitButton;
    private EditText visitEndMotive;
    private EditText visitEndObservation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visit_end_main);
        onSetContentViewFinish();

        /*
         * obtenemos el los componentes de la pantalla para validar sus valores
         */
        visitEndMotive = (EditText) findViewById(R.id.visitEndVisitMotive);
        visitEndObservation = (EditText) findViewById(R.id.visitEndVisitObservation);
        visitEndVisitButton = (Button) findViewById(R.id.visitEndVisitButton);
        visitEndVisitButton.setOnClickListener(new View.OnClickListener() {

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
                        visitEndMotive.getText(), visitEndObservation.getText());

                ((VisitService) entity).setEvent(serviceEvent.getServiceEventCod());
                ((VisitService) entity).setMotiveCode(visitEndMotive.getText().toString());
                if (!TextUtils.isEmpty(visitEndObservation.getText())) {
                    ((VisitService) entity).setObservation(visitEndObservation.getText().toString());
                }

                endUserMark(msg);
            }
        });

    }

    @Override
    protected boolean validateUserInput() {
        /*
         * validamos que el usuario haya completado el campo codigo de cliente
         */
        if (TextUtils.isEmpty(visitEndMotive.getText())) {
            Toast.makeText(
                    VisitEndVisitActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.visit_end_motive_not_empty),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

}
