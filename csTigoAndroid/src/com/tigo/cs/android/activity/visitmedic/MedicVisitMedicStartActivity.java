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

public class MedicVisitMedicStartActivity extends AbstractActivity {

    @Override
    public Integer getServicecod() {
        return 17;
    }

    @Override
    public String getServiceEventCod() {
        return "MS";
    }

    private EditText medicCode;
    private EditText observation;
    private Button startButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medic_visit_medic_start_main);
        onSetContentViewFinish();

        /*
         * obtenemos el los componentes de la pantalla para validar sus valores
         */
        medicCode = (EditText) findViewById(R.id.medicCode);
        observation = (EditText) findViewById(R.id.medicStartObservation);

        startButton = (Button) findViewById(R.id.medicStartVisitButton);
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
                        serviceEvent.getServiceEventCod(), medicCode.getText(),
                        observation.getText());

                ((VisitMedicService) entity).setEvent(serviceEvent.getServiceEventCod());
                ((VisitMedicService) entity).setMedicCode(medicCode.getText().toString());
                if (!TextUtils.isEmpty(observation.getText())) {
                    ((VisitMedicService) entity).setObservation(observation.getText().toString());
                }
                entity.setRequiresLocation(false);

                endUserMark(msg);
            }
        });

    }

    @Override
    protected boolean validateUserInput() {
        if (TextUtils.isEmpty(medicCode.getText())) {
            Toast.makeText(
                    MedicVisitMedicStartActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.medic_clinic_medic_not_empty),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

}
