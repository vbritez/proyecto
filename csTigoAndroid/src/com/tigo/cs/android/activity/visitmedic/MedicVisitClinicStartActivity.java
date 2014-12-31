package com.tigo.cs.android.activity.visitmedic;

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
import com.tigo.cs.api.entities.VisitMedicService;

public class MedicVisitClinicStartActivity extends AbstractActivity {

    @Override
    public Integer getServicecod() {
        return 17;
    }

    @Override
    public String getServiceEventCod() {
        return "CS";
    }

    private EditText clientCode;
    private EditText initialKm;
    private EditText observation;
    private Button startButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medic_visit_clinic_start_main);
        onSetContentViewFinish();

        /*
         * obtenemos el los componentes de la pantalla para validar sus valores
         */
        clientCode = (EditText) findViewById(R.id.clinicStartClientCode);
        initialKm = (EditText) findViewById(R.id.initialKm);
        observation = (EditText) findViewById(R.id.clinicStartObservation);

        startButton = (Button) findViewById(R.id.clinicStartVisitButton);
        startButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                entity = new VisitMedicService();

                if (!startUserMark()) {
                    return;
                }

                String msg = MessageFormat.format(
                        serviceEvent.getMessagePattern(),
                        service.getServicecod(),
                        serviceEvent.getServiceEventCod(),
                        clientCode.getText(), initialKm.getText(),
                        observation.getText());

                ((VisitMedicService) entity).setEvent(serviceEvent.getServiceEventCod());
                ((VisitMedicService) entity).setPlaceCode(clientCode.getText().toString());
                if (!TextUtils.isEmpty(initialKm.getText())) {
                    ((VisitMedicService) entity).setInitialKm(initialKm.getText().toString());
                }
                if (!TextUtils.isEmpty(observation.getText())) {
                    ((VisitMedicService) entity).setObservation(observation.getText().toString());
                }

                endUserMark(msg);
            }
        });

    }

    @Override
    protected boolean validateUserInput() {
        if (TextUtils.isEmpty(clientCode.getText())) {
            Toast.makeText(
                    MedicVisitClinicStartActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.medic_clinic_code_not_empty),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
