package com.tigo.cs.android.activity.visitmedic;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.AbstractActivity;

public class MedicVisitActivity extends AbstractActivity {

    private OnClickListener clinicStartOnClickListener;
    private OnClickListener clinicEndOnClickListener;
    private OnClickListener medicStartOnClickListener;
    private OnClickListener medicEndOnClickListener;
    private OnClickListener clinicQuickOnClickListener;
    private OnClickListener productRegisterOnClickListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medic_visit_main);
        onSetContentViewFinish();

        Button clinicStartButton = (Button) findViewById(R.id.clinicStartButton);
        Button clinicEndButton = (Button) findViewById(R.id.clinicEndButton);
        Button medicStartButton = (Button) findViewById(R.id.medicStartButton);
        Button medicEndButton = (Button) findViewById(R.id.medicEndButton);
        Button clinicQuickButton = (Button) findViewById(R.id.clinicQuickButton);
        Button productRegisterButton = (Button) findViewById(R.id.productRegisterButton);

        clinicStartOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                startEventActivity(MedicVisitClinicStartActivity.class);

            }
        };
        clinicEndOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                startEventActivity(MedicVisitClinicEndActivity.class);

            }
        };
        medicStartOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                startEventActivity(MedicVisitMedicStartActivity.class);

            }
        };

        medicEndOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                startEventActivity(MedicVisitMedicEndActivity.class);

            }
        };
        clinicQuickOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                startEventActivity(MedicVisitClinicQuickActivity.class);

            }
        };
        productRegisterOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                startEventActivity(MedicVisitProductRegisterActivity.class);

            }
        };

        clinicStartButton.setOnClickListener(clinicStartOnClickListener);
        clinicEndButton.setOnClickListener(clinicEndOnClickListener);
        medicStartButton.setOnClickListener(medicStartOnClickListener);
        medicEndButton.setOnClickListener(medicEndOnClickListener);
        clinicQuickButton.setOnClickListener(clinicQuickOnClickListener);
        productRegisterButton.setOnClickListener(productRegisterOnClickListener);
    }

}
