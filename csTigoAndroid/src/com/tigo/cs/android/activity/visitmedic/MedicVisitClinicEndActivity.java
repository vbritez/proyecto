package com.tigo.cs.android.activity.visitmedic;

import java.text.MessageFormat;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.AbstractActivity;
import com.tigo.cs.api.entities.VisitMedicService;

public class MedicVisitClinicEndActivity extends AbstractActivity {

    @Override
    public Integer getServicecod() {
        return 17;
    }

    @Override
    public String getServiceEventCod() {
        return "CE";
    }

    private EditText observation;
    private Button finishButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medic_visit_clinic_end_main);
        onSetContentViewFinish();

        /*
         * obtenemos el los componentes de la pantalla para validar sus valores
         */
        observation = (EditText) findViewById(R.id.clinicEndObservation);

        finishButton = (Button) findViewById(R.id.clinicEndButton);
        finishButton.setOnClickListener(new View.OnClickListener() {

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
                        observation.getText());

                ((VisitMedicService) entity).setEvent(serviceEvent.getServiceEventCod());
                if (!TextUtils.isEmpty(observation.getText())) {
                    ((VisitMedicService) entity).setObservation(observation.getText().toString());
                }

                endUserMark(msg);
            }
        });

    }

    @Override
    protected boolean validateUserInput() {
        return true;
    }
}
